package fitnessstudio.properties;

import fitnessstudio.AbstractIntegrationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class PropertiesControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    MockMvc mvc;

    @Autowired
    FitnessstudioProperties properties;

    @Test
    @WithMockUser(roles = "BOSS")
    public void testOverview() throws Exception {
        mvc.perform(get("/admin"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "BOSS")
    public void testProperties() throws Exception {

        mvc.perform(post("/admin")
                .param("name", "Fabians Fitness Fabrik")
                .param("bounty", "42")
                .param("starts[0]", LocalTime.of(7, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[1]", LocalTime.of(8, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[2]", LocalTime.of(9, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[3]", LocalTime.of(10, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[4]", LocalTime.of(11, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[5]", LocalTime.of(12, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("starts[6]", LocalTime.of(13, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[0]", LocalTime.of(14, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[1]", LocalTime.of(15, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[2]", LocalTime.of(16, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[3]", LocalTime.of(17, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[4]", LocalTime.of(18, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[5]", LocalTime.of(19, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .param("ends[6]", LocalTime.of(20, 30).format(DateTimeFormatter.ISO_LOCAL_TIME))
                .with(csrf()))
            .andExpect(status().isFound());

        assertEquals("Fabians Fitness Fabrik", this.properties.getName());
    }
}