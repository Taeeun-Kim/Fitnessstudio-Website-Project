package fitnessstudio.statistics;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class StatisticsControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Test
    @WithMockUser(roles = "BOSS")
    public void testStatistics() throws Exception {
        mvc.perform(get("/statistics"))
            .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "BOSS")
    public void testCustomer() throws Exception {
        mvc.perform(get("/statistics/customer"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("records"));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    public void testSales() throws Exception {
        mvc.perform(get("/statistics/sales"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("records"));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    public void testRevenue() throws Exception {
        mvc.perform(get("/statistics/revenue"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("records"));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    public void testCosts() throws Exception {
        mvc.perform(get("/statistics/costs"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("records"));
    }
}