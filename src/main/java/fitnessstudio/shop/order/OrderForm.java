package fitnessstudio.shop.order;

import javax.validation.constraints.NotNull;

public class OrderForm {

    @NotNull
    private Long customer;

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }
}
