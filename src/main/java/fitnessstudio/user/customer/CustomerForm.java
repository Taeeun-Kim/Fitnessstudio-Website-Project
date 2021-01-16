package fitnessstudio.user.customer;

import fitnessstudio.user.UserForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class CustomerForm extends UserForm.Register {

    private Long promo;

    @NotNull @PositiveOrZero
    private Long contractId;

    public CustomerForm(String email, String firstname, String lastname, String password, String repeatPassword,
                        String street, String number, String code, String location, Long promo, Long contractId) {
        super(email, firstname, lastname, password, repeatPassword, street, number, code, location);
        this.promo = promo;
        this.contractId = contractId;
    }

    public Long getPromo() {
        return promo;
    }

    public Long getContractId() {
        return contractId;
    }
}
