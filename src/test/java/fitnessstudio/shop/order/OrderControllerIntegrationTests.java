package fitnessstudio.shop.order;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.contracts.Contract;
import fitnessstudio.contracts.ContractForm;
import fitnessstudio.contracts.ContractManager;
import fitnessstudio.shop.catalog.CatalogManager;
import fitnessstudio.shop.catalog.FitnessstudioProduct;
import fitnessstudio.shop.inventory.FitnessstudioInventory;
import fitnessstudio.shop.inventory.FitnessstudioInventoryItem;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.customer.CustomerForm;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.salespointframework.core.Currencies;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class OrderControllerIntegrationTests extends AbstractIntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CatalogManager catalogManager;

    @Autowired
    private FitnessstudioInventory inventory;

    @Autowired
    private UserManager userManager;

    @Autowired
    private ContractManager contractManager;

    @Autowired
    private OrderManager<Order> orderManager;

    private FitnessstudioProduct product;
    private Customer customer;

    @BeforeAll
    public void setup() {
        this.product = new FitnessstudioProduct("Example Product",
                Money.of(10, Currencies.EURO), "Example Description", false,
                "/assets/image/example.png");

        catalogManager.getCatalog().save(product);
        inventory.save(new FitnessstudioInventoryItem(product, Quantity.of(5)));
        inventory.save(new FitnessstudioInventoryItem(product, Quantity.of(10)));

        Contract contract = contractManager.addContract(new ContractForm("Example", "Lorem ipsum", 10.0f));

        this.customer = userManager.createCustomer(new CustomerForm("john.doe@example.com", "John", "Doe",
                "123456", "123456", "Example Avenue", "69", "01069",
                "Dresden", null, contract.getId()));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testBasket() throws Exception {
        mvc.perform(get("/shop/cart"))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testAddToCart() throws Exception {
        Cart cart = new Cart();

        mvc.perform(post("/shop/cart")
                .param("product", Objects.requireNonNull(this.product.getId()).getIdentifier())
                .param("amount", "13")
                .flashAttr("cart", cart)
                .with(csrf()))
            .andExpect(status().isFound());

        assertEquals(1, cart.get().count());

        CartItem item = cart.get().findFirst().orElseThrow();

        assertEquals(this.product, item.getProduct());
        assertEquals(13, item.getQuantity().getAmount().intValue());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testUpdateCartItem() throws Exception {
        Cart cart = new Cart();
        CartItem item = cart.addOrUpdateItem(this.product, 13);

        mvc.perform(post("/shop/cart/{id}", item.getId())
                .param("amount", "4")
                .flashAttr("cart", cart)
                .with(csrf()))
            .andExpect(status().isFound());

        item = cart.getItem(item.getId()).orElseThrow(); // Refresh Item

        assertEquals(4, item.getQuantity().getAmount().intValue());

        mvc.perform(post("/shop/cart/{id}", item.getId())
                .param("amount", "0")
                .flashAttr("cart", cart)
                .with(csrf()))
                .andExpect(status().isFound());

        assertTrue(cart.getItem(item.getId()).isEmpty());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCreateOrder() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);

        mvc.perform(post("/shop/checkout")
                .param("customer", String.valueOf(this.customer.getId()))
                .flashAttr("cart", cart)
                .with(csrf()))
            .andExpect(status().isFound());

        assertTrue(cart.isEmpty());
        assertEquals(1, orderManager.findBy(OrderStatus.OPEN).get().count());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCheckoutOrder() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        orderManager.save(order);

        mvc.perform(get("/shop/checkout/{order}", order.getId()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCompleteOrderByCash() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        orderManager.save(order);

        mvc.perform(post("/shop/checkout/{order}/cash", order.getId())
                .with(csrf()))
            .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCompleteOrderByAccount() throws Exception {
        Customer customer = this.customer;
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        orderManager.save(order);

        mvc.perform(post("/shop/checkout/{order}/funds", order.getId())
                .with(csrf()))
            .andExpect(status().isBadRequest());

        customer.addCredit(this.product.getPrice().getNumber().floatValue());
        customer.addCredit(200);
        this.userManager.getCustomers().save(customer);

        mvc.perform(post("/shop/checkout/{order}/funds", order.getId())
                .with(csrf()))
            .andExpect(status().isFound());

        customer = this.userManager.findCustomerById(customer.getId()).orElseThrow();

        assertEquals(Money.of(80, Currencies.EURO), customer.getBalance());
        assertEquals(OrderStatus.COMPLETED, orderManager.get(order.getId()).orElseThrow().getOrderStatus());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testCancelOrder() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        orderManager.save(order);

        mvc.perform(post("/shop/checkout/{order}", order.getId())
                .param("cancel", "true")
                .with(csrf()))
            .andExpect(status().isFound());

        assertEquals(OrderStatus.CANCELLED, orderManager.get(order.getId()).orElseThrow().getOrderStatus());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testReadOrder() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        order.setPaymentMethod(Cash.CASH);
        orderManager.save(order);

        mvc.perform(get("/orders/{order}", order.getId()))
            .andExpect(status().isFound());

        orderManager.payOrder(order);
        orderManager.completeOrder(order);

        mvc.perform(get("/orders/{order}", order.getId()))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    public void testReadPaidOrder() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        order.setPaymentMethod(Cash.CASH);
        orderManager.save(order);

        orderManager.payOrder(order);

        mvc.perform(get("/orders/{order}", order.getId()))
                .andExpect(status().isOk());

        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    public void testReadOrders() throws Exception {
        Cart cart = new Cart();
        cart.addOrUpdateItem(this.product, 13);
        Order order = cart.createOrderFor(customer.getUserAccount());
        order.setPaymentMethod(Cash.CASH);
        orderManager.save(order);
        orderManager.payOrder(order);
        orderManager.completeOrder(order);

        mvc.perform(get("/orders"))
            .andExpect(status().isFound());

        // TODO: Fix dirty solution without performing a request to authenticate
        mvc.perform(post("/login")
                .param("username", this.customer.getUserAccount().getEmail())
                .param("password", "123456")
                .with(csrf()));

        mvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("orders"));
    }

}