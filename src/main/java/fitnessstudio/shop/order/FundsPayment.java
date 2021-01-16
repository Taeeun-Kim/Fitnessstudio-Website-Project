package fitnessstudio.shop.order;

import fitnessstudio.user.customer.Customer;
import org.salespointframework.payment.PaymentMethod;

public class FundsPayment extends PaymentMethod {

    private final long customerId;

    public FundsPayment(Customer customer) {
        super(customer.getUserAccount().getEmail());
        this.customerId = customer.getId();
    }

    public long getCustomerId() {
        return customerId;
    }
}
