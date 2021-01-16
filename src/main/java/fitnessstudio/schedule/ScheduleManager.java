package fitnessstudio.schedule;

import fitnessstudio.properties.FitnessstudioProperties;
import fitnessstudio.schedule.customer.suspension.Suspension;
import fitnessstudio.schedule.customer.suspension.SuspensionForm;
import fitnessstudio.schedule.customer.suspension.SuspensionRepository;
import fitnessstudio.schedule.customer.training.Training;
import fitnessstudio.schedule.customer.training.TrainingForm;
import fitnessstudio.schedule.customer.training.TrainingRepository;
import fitnessstudio.schedule.customer.trial.TrialRequest;
import fitnessstudio.schedule.customer.trial.TrialRequestForm;
import fitnessstudio.schedule.customer.trial.TrialRequestRepository;
import fitnessstudio.schedule.employee.holiday.Holiday;
import fitnessstudio.schedule.employee.holiday.HolidayForm;
import fitnessstudio.schedule.employee.holiday.HolidayRepository;
import fitnessstudio.schedule.employee.shift.Shift;
import fitnessstudio.schedule.employee.shift.ShiftForm;
import fitnessstudio.schedule.employee.shift.ShiftRepository;
import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Manager class for the websites schedule. It manages all database-interaction occurring from user-manipulation of
 * {@link Shift}, {@link Holiday}, {@link Training}, {@link Suspension} and {@link TrialRequest}.
 */
@Service
@Transactional
public class ScheduleManager {

    private final ShiftRepository shifts;
    private final TrainingRepository trainings;
    private final HolidayRepository holidayRequests;
    private final SuspensionRepository suspensions;
    private final TrialRequestRepository trialRequests;

    private final FitnessstudioProperties properties;

    private final UserManager userManager;

    /**
     * constructs a new {@link ScheduleManager} with the given parameters
     *
     * @param shifts {@link ShiftRepository} for database-interaction
     * @param trainings {@link TrainingRepository} for database-interaction
     * @param holidayRequests {@link HolidayRepository} for database-interaction
     * @param suspensions {@link SuspensionRepository} for database-interaction
     * @param trialRequests {@link TrialRequestRepository} for database-interaction
     * @param properties applications {@link FitnessstudioProperties}
     * @param userManager {@link UserManager} to get {@link User} information
     */
    public ScheduleManager(ShiftRepository shifts, TrainingRepository trainings,
                           HolidayRepository holidayRequests, SuspensionRepository suspensions,
                           TrialRequestRepository trialRequests, FitnessstudioProperties properties,
                           UserManager userManager) {
        this.shifts = shifts;
        this.trainings = trainings;
        this.holidayRequests = holidayRequests;
        this.suspensions = suspensions;
        this.trialRequests = trialRequests;
        this.properties = properties;
        this.userManager = userManager;
    }

    // Shifts

    /**
     * Creates or updates a {@link Shift} inside the schedule
     *
     * @param form {@link ShiftForm} to build the {@link Shift} from
     * @param id id of the {@link Shift} to edit (insert 'null' for creation)
     * @return the saved {@link Shift}
     */
    public Shift setShift(ShiftForm form, @Nullable Long id) {
        LocalDateTime start = LocalDate.parse(form.getStartDate()).atTime(form.getStartTime());
        LocalDateTime end = LocalDate.parse(form.getEndDate()).atTime(form.getEndTime());
        Optional<Employee> employee = userManager.getEmployees().findById(form.getEmployeeId());

        if (employee.isEmpty()) {
            return null;
        }

        Shift retShift;
        if (id == null) {
            // Create Shift
            retShift = shifts.save(new Shift(start, end, employee.get(), form.getDepartment()));
        } else {
            // Update Shift
            Optional<Shift> shift = shifts.findById(id);
            if (shift.isEmpty()) {
                return null;
            }
            shift.get().setStart(start);
            shift.get().setEnd(end);
            shift.get().setEmployee(employee.get());
            for (Department department : Department.values()) {
                if (department.equals(form.getDepartment())) {
                    shift.get().setDepartment(department);
                    break;
                }
            }
            retShift = shifts.save(shift.get());
        }
        return retShift;
    }

    /**
     * Removes a {@link Shift} from the schedule
     *
     * @param id the id of the {@link Shift} to be removed
     */
    public void removeShift(Long id) {
        shifts.deleteById(id);
    }

    // Holiday Requests

    /**
     * Creates a Holiday inside the schedule
     *
     * @param form {@link HolidayForm} to build the {@link Holiday} from
     * @return the saved {@link Holiday}
     */
    public Holiday addHolidayRequest(HolidayForm form) {
        LocalDate start = LocalDate.parse(form.getStartDate());
        LocalDate end = LocalDate.parse(form.getEndDate());
        Optional<Employee> employee = userManager.getEmployees().findById(form.getEmployeeId());

        if (employee.isEmpty()) {
            return null;
        }

        return holidayRequests.save(new Holiday(start, end, employee.get(), RequestStatus.PENDING));
    }

    /**
     * Sets the status of a {@link Holiday}
     *
     * @param id the id of the {@link Holiday}s status to edit
     * @param status new {@link RequestStatus}
     */
    public void setHolidayRequestStatus(Long id, RequestStatus status) {
        Optional<Holiday> h = holidayRequests.findById(id);
        if (h.isEmpty()) {
            return;
        }
        h.get().setStatus(status);
    }

    /**
     * Removes a {@link Holiday} from the schedule
     *
     * @param id the id of the {@link Holiday} to be removed
     */
    public void removeHolidayRequest(Long id) {
        holidayRequests.deleteById(id);
    }

    /**
     * Creates or updates a planned training session
     *
     * @param form {@link TrainingForm} to build the {@link Training} from
     * @param id id of the {@link Training} to edit (insert 'null' for creation)
     * @return updated Training
     */
    public Training setTraining(TrainingForm form, @Nullable Long id) {
        LocalDateTime start = LocalDate.parse(form.getStartDate()).atTime(form.getStartTime());
        LocalDateTime end = LocalDate.parse(form.getEndDate()).atTime(form.getEndTime());
        Optional<Customer> customer = userManager.getCustomers().findById(form.getCustomerId());

        if (customer.isEmpty()) {
            return null;
        }

        Training retTraining;
        if (id == null) {
            // Create Training
            retTraining = trainings.save(new Training(start, end, customer.get()));
        } else {
            // Update Training
            Optional<Training> training = trainings.findById(id);
            if (training.isEmpty()) {
                return null;
            }
            training.get().setStart(start);
            training.get().setEnd(end);
            training.get().setCustomer(customer.get());
            retTraining =  trainings.save(training.get());
        }
        return retTraining;
    }

    /**
     * Removes a {@link Training} from the schedule
     *
     * @param id the id of the {@link Training} to be removed
     */
    public void removeTraining(Long id) {
        trainings.deleteById(id);
    }

    // Suspensions

    /**
     * Creates a {@link Suspension} inside the Schedule
     *
     * @param form {@link SuspensionForm} to build the {@link Suspension} form
     * @return the saved {@link Suspension}
     */
    public Suspension addSuspension(SuspensionForm form) {
        LocalDateTime start = LocalDate.parse(form.getStartDate()).atTime(form.getStartTime());
        Optional<Customer> customer = userManager.getCustomers().findById(form.getCustomerId());
        if (customer.isEmpty()) {
            return null;
        }
        return suspensions.save(new Suspension(start, customer.get()));
    }

    /**
     * Removes a {@link Suspension} form the schedule
     *
     * @param id the id of the {@link Suspension} to be removed
     */
    public void removeSuspension(Long id) {
        suspensions.deleteById(id);
    }

    // Trial Requests

    /**
     * Creates a {@link TrialRequest} inside the schedule
     *
     * @param form {@link TrialRequestForm} to build the {@link TrialRequest} from
     * @return the saved {@link TrialRequest}
     */
    public TrialRequest addTrialRequest(TrialRequestForm form) {
        LocalDateTime start = LocalDate.parse(form.getStartDate()).atTime(form.getStartTime());
        LocalDateTime end = LocalDate.parse(form.getEndDate()).atTime(form.getEndTime());
        Optional<Employee> employee = userManager.getEmployees().findById(form.getEmployeeId());
        Optional<Customer> customer = userManager.getCustomers().findById(form.getCustomerId());
        if (employee.isEmpty() || customer.isEmpty()) {
            return null;
        }
        return trialRequests.save(new TrialRequest(start, end, customer.get(), employee.get(), RequestStatus.PENDING));
    }

    /**
     * Sets the status of a {@link TrialRequest}
     *
     * @param id the id of the {@link TrialRequest}s status to edit
     * @param status new {@link RequestStatus}
     */
    public void setTrialRequestStatus(Long id, RequestStatus status) {
        Optional<TrialRequest> tr = trialRequests.findById(id);
        if (tr.isEmpty()) {
            return;
        }
        tr.get().setStatus(status);
    }

    /**
     * Removes a {@link TrialRequest} from the schedule
     *
     * @param id the id of the {@link TrialRequest} to be removed
     */
    public void removeTrialRequest(Long id) {
        trialRequests.deleteById(id);
    }

    // Repository Getters

    public ShiftRepository getShifts() {
        return shifts;
    }

    public HolidayRepository getHolidayRequests() {
        return holidayRequests;
    }

    public SuspensionRepository getSuspensions() {
        return suspensions;
    }

    public TrainingRepository getTrainings() {
        return trainings;
    }

    public TrialRequestRepository getTrialRequests() {
        return trialRequests;
    }

    // Properties Getter

    public  FitnessstudioProperties getProperties() {
        return properties;
    }
}