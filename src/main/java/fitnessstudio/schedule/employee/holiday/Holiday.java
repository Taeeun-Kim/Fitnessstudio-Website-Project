package fitnessstudio.schedule.employee.holiday;

import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.employee.Employee;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * {@link ScheduleEntry} class that represents an {@link Employee}s holiday.
 */
@Entity
public class Holiday extends ScheduleEntry {

    @OneToOne
    private Employee employee;
    private RequestStatus status;

    @SuppressWarnings("unused")
    protected Holiday() {}

    /**
     * constructs a new {@link Holiday} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the holidays start
     * @param end {@link LocalDateTime} that represents the holidays end
     * @param employee the {@link Employee} taking the holiday
     * @param status {@link RequestStatus} indicating the holidays status
     */
    public Holiday(LocalDate start, LocalDate end, Employee employee, RequestStatus status) {
        super(LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(end, LocalTime.of(23,59)));
        this.employee = employee;
        this.status = status;
    }

    public Employee getEmployee() {
        return employee;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}