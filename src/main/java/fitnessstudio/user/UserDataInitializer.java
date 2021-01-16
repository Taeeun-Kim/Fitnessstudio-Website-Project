package fitnessstudio.user;


import fitnessstudio.contracts.Contract;
import fitnessstudio.contracts.ContractManager;
import fitnessstudio.user.customer.CustomerForm;
import fitnessstudio.user.employee.Employee;
import fitnessstudio.user.employee.EmployeeForm;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.UserAccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;


@Component
@Order(10)
public class UserDataInitializer implements DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataInitializer.class);
    private final UserAccountManager userAccountManager;
    private final UserManager userManager;
    private final ContractManager contractManager;

    UserDataInitializer(UserAccountManager userAccountManager, UserManager userManager,
                        ContractManager contractManager) {
        Assert.notNull(userAccountManager, "UserAccountManager must not be null");
        Assert.notNull(userManager, "UserManager must not be null");
        this.userAccountManager = userAccountManager;
        this.userManager = userManager;
        this.contractManager = contractManager;
    }

    @Override
    public void initialize() {
        if (!userAccountManager.findAll().isEmpty()) {
            return;
        }

        LOG.info("No Users found! Creating defaults.");

        // Initialize Boss

        Employee boss = userManager.createEmployee(new EmployeeForm("boss@email.com", "Elon", "Musk", "123",
                "123", "Musterstraße", "1", "01069", "Musterstadt", "2.99"));
        boss.getUserAccount().add(UserManager.ROLE_BOSS);

        // Initialize Employees
        List.of(
                new EmployeeForm("hans@email.com", "Hans", "Get the Flammenwerfer", "123", "123",
                        "Musterstraße", "1", "01069", "Musterstadt", "2.99"),
                new EmployeeForm("peter@email.com", "Peter", "Lustig", "123", "123",
                        "Musterallee", "1", "01069", "Musterstadt", "2.99")
        ).forEach(userManager::createEmployee);

        // Initialize Customers
        Contract contract = contractManager.findAll().iterator().next();
        List.of(new CustomerForm("harald@email.com", "Harald", "Müller", "123", "123",
                        "Musterweg", "1", "01069", "Musterstadt", null, contract.getId()),
                new CustomerForm("franz@email.com", "Franz", "Klagenfurt", "123", "123",
                        "Musterplatz", "1", "01069", "Musterstadt", null, contract.getId())
        ).forEach(userManager::createCustomer);
    }
}
