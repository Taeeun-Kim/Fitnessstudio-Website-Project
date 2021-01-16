package fitnessstudio.schedule;

import com.mysema.commons.lang.Assert;
import fitnessstudio.schedule.customer.CustomerForm;
import fitnessstudio.schedule.customer.suspension.Suspension;
import fitnessstudio.schedule.customer.suspension.SuspensionForm;
import fitnessstudio.schedule.customer.training.Training;
import fitnessstudio.schedule.customer.training.TrainingForm;
import fitnessstudio.schedule.customer.trial.TrialRequest;
import fitnessstudio.schedule.customer.trial.TrialRequestForm;
import fitnessstudio.schedule.employee.EmployeeForm;
import fitnessstudio.schedule.employee.holiday.Holiday;
import fitnessstudio.schedule.employee.shift.Shift;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;
import org.salespointframework.time.Interval;
import org.springframework.data.util.Streamable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Helper class for {@link ScheduleEntry} validation. At generation of an instance it collects all
 * {@link ScheduleEntry}s that can be found in the corresponding repositories and saves them in a
 * {@link List}. Temporal limitation of these {@link ScheduleEntry}s is optional but possible.
 */
public class Schedule {

    private List<ScheduleEntry> entries;
    private ScheduleManager scheduleManager;
    private UserManager userManager;

    /**
     * constructs a new {@link Schedule} with the given parameters
     *
     * @param users {@link List} where the entries list is build from
     * @param scheduleManager {@link ScheduleManager} for database interaction
     * @param userManager {@link UserManager} to get {@link User} information
     * @param interval optional parameter that works as temporal limitation for {@link ScheduleEntry}s in the entries
     *                 list (insert '{@link Optional}.empty()' for no temporal limitation)
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public Schedule(List<User> users, ScheduleManager scheduleManager, UserManager userManager,
                    Optional<Interval> interval) {
        entries = new ArrayList<>();
        this.scheduleManager = scheduleManager;
        this.userManager = userManager;
        users.forEach(u -> {
            if (u instanceof Employee) {
                entries.addAll(scheduleManager.getShifts().findByEmployee((Employee) u).toList());
                entries.addAll(scheduleManager.getHolidayRequests().findByEmployee((Employee) u).toList());
                entries.addAll(scheduleManager.getTrialRequests().findByEmployee((Employee) u).toList());
            } else if (u instanceof Customer) {
                entries.addAll(scheduleManager.getTrainings().findByCustomer((Customer) u).toList());
                entries.addAll(scheduleManager.getSuspensions().findByCustomer((Customer) u).toList());
                entries.addAll(scheduleManager.getTrialRequests().findByCustomer((Customer) u).toList());
            }
        });
        entries.sort(Comparator.comparing(ScheduleEntry::getStart));
        interval.ifPresent(value -> entries = Streamable.of(entries)
                .filter(e -> value.contains(e.getStart())).toList());
    }

    /**
     * Validates whether a schedule can or cannot fit a possible {@link ScheduleEntry} suggested by a
     * {@link ScheduleEntryForm}. It takes opening hours as well as overlap of different {@link ScheduleEntry}s into
     * account.
     *
     * @param form {@link ScheduleEntryForm} to build the possible {@link ScheduleEntry} from
     * @return 'true' if the schedule can fit the entry
     */
    public boolean canFit(ScheduleEntryForm form) {
        Assert.notNull(form, "ScheduleEntryForm must not be null");
        boolean result = false;
        LocalDateTime start = LocalDate.parse(form.getStartDate()).atTime(form.getStartTime());
        LocalDateTime end = LocalDate.parse(form.getEndDate()).atTime(form.getEndTime());
        Interval interval = Interval.from(start).to(end);
        if (form instanceof EmployeeForm) {
            Optional<Employee> e = userManager.getEmployees().findById(((EmployeeForm) form).getEmployeeId());
            if (e.isPresent()) {
                result = (verifyShifts(e.get(), interval) && verifyHolidayRequests(e.get(), interval)
                        && verifyTrialRequests(e.get(), interval, false));
            }
        } else if (form instanceof CustomerForm) {
            Optional<Customer> c = userManager.getCustomers().findById(((CustomerForm) form).getCustomerId());
            if (c.isPresent()) {
                boolean isTrainingForm = (form instanceof TrainingForm);
                boolean isSuspensionForm = (form instanceof SuspensionForm);
                result = (verifyTrainings(c.get(), interval, isTrainingForm)
                        && verifySuspensions(c.get(), interval, isSuspensionForm)
                        && verifyTrialRequests(c.get(), interval, false));
            }
        } else if (form instanceof TrialRequestForm) {
            Optional<Employee> e = userManager.getEmployees().findById(((TrialRequestForm) form).getEmployeeId());
            Optional<Customer> c = userManager.getCustomers().findById(((TrialRequestForm) form).getCustomerId());
            if (e.isPresent() && c.isPresent()) {
                boolean verifyEmployee = (verifyShifts(e.get(), interval) && verifyHolidayRequests(e.get(), interval)
                        && verifyTrialRequests(e.get(), interval, true));
                boolean verifyCustomer = (verifyTrainings(c.get(), interval, false)
                        && verifySuspensions(c.get(), interval, false)
                        && verifyTrialRequests(c.get(), interval, true));
                result = (verifyEmployee && verifyCustomer);
            }
        }
        return result;
    }

    /**
     * Validates whether a schedule can or cannot fit an edited {@link ScheduleEntry} suggested by a
     * {@link ScheduleEntryForm}. It takes opening hours as well as overlap of different {@link ScheduleEntry}s into
     * account.
     *
     * @param form {@link ScheduleEntryForm} to build the possible {@link ScheduleEntry} from
     * @param id the id of the {@link ScheduleEntry} to be edited
     * @return 'true' if the schedule can fit the edited entry
     */
    public boolean canFitEdited(ScheduleEntryForm form, Long id) {
        entries.removeIf(e -> e.getId() == id);
        return canFit(form);
    }

    public Streamable<ScheduleEntry> getEntries() {
        return Streamable.of(entries);
    }

    /**
     * Helper-function to verify opening hours
     *
     * @param interval time interval of the {@link ScheduleEntry}
     * @return 'true' if the interval fits the opening hours
     */
    private boolean verifyBusinessHours(Interval interval) {
        DayOfWeek dow = interval.getStart().toLocalDate().getDayOfWeek();
        Map.Entry<LocalTime, LocalTime> day = scheduleManager.getProperties()
                .getBusinessHours().get(dow.getValue() - 1);
        if ((day.getKey() == null) || (day.getValue() == null)) {
            return false;
        }
        Interval businessDay = Interval.from(day.getKey().atDate(interval.getStart().toLocalDate()))
                .to(day.getValue().atDate(interval.getStart().toLocalDate()));
        return (businessDay.contains(interval.getStart()) && businessDay.contains(interval.getEnd()));
    }

    /**
     * Helper-function to verify {@link Shift}s
     *
     * @param e the {@link Employee} participating in the {@link ScheduleEntry}
     * @param interval time interval of the {@link ScheduleEntry}
     * @return 'true' if there are no {@link Shift}s of this {@link Employee} overlapping the {@link ScheduleEntry}
     */
    private boolean verifyShifts(Employee e, Interval interval) {
        List<ScheduleEntry> tmp = entries;
        tmp.removeIf(entry -> !((entry instanceof Shift) && (((Shift) entry).getEmployee().equals(e))));
        for (ScheduleEntry s : tmp) {
            if (interval.overlaps(Interval.from(s.getStart()).to(s.getEnd()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper-function to verify {@link Holiday}s
     *
     * @param e the {@link Employee} participating in the {@link ScheduleEntry}
     * @param interval time interval of the {@link ScheduleEntry}
     * @return 'true' if there are no {@link Holiday}s of this {@link Employee} overlapping the {@link ScheduleEntry}
     */
    private boolean verifyHolidayRequests(Employee e, Interval interval) {
        for (Holiday h : scheduleManager.getHolidayRequests().findByEmployee(e)
                .filter(hr -> !hr.getStatus().equals(RequestStatus.REJECTED))) {
            if (interval.overlaps(Interval.from(h.getStart()).to(h.getEnd()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper-function to verify {@link Training}s
     *
     * @param c the {@link Customer} participating in the {@link ScheduleEntry}
     * @param interval time interval of the {@link ScheduleEntry}
     * @param isTraining whether the {@link ScheduleEntry} is a {@link Training} or not
     * @return 'true' if there are no {@link Training}s of this {@link Customer} overlapping the {@link ScheduleEntry}
     *         and if its a {@link Training} whether it fits the opening hours or not
     */
    private boolean verifyTrainings(Customer c, Interval interval, boolean isTraining) {
        if ((isTraining) && (!verifyBusinessHours(interval))) {
            return false;
        }
        List<ScheduleEntry> tmp = entries;
        tmp.removeIf(entry -> !((entry instanceof Training) && (((Training) entry).getCustomer().equals(c))));
        for (ScheduleEntry t : tmp) {
            if (interval.overlaps(Interval.from(t.getStart()).to(t.getEnd()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper-function to verify {@link Suspension}s
     *
     * @param c the {@link Customer} participating in the {@link ScheduleEntry}
     * @param interval time interval of the {@link ScheduleEntry}
     * @param isSuspension whether the {@link ScheduleEntry} is a {@link Suspension} or not
     * @return 'true' if there are no {@link Suspension}s of this {@link Customer} overlapping the {@link ScheduleEntry}
     *         and if it is the first suspension in the current year
     */
    private boolean verifySuspensions(Customer c, Interval interval, boolean isSuspension) {
        for (Suspension s : scheduleManager.getSuspensions().findByCustomer(c)) {
            if (interval.overlaps(Interval.from(s.getStart()).to(s.getEnd()))) {
                return false;
            }
            if ((isSuspension) && (interval.getStart().getYear() == s.getStart().getYear())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper-function to verify {@link TrialRequest}s
     *
     * @param u the {@link User} participating in the {@link ScheduleEntry}
     * @param interval time interval of the {@link ScheduleEntry}
     * @param isTrialRequest whether the {@link ScheduleEntry} is a {@link TrialRequest} or not
     * @return 'true' if there are no {@link TrialRequest}s of this {@link User} overlapping the {@link ScheduleEntry}
     *         and if it the {@link User} is a {@link Customer} if it is his first {@link TrialRequest}
     */
    private boolean verifyTrialRequests(User u, Interval interval, boolean isTrialRequest) {
        boolean result = true;
        Streamable<TrialRequest> trials = Streamable.empty();
        if (u instanceof Employee) {
            trials = scheduleManager.getTrialRequests().findByEmployee((Employee) u);
        } else if (u instanceof Customer) {
            trials = scheduleManager.getTrialRequests().findByCustomer((Customer) u);
            boolean firstTrial = trials.isEmpty();
            if (isTrialRequest && !(firstTrial && verifyBusinessHours(interval))) {
                return false;
            }
        }
        for (TrialRequest t : trials.filter(tr -> !tr.getStatus().equals(RequestStatus.REJECTED))) {
            if (interval.overlaps(Interval.from(t.getStart()).to(t.getEnd()))) {
                result = false;
                break;
            }
        }
        return result;
    }
}