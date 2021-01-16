package fitnessstudio.user.employee;

import fitnessstudio.user.UserManager;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequestMapping("/admin/employees")
public class EmployeeController {

    private final UserManager userManager;
    private final UserAccountManager userAccountManager;

    public EmployeeController(UserManager userManager, UserAccountManager userAccountManager) {
        this.userManager = userManager;
        this.userAccountManager = userAccountManager;
    }

    @GetMapping("")
    public String employees(Model model) {
        model.addAttribute("employeeList", userManager.getEmployees().findAll());
        return "pages/admin/employees/index";
    }

    @GetMapping("/create")
    public String createGet(Model model, EmployeeForm employeeForm) {
        model.addAttribute("employeeForm", employeeForm);
        return "pages/admin/employees/add";
    }

    @PostMapping("/create")
    public String createPost(@Valid EmployeeForm employeeForm, Errors result) {
        if (!employeeForm.getPassword().equals(employeeForm.getRepeatPassword())) {
            result.rejectValue("repeatPassword", "repeatPassword.incorrect", "Passwörter müssen übereinstimmen");
        }

        if (result.hasErrors()) {
            return "pages/admin/employees/add";
        }

        userManager.createEmployee(employeeForm);
        return "redirect:/admin/employees";
    }

    @GetMapping("/{id}")
    public String editEmployeeGet(Model model, @PathVariable Long id) {
        Employee employee = userManager.findEmployeeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Employee ID"));

        EmployeeForm.Edit employeeForm = new EmployeeForm.Edit(employee.getUserAccount().getEmail(),
                employee.getUserAccount().getFirstname(), employee.getUserAccount().getLastname(),
                employee.getAddress().getStreet(), employee.getAddress().getNumber(), employee.getAddress().getCode(),
                employee.getAddress().getLocation(), employee.getSalary().getNumber().floatValue());

        model.addAttribute("employeeForm", employeeForm);

        return "pages/admin/employees/edit";

    }

    @PostMapping("/{id}")
    public String editEmployeePost(@PathVariable Long id,
                                   @ModelAttribute("employeeForm") @Valid EmployeeForm.Edit employeeForm,
                                   Errors result) {
        Employee employee = userManager.findEmployeeById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Employee ID"));

        if (result.hasErrors()) {
            return "pages/admin/employees/edit";
        }

        userManager.editEmployee(employee, employeeForm);
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/toggle")
    public String deactivateEmployee(@PathVariable String id) {
        Employee employee = userManager.findEmployeeById(Long.parseLong(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Customer ID"));

        if (employee.getUserAccount().hasRole(UserManager.ROLE_BOSS)) {
            return "redirect:/admin/employees";
        }

        if (employee.getUserAccount().isEnabled()) {
            userAccountManager.disable(Objects.requireNonNull(employee.getUserAccount().getId()));
        } else {
            userAccountManager.enable(Objects.requireNonNull(employee.getUserAccount().getId()));
        }
        return "redirect:/admin/employees";
    }
}
