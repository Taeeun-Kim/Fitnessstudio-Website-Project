package fitnessstudio.statistics.record;

import fitnessstudio.user.customer.Customer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class CustomerActivity {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne
    private Customer customer;

    private LocalDateTime timestamp;

    @NotNull
    private Boolean direction;

    protected CustomerActivity() {}

    public CustomerActivity(Customer customer, LocalDateTime timestamp, Boolean direction) {
        this.customer = customer;
        this.timestamp = timestamp;
        this.direction = direction;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Boolean getDirection() {
        return direction;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setDirection(Boolean direction) {
        this.direction = direction;
    }
}
