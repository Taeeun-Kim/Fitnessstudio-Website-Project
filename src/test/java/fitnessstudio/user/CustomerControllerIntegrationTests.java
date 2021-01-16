package fitnessstudio.user;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.user.customer.CustomerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class CustomerControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    CustomerController customerController;

    @Test
    void rejectsUnauthenticatedAccessToController(){
        assertThatExceptionOfType(AuthenticationException.class).//
         isThrownBy(()->customerController.customers(new ExtendedModelMap()));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void testCustomers() throws Exception {
        mvc.perform(get("/customers")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("franz@email.com")
    void testProfileCustomer() throws Exception {
        mvc.perform(get("/profile")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("boss@email.com")
    void testProfileEmployee() throws Exception {
        mvc.perform(get("/profile")).andExpect(status().isOk());
    }

}
