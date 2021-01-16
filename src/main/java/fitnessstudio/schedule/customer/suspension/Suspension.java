package fitnessstudio.schedule.customer.suspension;

import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.customer.Customer;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 * {@link ScheduleEntry} class that represents a {@link Customer}s suspension. Suspensions have a duration of exactly
 * one month and prevent the {@link Customer} from logging in to the website. During this time the {@link Customer}s
 * membership is paused. Suspensions can be taken once per year.
 */
@Entity
public class Suspension extends ScheduleEntry {

    @OneToOne
    private Customer customer;

    @SuppressWarnings("unused")
    protected Suspension() {}

    /**
     * constructs a new {@link Suspension} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the suspensions start
     * @param customer the {@link Customer} to be suspended
     */
    public Suspension(LocalDateTime start, Customer customer) {
        super(start, start.plusMonths(1));
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
