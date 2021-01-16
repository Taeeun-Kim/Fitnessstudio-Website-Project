package fitnessstudio.statistics.record;

import fitnessstudio.user.customer.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerActivityTests {

    private CustomerActivity activity;

    private Customer customer;
    private LocalDateTime timestamp;

    @BeforeEach
    public void init() {
        this.customer = Mockito.mock(Customer.class);
        this.timestamp = LocalDateTime.of(2020, 1, 1, 12, 0);

        this.activity = new CustomerActivity(customer, timestamp, true);
    }

    @Test
    public void testGetCustomer() {
        assertEquals(this.customer, this.activity.getCustomer());
    }

    @Test
    void testGetTimestamp() {
        assertEquals(this.timestamp, this.activity.getTimestamp());
    }

    @Test
    void testGetDirection() {
        assertEquals(true, this.activity.getDirection());
    }

    @Test
    void setCustomer() {
        Customer customer = Mockito.mock(Customer.class);
        this.activity.setCustomer(customer);
        assertEquals(customer, this.activity.getCustomer());
    }

    @Test
    void setTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2012, 12, 12, 12, 12);
        this.activity.setTimestamp(timestamp);
        assertEquals(timestamp, this.activity.getTimestamp());
    }

    @Test
    void setDirection() {
        this.activity.setDirection(false);
        assertEquals(false, this.activity.getDirection());
    }
}