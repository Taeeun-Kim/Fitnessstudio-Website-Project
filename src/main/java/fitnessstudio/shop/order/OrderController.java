package fitnessstudio.shop.order;

import fitnessstudio.coupon.Coupon;
import fitnessstudio.coupon.CouponManager;
import fitnessstudio.shop.inventory.FitnessstudioInventory;
import fitnessstudio.shop.inventory.FitnessstudioInventoryItem;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class OrderController {

    private final OrderManager<Order> orderManager;
    private final FitnessstudioInventory inventory;
    private final UserManager userManager;
    private final BusinessTime businessTime;
    private final CouponManager couponManager;

    public OrderController(OrderManager<Order> orderManager, FitnessstudioInventory inventory,
                           UserManager userManager, BusinessTime businessTime, CouponManager couponManager) {
        this.orderManager = orderManager;
        this.inventory = inventory;
        this.userManager = userManager;
        this.businessTime = businessTime;
        this.couponManager = couponManager;
    }

    @ModelAttribute("cart")
    Cart initializeCart() {
        return new Cart();
    }

    @GetMapping("/shop/cart")
    public String basket(Model model, @ModelAttribute Cart cart, OrderForm orderForm) {
        modelCart(model, cart, orderForm);
        return "pages/shop/cart";
    }

    @PostMapping("/shop/cart")
    public String addToCart(@RequestParam Product product,
                            @RequestParam @Positive Integer amount,
                            @ModelAttribute Cart cart) {
        cart.addOrUpdateItem(product, amount);
        return "redirect:/shop/cart";
    }

    @PostMapping("/shop/cart/{id}")
    public String updateCartItem(@PathVariable String id,
                                 @RequestParam @Positive Integer amount,
                                 @ModelAttribute Cart cart) {
        CartItem item = cart.getItem(id).orElseThrow();

        if (amount <= 0) {
            cart.removeItem(id);
        } else {
            cart.addOrUpdateItem(item.getProduct(), Quantity.of(amount).subtract(item.getQuantity()));
        }
        return "redirect:/shop/cart";
    }

    @PostMapping("/shop/checkout")
    public String createOrder(@ModelAttribute Cart cart, @Valid OrderForm orderForm, Errors result, Model model) {
        if (result.hasErrors()) {
            modelCart(model, cart, orderForm);
            return "pages/shop/cart";
        }

        Optional<Customer> customer = userManager.findCustomerById(orderForm.getCustomer());

        if (customer.isEmpty()) {
            result.rejectValue("customer", "customer.invalid", "Diese Kundennummer existiert nicht");
            modelCart(model, cart, orderForm);
            return "pages/shop/cart";
        }

        Order order = cart.createOrderFor(customer.get().getUserAccount());
        orderManager.save(order);

        cart.clear();

        return "redirect:/shop/checkout/" + order.getId();
    }

    private void modelCart(Model model, Cart cart, OrderForm orderForm) {
        model.addAttribute("cart", cart);
        model.addAttribute("stock", inventory);
        model.addAttribute("today", businessTime.getTime().toLocalDate());
        model.addAttribute("orderForm", orderForm);
    }

    @GetMapping("/shop/checkout/{order}")
    public String checkoutOrder(Model model, @PathVariable @NonNull Order order) {
        Customer customer = userManager.getCustomers().findByUserAccount(order.getUserAccount()).orElseThrow();

        model.addAttribute("order", order);
        model.addAttribute("customer", customer);
        return "pages/shop/checkout";
    }

    @PostMapping(value = "/shop/checkout/{order}/cash")
    public String completeOrderByCash(@PathVariable @NotNull Order order) {
        order.setPaymentMethod(Cash.CASH);
        orderManager.save(order);
        orderManager.payOrder(order);
        this.completeOrder(order);
        return "redirect:/orders/" + order.getId();
    }

    @PostMapping(value = "/shop/checkout/{order}/funds")
    public String completeOrderByAccount(@PathVariable @NotNull Order order) {
        Customer customer = userManager.getCustomers().findByUserAccount(order.getUserAccount()).orElseThrow();
        Money balance = customer.getBalance();

        if (balance.isLessThan(order.getTotal())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer cannot afford the payment");
        }

        order.setPaymentMethod(new FundsPayment(customer));
        orderManager.save(order);

        customer.subtractCredit(order.getTotal());

        orderManager.payOrder(order);
        this.completeOrder(order);

        return "redirect:/orders/" + order.getId();
    }

    @PostMapping(value = "/shop/checkout/{order}", params = "cancel")
    public String cancelOrder(@PathVariable @NonNull Order order) {
        orderManager.cancelOrder(order);
        return "redirect:/orders/" + order.getId();
    }

    @GetMapping("/orders/{order}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public String readOrder(Model model, @PathVariable @NonNull Order order) {
        if (order.getOrderStatus() == OrderStatus.OPEN) {
            // Redirect to Checkout if Order is still open
            return "redirect:/shop/checkout/" + order.getId();
        } else if (order.getOrderStatus() == OrderStatus.PAID) {
            /* This block shouldn't be reached. But if it does,
            any paid Order should instantly be marked as completed as well */
            orderManager.completeOrder(order);
        }

        model.addAttribute("order", order);

        // Add Coupon to model if Order is a Coupon
        Optional<OrderLine> line = order.getOrderLines().get().findFirst();
        if (line.isPresent()
                && line.get().getProductName().equals("Coupon")) {
            Optional<Coupon> coupon = couponManager.getRepository()
                    .findById(line.get().getProductIdentifier());

            coupon.ifPresent(value -> model.addAttribute("coupon", value));
        }

        return "pages/shop/order";
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String readOrders(Model model, @LoggedIn Optional<UserAccount> userAccount) {
        // Redirect if not authenticated
        if (userAccount.isEmpty()) {
            return "redirect:/";
        }

        model.addAttribute("orders", orderManager.findBy(userAccount.get())
                .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETED));
        return "pages/shop/orders";
    }

    /**
     * Stellt eine Bestellung fertig und entfernt die entsprechenden Produkte aus dem Lager
     */
    private void completeOrder(Order order) {
        // Check if Coupon has been ordered
        Optional<OrderLine> line = order.getOrderLines().get().findFirst();
        if (line.isPresent()
                && line.get().getProductName().equals("Coupon")) {
            Coupon coupon = couponManager.getRepository().findById(line.get().getProductIdentifier()).orElseThrow();
            inventory.save(new FitnessstudioInventoryItem(coupon, Quantity.of(1)));
        }

        orderManager.completeOrder(order);

        // Remove Products from Inventory
        order.getOrderLines().get().forEach(orderLine -> {
            Quantity left = orderLine.getQuantity();
            ProductIdentifier productId = orderLine.getProductIdentifier();
            LocalDate today = businessTime.getTime().toLocalDate();

            Iterator<FitnessstudioInventoryItem> iter = inventory.findUnexpiredItemsSorted(productId, today).iterator();

            while (!left.isZeroOrNegative()) {
                FitnessstudioInventoryItem item = iter.next();
                if (left.isLessThan(item.getQuantity())) {
                    item.decreaseQuantity(left);
                    inventory.save(item);
                    left = Quantity.NONE;
                } else {
                    left = left.subtract(item.getQuantity());
                    inventory.delete(item);
                }
            }
        });
    }
}
