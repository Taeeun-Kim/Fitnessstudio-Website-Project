package fitnessstudio.user.customer;


import fitnessstudio.user.UserManager;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

@Controller
public class CustomerController {

    private final UserManager userManager;
    private final UserAccountManager userAccountManager;

    public CustomerController(UserManager userManager, UserAccountManager userAccountManager) {
        this.userManager = userManager;
        this.userAccountManager = userAccountManager;
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasAnyRole('BOSS','EMPLOYEE')")
    public String editCustomerGet(Model model, @PathVariable long id){
        Customer customer = userManager.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer ID"));

        model.addAttribute("customerForm", new CustomerForm.Profile(customer.getUserAccount().getEmail(),
                customer.getUserAccount().getFirstname(), customer.getUserAccount().getLastname(), null, null, null,
                customer.getAddress().getStreet(), customer.getAddress().getNumber(), customer.getAddress().getCode(),
                customer.getAddress().getLocation()));

        return "pages/customers/edit";
    }

    @PostMapping("/customers/{id}")
    @PreAuthorize("hasAnyRole('BOSS','EMPLOYEE')")
    public String editCustomerPost(@PathVariable Long id,
                                   @ModelAttribute("customerForm") @Valid CustomerForm.Profile customerForm,
                                   Errors result) {
        Customer customer = userManager.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer ID"));

        if (result.hasErrors()) {
            return "pages/customers/edit";
        }

        userManager.editCustomer(customer, customerForm);
        return "redirect:/customers";
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('BOSS','EMPLOYEE')")
    public String customers(Model model) {
        model.addAttribute("customerList", userManager.getCustomers().findAll());
        return "pages/customers/index";
    }

    @PostMapping("/customers/{id}/toggle")
    @PreAuthorize("hasAnyRole('BOSS','EMPLOYEE')")
    public String deactivateCustomer(@PathVariable Long id) {
        Customer customer = userManager.findCustomerById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer ID"));

        if (customer.getUserAccount().isEnabled()) {
            userAccountManager.disable(Objects.requireNonNull(customer.getUserAccount().getId()));
        } else {
            userAccountManager.enable(Objects.requireNonNull(customer.getUserAccount().getId()));
        }
        return "redirect:/customers";
    }
}
