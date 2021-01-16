package fitnessstudio;


import fitnessstudio.contracts.ContractManager;
import fitnessstudio.properties.FitnessstudioProperties;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.CustomerForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class FitnessstudioController {

    private final FitnessstudioProperties properties;
    private final UserManager userManager;
    private final ContractManager contractManager;

    public FitnessstudioController(FitnessstudioProperties properties, UserManager userManager,
                                   ContractManager contractManager) {
        this.properties = properties;
        this.userManager = userManager;
        this.contractManager = contractManager;
    }

    @GetMapping("/")
    public String index(Model model, CustomerForm customerForm, HttpServletRequest req) {
        if (req.isUserInRole("CUSTOMER") || req.isUserInRole("EMPLOYEE") || req.isUserInRole("BOSS")) {
            return "redirect:/schedule";
        }
        model.addAttribute("customerForm", customerForm);
        model.addAttribute("contracts", contractManager.findAll());
        return "pages/index";
    }

    @GetMapping("/register")
    public String registerGet(Model model, CustomerForm customerForm){
        model.addAttribute("contracts", contractManager.findAll());
        model.addAttribute("customerForm", customerForm);
        return "pages/auth";
    }

    @PostMapping("/register")
    public String registerPost(@Valid CustomerForm customerForm, Errors results, Model model) {
        if (!customerForm.getRepeatPassword().equals(customerForm.getPassword())){
            results.rejectValue("repeatPassword", "repeatPassword.incorrect", "Passwörter stimmen nicht überein");
        }

        if (customerForm.getPromo() != null && userManager.getCustomers().findById(customerForm.getPromo()).isEmpty()) {
            results.rejectValue("promo", "promo.incorrect", "Mitglied existiert nicht");
        }

        if (results.hasErrors()) {
            model.addAttribute("contracts", contractManager.findAll());
            return "pages/auth";
        }

        userManager.createCustomer(customerForm);
        return "redirect:/login";
    }

    @GetMapping("/hours")
    public String hours(Model model) {
        model.addAttribute("hours", properties.getBusinessHours());
        return "pages/hours";
    }

    @GetMapping("/contracts")
    public String contracts(Model model) {
        model.addAttribute("contracts", contractManager.findAll());
        return "pages/contracts";
    }
}
