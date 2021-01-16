package fitnessstudio.user.employee;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Override
    Streamable<Employee> findAll();

    Optional<Employee> findByUserAccount(UserAccount userAccount);
}
