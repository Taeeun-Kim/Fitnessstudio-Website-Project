package fitnessstudio.shop.catalog;

import fitnessstudio.AbstractIntegrationTests;
import org.hamcrest.Matchers;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.core.Currencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class CatalogControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CatalogManager catalogManager;

    private FitnessstudioProduct product;

    @BeforeAll
    public void setup() {
        this.product = new FitnessstudioProduct("Example Product",
                Money.of(10, Currencies.EURO), "Example Description", false,
                "/assets/image/example.png");

        catalogManager.getCatalog().save(product);
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCatalog() throws Exception {
        mvc.perform(get("/shop"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("catalog", is(not(emptyIterable()))));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testDetail() throws Exception {
        mvc.perform(get("/shop/product/{product}", this.product.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("product", Matchers.is(this.product)));
    }
}
