package fitnessstudio.schedule.employee.holiday;

import fitnessstudio.schedule.employee.EmployeeForm;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.User;
import fitnessstudio.user.employee.Employee;

/**
 * {@link ScheduleEntryForm} class that collects {@link User} input for {@link Holiday} creation.
 */
public class HolidayForm extends EmployeeForm {

    /**
     * constructs a new {@link HolidayForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param employeeId the id of the {@link Employee} taking the holiday
     */
    public HolidayForm(String startDate, String endDate, Long employeeId) {
        super(startDate, endDate, employeeId);
    }
}
