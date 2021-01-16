package fitnessstudio.schedule.employee.shift;

import fitnessstudio.schedule.employee.EmployeeForm;
import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.User;
import fitnessstudio.user.employee.Employee;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that collects {@link User} input for {@link Shift} creation.
 */
public class ShiftForm extends EmployeeForm {

    @NotNull(message = "Bereich darf nicht leer sein")
    private final Department department;

    /**
     * constructs a new {@link ShiftForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endTime {@link LocalTime} that represents the end time
     * @param employeeId the id of the {@link Employee} who is working the {@link Shift}
     * @param department {@link Department} the {@link Shift} is going to take place in
     */
    public ShiftForm(String startDate, LocalTime startTime, LocalTime endTime, Long employeeId,
                     Department department) {
        super(startDate, startTime, startDate, endTime, employeeId);
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }
}
