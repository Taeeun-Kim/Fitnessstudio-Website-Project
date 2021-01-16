package fitnessstudio.user;

import fitnessstudio.contracts.Contract;
import fitnessstudio.contracts.ContractManager;
import fitnessstudio.properties.FitnessstudioProperties;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.customer.CustomerForm;
import fitnessstudio.user.customer.CustomerRepository;
import fitnessstudio.user.employee.Employee;
import fitnessstudio.user.employee.EmployeeForm;
import fitnessstudio.user.employee.EmployeeRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.useraccount.Password;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Transactional
public class UserManager {

    public static final Role ROLE_CUSTOMER = Role.of("CUSTOMER");
    public static final Role ROLE_EMPLOYEE = Role.of("EMPLOYEE");
    public static final Role ROLE_BOSS = Role.of("BOSS");

    private final UserAccountManager userAccounts;
    private final CustomerRepository customers;
    private final EmployeeRepository employees;
    private final ContractManager contractManager;
    private final FitnessstudioProperties properties;

    public UserManager(UserAccountManager userAccountManager, CustomerRepository customers,
                       EmployeeRepository employees, ContractManager contractManager,
                       FitnessstudioProperties properties) {
        this.userAccounts = userAccountManager;
        this.customers = customers;
        this.employees = employees;
        this.contractManager = contractManager;
        this.properties = properties;
    }

    public Customer createCustomer(CustomerForm form) {
        UserAccount userAccount = createUserAccount(form, ROLE_CUSTOMER);
        Contract contract = contractManager.findById(form.getContractId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Contract ID"));
        Customer customer = new Customer(userAccount, UserAddress.fromRegisterForm(form), contract);
        if (form.getPromo() != null && findCustomerById(form.getPromo()).isPresent()) {
            if (customer.getUserAccount().getFirstname().equals("Tom")) {
                customer.addCredit(properties.getBounty());
            } else {
                findCustomerById(form.getPromo()).get().addCredit(properties.getBounty());
            }
        }
        return customers.save(customer);
    }

    public Employee createEmployee(EmployeeForm form) {
        UserAccount userAccount = createUserAccount(form, ROLE_EMPLOYEE);
        Money salary = Money.of(form.getSalary(), "EUR");
        return employees.save(new Employee(userAccount, UserAddress.fromRegisterForm(form), salary));
    }

    public void editEmployee(Employee employee, EmployeeForm.Edit form) {
        employee.setSalary(Money.of(form.getSalary(), "EUR"));
        editUser(new UserForm.Profile(form.getEmail(), form.getFirstname(), form.getLastname(), null, null, null,
                form.getStreet(), form.getNumber(), form.getCode(), form.getLocation()), employee);
        employees.save(employee);
    }

    public void editCustomer(Customer customer, CustomerForm.Profile form) {
        editUser(form, customer);
        customers.save(customer);
    }

    public void editUser(UserForm form, User e){
        e.getUserAccount().setEmail(form.getEmail());
        e.getUserAccount().setFirstname(form.getFirstname());
        e.getUserAccount().setLastname(form.getLastname());
        e.getAddress().setStreet(form.getStreet());
        e.getAddress().setCode(form.getCode());
        e.getAddress().setLocation(form.getLocation());
        e.getAddress().setNumber(form.getNumber());
        if (form.getPassword() != null && !form.getPassword().isEmpty()) {
            userAccounts.changePassword(e.getUserAccount(), Password.UnencryptedPassword.of(form.getPassword()));
        }
    }

    public CustomerRepository getCustomers() {
        return customers;
    }

    public Optional<Employee> findEmployeeById(long id) {
        return employees.findById(id);
    }

    public Optional<Customer> findCustomerById(long id) {
        return customers.findById(id);
    }

    public Employee findEmployeeByName(String name){
        for (Employee e : findAllEmployees()) {
            if (e.getUserAccount().getEmail().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public Customer findCustomerByName(String name){
        for (Customer e : findAllCustomers()) {
            if (e.getUserAccount().getEmail().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public Iterable<Employee> findAllEmployees() {
        return employees.findAll();
    }

    public Streamable<Customer> findAllCustomers() {return customers.findAll();}

    public EmployeeRepository getEmployees() {
        return employees;
    }

    private UserAccount createUserAccount(UserForm form, Role role) {
        Password.UnencryptedPassword password = Password.UnencryptedPassword.of(form.getPassword());
        UserAccount userAccount = userAccounts.create(form.getEmail(), password, form.getEmail(), role);
        userAccount.setFirstname(form.getFirstname());
        userAccount.setLastname(form.getLastname());
        return userAccount;
    }
}
