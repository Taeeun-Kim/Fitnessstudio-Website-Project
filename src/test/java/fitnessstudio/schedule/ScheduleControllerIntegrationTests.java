package fitnessstudio.schedule;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class ScheduleControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Test
    void showsLoginOnUnauthorized() throws Exception {
        // var obama = status();
        // obama.isGone()
        mvc.perform(get("/schedule")).andExpect(status().isFound()).andExpect(header().string(HttpHeaders.LOCATION, endsWith("/login")));
    }

    @Test
    @WithUserDetails("boss@email.com")
    void showsScheduleOnAuthorized() throws Exception {
        mvc.perform(get("/schedule")).andExpect(status().isOk()).andExpect(model().attributeExists("employeeId"));
    }

    @Test
    @WithMockUser(username="customer", roles="CUSTOMER")
    void addShiftForbiddenForCustomer() throws Exception {
        mvc.perform(post("/schedule/shift")).andExpect(status().isForbidden());
    }
}
