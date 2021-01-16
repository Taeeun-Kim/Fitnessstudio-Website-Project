package fitnessstudio.user;


import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.contracts.Contract;
import fitnessstudio.contracts.ContractManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.customer.CustomerForm;
import fitnessstudio.user.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
public class CustomerManagementTests extends AbstractIntegrationTests {

    @Mock
    private ContractManager contractManager;

    @Mock
    private CustomerRepository customers;

    @Mock
    private UserAccountManager userAccountManager;

    @InjectMocks
    private UserManager userManager;

    private CustomerForm cform;
    private CustomerForm.Profile pform;
    private Customer cus2;
    private Contract contract;

    @BeforeEach
    void setup(){
        UserAddress us = new UserAddress("St", "Nr", "01234", "Dresden");
        cus2 = new Customer(new UserAccount(), us, mock(Contract.class));
        cform = new CustomerForm("c@email.com", "firstname", "lastname", "123",
                "123","Street", "1", "01234", "Dresden", null,
                1L);
        pform = new CustomerForm.Profile("p@email.com", "john", "doe", "123", "456",
                "456", "Road", "9", "69420", "Leipzig");
        contract = new Contract("Gold", "Ist sehr teuer", 4.99f);
        when(customers.save(any())).then(i->i.getArgument(0));
        when(customers.findById((any()))).thenReturn(java.util.Optional.of(cus2));
        when(userAccountManager.create(any(), any(), any(), any())).thenReturn(new UserAccount());
        when(contractManager.findById(any())).thenReturn(Optional.of(contract));
    }

    @Test
    void registerCustomer(){
        Customer cus = userManager.createCustomer(cform);
        assertNotNull(cus);
    }

    @Test
    void editCustomer(){
        userManager.editCustomer(cus2, pform);
        Customer cus2copy = userManager.findCustomerById(cus2.getId()).orElseThrow();
        assertEquals(cus2.getAddress().getStreet(), pform.getStreet());
        assertEquals(cus2.getAddress().getCode(),pform.getCode());
        assertEquals(cus2.getAddress().getLocation(), pform.getLocation());
        assertEquals(cus2.getAddress().getNumber(), pform.getNumber());
    }

}
