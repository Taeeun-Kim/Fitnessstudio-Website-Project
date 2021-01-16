package fitnessstudio.coupon;

import fitnessstudio.shop.order.OrderForm;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import org.javamoney.moneta.Money;
import org.salespointframework.core.Currencies;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Optional;

@Controller
public class CouponController {

    private CouponManager couponManager;
    private UserManager userManager;
    private OrderManager<Order> orderManager;

    public CouponController(CouponManager couponManager, UserManager userManager, OrderManager<Order> orderManager) {
        this.couponManager = couponManager;
        this.userManager = userManager;
        this.orderManager = orderManager;
    }

    @GetMapping("/shop/coupon")
    public String coupon(Model model, OrderForm orderForm) {
        model.addAttribute("orderForm", orderForm);
        return "pages/shop/coupon";
    }

    @PostMapping("/shop/coupon")
    public String buy(@RequestParam @Positive Float amount, @Valid OrderForm orderForm, Errors result) {
        if (result.hasErrors()) {
            return "pages/shop/coupon";
        }

        Optional<Customer> customer = userManager.findCustomerById(orderForm.getCustomer());

        if (customer.isEmpty()) {
            result.rejectValue("customer", "customer.invalid", "Diese Kundennummer existiert nicht");
            return "pages/shop/coupon";
        }

        Coupon coupon = couponManager.create(Money.of(amount, Currencies.EURO));

        Cart cart = new Cart();
        cart.addOrUpdateItem(coupon, 1);

        Order order = cart.createOrderFor(customer.get().getUserAccount());
        orderManager.save(order);

        return "redirect:/shop/checkout/" + order.getId();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/profile/account/coupon")
    public String redeem(@LoggedIn Optional<UserAccount> userAccount, Model model,
                         @Valid @ModelAttribute("couponForm") Coupon.Form couponForm, Errors result) {
        Optional<Coupon> coupon = couponManager.findCouponByCode(couponForm.getCode());

        if (coupon.isEmpty()) {
            result.rejectValue("code", "code.incorrect", "Dieser Coupon ist ungültig");
        }

        if (result.hasErrors()) {
            Customer customer = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow();
            model.addAttribute("customer", customer);
            return "pages/profile/account";
        }

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        customer.addCredit(coupon.orElseThrow().getPrice().getNumber().floatValue());
        couponManager.delete(coupon.orElseThrow());

        return "redirect:/profile/account";
    }
}
