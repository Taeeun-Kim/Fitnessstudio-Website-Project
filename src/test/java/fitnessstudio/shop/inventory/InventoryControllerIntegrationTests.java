package fitnessstudio.shop.inventory;

import fitnessstudio.contracts.Contract;
import fitnessstudio.shop.catalog.FitnessstudioProduct;
import fitnessstudio.user.customer.Customer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerIntegrationTests {

    @Autowired MockMvc mvc;

    private FitnessstudioProduct product;
    private Contract contract;
    private Customer customer;

    @Test
    void preventsPublicAccessForStockOverview() throws Exception {

        mvc.perform(get("/shop/stock")) //
                .andExpect(status().isFound()) //
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));//
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAsEmployee() throws Exception {
        mvc.perform(get("/shop/stock"))
                .andExpect(status().isOk());
    }
}