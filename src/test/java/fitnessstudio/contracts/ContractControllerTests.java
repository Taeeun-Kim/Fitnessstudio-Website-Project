package fitnessstudio.contracts;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class ContractControllerTests extends AbstractIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void foundContractLoginPage() throws Exception {
        mvc.perform(get("/contracts")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Verträge")));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void showContractedManagementPage() throws Exception {
        mvc.perform(get("/admin/contracts")).andExpect(status().isOk())
                .andExpect(model().attributeExists("contracts"));
    }

    @Test
    @WithMockUser(username="employee", roles="EMPLOYEE")
    void showContractManagementPageForEmployeeForbidden() throws Exception {
        mvc.perform(get("/admin/contracts")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username="customer", roles="CUSTOMER")
    void showContractManagementPageForCustomerForbidden() throws Exception {
        mvc.perform(get("/admin/contracts")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void addContractAllowed() throws Exception {
        mvc.perform(get("/admin/contracts/add")).andExpect(status().isOk())
                .andExpect(model().attributeExists("contractForm"));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void addContractSuccessful() throws Exception {
        mvc.perform(post("/admin/contracts/add").with(csrf()).param("title", "Bronze")
                .param("description", "Akzeptabel").param("fee", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/admin/contracts")));
        mvc.perform(get("/admin/contracts"))
                .andExpect(content().string(containsString("Bronze")));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void addExistingContract() throws Exception {
        mvc.perform(post("/admin/contracts/add").with(csrf()).param("title", "Gold")
                .param("description", "Toll").param("fee", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void editContractAllowed() throws Exception {
        mvc.perform(get("/admin/contracts/1")).andExpect(status().isOk())
                .andExpect(model().attributeExists("contractForm"));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void editContractSuccessful() throws Exception {
        mvc.perform(post("/admin/contracts/1").with(csrf()).param("title", "Diamond")
                .param("description", "Toll").param("fee", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/admin/contracts")));
    }

    @Test
    @WithMockUser(roles = "BOSS")
    void deleteContractSuccessful() throws Exception {
        mvc.perform(post("/admin/contracts/2/delete").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, endsWith("/admin/contracts")));
    }
}
