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
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.customer.CustomerRepository;
import fitnessstudio.user.employee.Employee;
import fitnessstudio.user.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScheduleManagerTests {

    @Mock
    private ShiftRepository shifts;
    @Mock
    private HolidayRepository holidays;
    @Mock
    private TrainingRepository trainings;
    @Mock
    private SuspensionRepository suspensions;
    @Mock
    private TrialRequestRepository trials;

    @SuppressWarnings("unused")
    @Mock
    private FitnessstudioProperties properties;

    @Mock
    private UserManager userManager;
    @Mock
    private EmployeeRepository employees;
    @Mock
    private Employee employee;
    @Mock
    private CustomerRepository customers;
    @Mock
    private Customer customer;

    @InjectMocks
    private ScheduleManager scheduleManager;

    @Test
    void setShiftNotNull() {
        LocalDateTime now = LocalDateTime.now();
        ShiftForm sF = new ShiftForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalTime(), (long) 1, Department.TRAINING);
        when(userManager.getEmployees()).thenReturn(employees);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(shifts.save(any())).then(i -> i.getArgument(0));
        Shift savedShift = scheduleManager.setShift(sF, null);
        assertNotNull(savedShift);
    }

    @Test
    void setShiftHasRightProperties() {
        LocalDateTime now = LocalDateTime.now();
        ShiftForm sF = new ShiftForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalTime(), (long) 1, Department.TRAINING);
        when(userManager.getEmployees()).thenReturn(employees);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(shifts.save(any())).then(i -> i.getArgument(0));
        Shift savedShift = scheduleManager.setShift(sF, null);
        assertEquals(savedShift.getStart(), now);
    }

    @Test
    void setShiftSetsRightProperties() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        ShiftForm sF = new ShiftForm(tomorrow.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), tomorrow.toLocalTime(), tomorrow.toLocalTime(), (long) 1, Department.TRAINING);
        Shift s = new Shift(now, now, employee, Department.TRAINING);
        when(userManager.getEmployees()).thenReturn(employees);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(shifts.findById(any())).thenReturn(Optional.of(s));
        when(shifts.save(any())).then(i -> i.getArgument(0));
        Shift savedShift = scheduleManager.setShift(sF, (long) 1);
        assertEquals(savedShift.getStart(), tomorrow);
    }

    @Test
    void addHolidayRequestNotNull() {
        LocalDateTime now = LocalDateTime.now();
        HolidayForm hF = new HolidayForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), (long) 1);
        when(userManager.getEmployees()).thenReturn(employees);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(holidays.save(any())).then(i -> i.getArgument(0));
        Holiday savedHoliday = scheduleManager.addHolidayRequest(hF);
        assertNotNull(savedHoliday);
    }

    @Test
    void addHolidayRequestHasRightProperties() {
        LocalDateTime now = LocalDateTime.now();
        HolidayForm hF = new HolidayForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), (long) 1);
        when(userManager.getEmployees()).thenReturn(employees);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(holidays.save(any())).then(i -> i.getArgument(0));
        Holiday savedHoliday = scheduleManager.addHolidayRequest(hF);
        assertEquals(savedHoliday.getStart().toLocalDate(), now.toLocalDate());
    }

    @Test
    void setHolidayRequestStatusSetsRightStatus() {
        LocalDateTime now = LocalDateTime.now();
        Holiday h = new Holiday(now.toLocalDate(), now.toLocalDate(), employee, RequestStatus.PENDING);
        when(holidays.findById(any())).thenReturn(Optional.of(h));
        scheduleManager.setHolidayRequestStatus((long) 1, RequestStatus.ACCEPTED);
        assertEquals(h.getStatus(), RequestStatus.ACCEPTED);
    }

    @Test
    void setTrainingNotNull() {
        LocalDateTime now = LocalDateTime.now();
        TrainingForm tF = new TrainingForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalTime(), (long) 1);
        when(userManager.getCustomers()).thenReturn(customers);
        when(customers.findById(any())).thenReturn(Optional.of(customer));
        when(trainings.save(any())).then(i -> i.getArgument(0));
        Training savedTraining = scheduleManager.setTraining(tF, null);
        assertNotNull(savedTraining);
    }

    @Test
    void setTrainingHasRightProperties() {
        LocalDateTime now = LocalDateTime.now();
        TrainingForm tF = new TrainingForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalTime(), (long) 1);
        when(userManager.getCustomers()).thenReturn(customers);
        when(customers.findById(any())).thenReturn(Optional.of(customer));
        when(trainings.save(any())).then(i -> i.getArgument(0));
        Training savedTraining = scheduleManager.setTraining(tF, null);
        assertEquals(savedTraining.getStart(), now);
    }

    @Test
    void setTrainingSetsRightProperties() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        TrainingForm tF = new TrainingForm(tomorrow.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), tomorrow.toLocalTime(), tomorrow.toLocalTime(), (long) 1);
        Training t = new Training(now, now, customer);
        when(userManager.getCustomers()).thenReturn(customers);
        when(customers.findById(any())).thenReturn(Optional.of(customer));
        when(trainings.findById(any())).thenReturn(Optional.of(t));
        when(trainings.save(any())).then(i -> i.getArgument(0));
        Training savedTraining = scheduleManager.setTraining(tF, (long) 1);
        assertEquals(savedTraining.getStart(), tomorrow);
    }

    @Test
    void addSuspensionNotNull()  {
        LocalDateTime now = LocalDateTime.now();
        SuspensionForm sF = new SuspensionForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), (long) 1);
        when(userManager.getCustomers()).thenReturn(customers);
        when(customers.findById(any())).thenReturn(Optional.of(customer));
        when(suspensions.save(any())).then(i -> i.getArgument(0));
        Suspension savedSuspension = scheduleManager.addSuspension(sF);
        assertNotNull(savedSuspension);
    }

    @Test
    void addTrialRequestNotNull() {
        LocalDateTime now = LocalDateTime.now();
        TrialRequestForm trF = new TrialRequestForm(now.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), now.toLocalTime(), now.toLocalTime(), (long) 1, (long) 1, RequestStatus.PENDING);
        when(userManager.getEmployees()).thenReturn(employees);
        when(userManager.getCustomers()).thenReturn(customers);
        when(employees.findById(any())).thenReturn(Optional.of(employee));
        when(customers.findById(any())).thenReturn(Optional.of(customer));
        when(trials.save(any())).then(i -> i.getArgument(0));
        TrialRequest savedTrialRequest = scheduleManager.addTrialRequest(trF);
        assertNotNull(savedTrialRequest);
    }

    @Test
    void setTrialRequestStatusSetsRightStatus() {
        LocalDateTime now = LocalDateTime.now();
        TrialRequest tr = new TrialRequest(now, now, customer, employee, RequestStatus.PENDING);
        when(trials.findById(any())).thenReturn(Optional.of(tr));
        scheduleManager.setTrialRequestStatus((long) 1, RequestStatus.ACCEPTED);
        assertEquals(tr.getStatus(), RequestStatus.ACCEPTED);
    }



}
