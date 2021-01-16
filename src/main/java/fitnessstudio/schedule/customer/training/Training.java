package fitnessstudio.schedule.customer.training;

import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.customer.Customer;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * {@link ScheduleEntry} class that represents a {@link Customer}s training. Trainings are bound to a specific day and
 * to the opening hours of the fitnessstudio. In addition to that trainings for one {@link Customer} can't overlap.
 */
@Entity
public class Training extends ScheduleEntry {

    @OneToOne
    private Customer customer;

    @SuppressWarnings("unused")
    protected Training() {}

    /**
     * constructs a new {@link Training} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the trainings start
     * @param end {@link LocalDateTime} that represents the trainings end
     * @param customer the {@link Customer} who is participating in this training
     */
    public Training(LocalDateTime start, LocalDateTime end, Customer customer) {
        super(start, end);
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
