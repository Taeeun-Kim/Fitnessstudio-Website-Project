package fitnessstudio.schedule.customer.trial;

import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * {@link ScheduleEntry} class that represents a {@link Customer}s trial-request. Trials can be requested once per
 * {@link Customer} and need to be accepted by an {@link Employee} in order to take place. Trials are bound to a
 * specific day and to the opening hours of the fitnessstudio. During a trial a {@link Customer} is instructed by an
 * {@link Employee} acting as a trainer.
 */
@Entity
public class TrialRequest extends ScheduleEntry {

    @OneToOne
    private Customer customer;
    @OneToOne
    private Employee employee;
    private RequestStatus status;

    @SuppressWarnings("unused")
    protected TrialRequest() {}

    /**
     * constructs a new {@link TrialRequest} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the trials start
     * @param end {@link LocalDateTime} that represents the trials end
     * @param customer the {@link Customer} who is participating in this trial
     * @param employee the {@link Employee} who is participating in this trial
     * @param status {@link RequestStatus} indicating the trials status
     */
    public TrialRequest(LocalDateTime start, LocalDateTime end, Customer customer, Employee employee,
                        RequestStatus status) {
        super(start, end);
        this.customer = customer;
        this.employee = employee;
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}