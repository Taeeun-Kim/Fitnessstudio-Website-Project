package fitnessstudio.schedule.employee.shift;

import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.employee.Employee;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * {@link ScheduleEntry} class that represents an {@link Employee}s shift. Shifts are bound to a specific day.
 * In addition to that shifts for one {@link Employee} can't overlap.
 */
@Entity
public class Shift extends ScheduleEntry {

    @OneToOne
    private Employee employee;
    private Department department;

    @SuppressWarnings("unused")
    protected Shift() {}

    /**
     * constructs a new {@link Shift} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the shifts start
     * @param end {@link LocalDateTime} that represents the shifts end
     * @param employee the {@link Employee} who is working the shift
     * @param department {@link Department} the shift takes place in
     */
    public Shift(LocalDateTime start, LocalDateTime end, Employee employee, Department department) {
        super(start, end);
        this.employee = employee;
        this.department = department;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Department getDepartment() {
        return department;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
