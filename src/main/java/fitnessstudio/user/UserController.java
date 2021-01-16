package fitnessstudio.user;

import fitnessstudio.coupon.Coupon;
import fitnessstudio.schedule.ScheduleManager;
import fitnessstudio.schedule.customer.suspension.Suspension;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Controller
public class UserController {

    private UserManager userManager;
    private UserAccountManager userAccountManager;
    private ScheduleManager scheduleManager;
    private BusinessTime businessTime;
    private OrderManager<Order> orderManager;

    public UserController(UserManager userManager, ScheduleManager scheduleManager, BusinessTime businessTime,
                          UserAccountManager userAccountManager, OrderManager<Order> orderManager) {
        this.userManager = userManager;
        this.scheduleManager = scheduleManager;
        this.businessTime = businessTime;
        this.userAccountManager = userAccountManager;
        this.orderManager = orderManager;
    }

    //@Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate = 30000)
    public void autoEnable() {
        System.out.println("Scanning for disabled users.");

        for (Customer customer: userManager.findAllCustomers()) {
            if (customer.isEnabled()) {
                continue;
            }

            for (Suspension s : scheduleManager.getSuspensions().findByCustomer(customer)) {
                if (businessTime.getTime().isAfter(s.getEnd())) {
                    System.out.println("User Unlocked: " + customer.getUserAccount().getEmail());
                    userAccountManager.enable(Objects.requireNonNull(customer.getUserAccount().getId()));
                }
            }
        }
    }

    @GetMapping("/profile")
    public String profileGet(Model model, @LoggedIn Optional<UserAccount> loggedIn) {
        UserAccount userAccount = loggedIn.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));

        User user = userManager.getEmployees().findByUserAccount(userAccount).orElse(null);

        if (user == null) {
            user = userManager.getCustomers().findByUserAccount(userAccount).orElse(null);
            Customer customer = (Customer) user;
            model.addAttribute("sus", true);
            for(Suspension s : scheduleManager.getSuspensions().findByCustomer(customer)){
                //1 Year period?
                if(businessTime.getTime().getYear() - s.getStart().getYear() == 0){
                    model.addAttribute("sus", false);
                    break;
                }
            }
        }

        if (user == null) {
            return "redirect:/";
        }

        UserForm.Profile profileForm = new UserForm.Profile(user.getUserAccount().getEmail(),
                user.getUserAccount().getFirstname(), user.getUserAccount().getLastname(),
                null, null, null, user.getAddress().getStreet(), user.getAddress().getNumber(),
                user.getAddress().getCode(), user.getAddress().getLocation());

        if (user instanceof Customer) {
            model.addAttribute("id", user.getId());
        }

        model.addAttribute("profileForm", profileForm);

        return "pages/profile/index";
    }

    @PostMapping("/profile")
    public String profilePost(@Valid @ModelAttribute("profileForm") UserForm.Profile profileForm, Errors result,
                              Model model, @LoggedIn Optional<UserAccount> loggedIn) {
        UserAccount userAccount = loggedIn.orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        User user = userManager.getEmployees().findByUserAccount(userAccount).orElse(null);
        if (user == null) {
            user = userManager.getCustomers().findByUserAccount(userAccount).orElse(null);
        }

        if (!profileForm.getPassword().isBlank()) {
            if (!(new BCryptPasswordEncoder().matches(profileForm.getOldPw(),
                    user.getUserAccount().getPassword().toString()))) {
                result.rejectValue("oldPw", "oldPw.incorrect", "Altes Passwort ist nicht korrekt");
            }
            if (!profileForm.getPassword().equals(profileForm.getRepeatPassword())) {
                result.rejectValue("repeatPassword", "repeatPassword.incorrect", "Passwörter stimmen nicht überein");
            }
        }

        if (!result.hasErrors()) {
            userManager.editUser(profileForm, user);
            model.addAttribute("info", "Daten wurden gespeichert");
        }

        if (user instanceof Customer) {
            model.addAttribute("id", user.getId());
        }

        return "pages/profile/index";
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/profile/suspense")
    public String suspensePost(@LoggedIn Optional<UserAccount> userAccount){
        if (userAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Optional<Customer> customer = userManager.getCustomers().findByUserAccount(userAccount.get());

        if (customer.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        scheduleManager.getSuspensions().save(new Suspension(businessTime.getTime(), customer.get()));
        userAccountManager.disable(Objects.requireNonNull(userAccount.get().getId()));

        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

        return "redirect:/profile";
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/profile/account")
    public String accountGet(Model model, @LoggedIn Optional<UserAccount> userAccount,
                             @ModelAttribute("couponForm") Coupon.Form couponForm) {
        // Redirect if not authenticated
        if (userAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow();
        model.addAttribute("customer", customer);
        model.addAttribute("couponForm", couponForm);
        return "pages/profile/account";
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/profile/account")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String accountPost(@RequestParam Float credit, @LoggedIn Optional<UserAccount> userAccount) {
        // Redirect if not authenticated
        if (userAccount.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow();
        customer.addCredit(credit);
        userManager.getCustomers().save(customer);
        return "redirect:/profile/account";
    }


    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/profile/account/pdf")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE', 'CUSTOMER')")
    public ResponseEntity<byte[]> accountPdf(@LoggedIn Optional<UserAccount> userAccount) {
        // Redirect if not authenticated
        if (userAccount.isEmpty()) {
            return null;
        }
        try {
            byte[] pdfContent;
            String filename;
            if (userAccount.get().hasRole(UserManager.ROLE_EMPLOYEE)) {
                Employee user = userManager.getEmployees().findByUserAccount(userAccount.get()).orElseThrow();
                pdfContent = user.generatePdf(businessTime, scheduleManager, userManager);
                filename = "Gehaltsabrechnung.pdf";
            } else {
                Customer user = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow();
                pdfContent = user.generatePdf(businessTime, orderManager);
                filename = "Rechnung.pdf";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
