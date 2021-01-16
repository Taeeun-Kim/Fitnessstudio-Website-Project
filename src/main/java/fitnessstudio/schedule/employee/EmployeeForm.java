package fitnessstudio.schedule.employee;

import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.employee.Employee;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that is parent-class for {@link ScheduleEntryForm}s regarding the {@link Employee}.
 */
public abstract class EmployeeForm extends ScheduleEntryForm {

    @NotNull(message = "ID des Mitarbeiters darf nicht leer sein")
    private final Long employeeId;

    /**
     * constructs a new {@link EmployeeForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param endTime {@link LocalTime} that represents the end time
     * @param employeeId the id of the {@link Employee}
     */
    public EmployeeForm(String startDate, LocalTime startTime, String endDate, LocalTime endTime, Long employeeId) {
        super(startDate, startTime, endDate, endTime);
        this.employeeId = employeeId;
    }

    /**
     * constructs a new {@link EmployeeForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param employeeId the id of the {@link Employee}
     */
    public EmployeeForm(String startDate, String endDate, Long employeeId) {
        super(startDate, LocalTime.MIN, endDate, LocalTime.MAX);
        this.employeeId = employeeId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
}
