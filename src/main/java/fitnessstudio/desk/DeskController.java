package fitnessstudio.desk;

import fitnessstudio.shop.order.OrderForm;
import fitnessstudio.statistics.record.CustomerActivity;
import fitnessstudio.statistics.repository.CustomerActivityRepository;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import org.salespointframework.time.BusinessTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.*;

@Controller
@RequestMapping("desk")
public class DeskController {

    public static final boolean ENTER = true;
    public static final boolean EXIT = false;

    private final CustomerActivityRepository customerActivities;
    private final UserManager userManager;
    private BusinessTime businessTime;

    public DeskController(CustomerActivityRepository customerActivities, UserManager userManager,
                          BusinessTime businessTime) {
        this.customerActivities = customerActivities;
        this.userManager = userManager;
        this.businessTime = businessTime;
    }

    @GetMapping
    public String desk(Model model, OrderForm orderForm) {
        model.addAttribute("activeCustomers", this.findActiveCustomers());
        model.addAttribute("form", orderForm);
        return "pages/desk";
    }

    @PostMapping("/checkin")
    public String enter(@ModelAttribute("form") @Valid OrderForm form, Errors result, Model model) {
        Optional<Customer> customer = userManager.getCustomers().findById(form.getCustomer());

        if (customer.isEmpty()) {
            result.rejectValue("customer", "customer.invalid", "Diese Kundennummer existiert nicht");
        }

        if (result.hasErrors()) {
            model.addAttribute("activeCustomers", this.findActiveCustomers());
            return "pages/desk";
        }

        addCustomerActivity(customer.get(), ENTER);
        return "redirect:/desk";
    }

    @PostMapping("/checkout")
    public String exit(@RequestParam @NotNull Long id) {
        Customer customer = userManager.getCustomers().findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer ID"));

        addCustomerActivity(customer, EXIT);
        return "redirect:/desk";
    }

    /**
     * Saves a new CustomerActivity to Statistics. Throws Bad Request if the Customer does not exist.
     */
    private void addCustomerActivity(Customer customer, boolean direction) {
        customerActivities.save(new CustomerActivity(customer, businessTime.getTime(), direction));
    }

    /**
     * Finds all customers who entered but haven't exited yet
     */
    private List<Customer> findActiveCustomers() {
        Map<Long, CustomerActivity> latest = new HashMap<>();
        customerActivities.findAll().forEach(c -> {
            Long cId = c.getCustomer().getId();
            if (!latest.containsKey(cId)
                    || c.getTimestamp().isAfter(latest.get(cId).getTimestamp())
                    || c.getTimestamp().isEqual(latest.get(cId).getTimestamp())) {
                latest.put(c.getCustomer().getId(), c);
            }
        });
        List<Customer> actives = new ArrayList<>();
        latest.forEach((key, value) -> {
            if (value.getDirection() == ENTER) {
                actives.add(userManager.getCustomers().findById(key).orElseThrow());
            }
        });
        return actives;
    }
}
