package fitnessstudio.shop.order;

import fitnessstudio.user.customer.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.salespointframework.useraccount.UserAccount;

import static org.junit.jupiter.api.Assertions.*;

class FundsPaymentTests {

    @Test
    public void testCustomerId() {
        Customer customer = Mockito.mock(Customer.class);
        UserAccount userAccount = Mockito.mock(UserAccount.class);


        Mockito.when(customer.getId()).thenReturn(123456L);
        Mockito.when(customer.getUserAccount()).thenReturn(userAccount);

        Mockito.when(userAccount.getEmail()).thenReturn("test@example.com");

        FundsPayment fundsPayment = new FundsPayment(customer);

        assertEquals(123456L, fundsPayment.getCustomerId());
    }

}