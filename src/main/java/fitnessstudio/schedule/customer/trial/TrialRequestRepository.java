package fitnessstudio.schedule.customer.trial;

import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface TrialRequestRepository extends CrudRepository<TrialRequest, Long> {

    @Override
    @NonNull
    Streamable<TrialRequest> findAll();

    @Override
    @NonNull
    Optional<TrialRequest> findById(@NonNull Long id);

    Streamable<TrialRequest> findByCustomer(Customer customer);

    Streamable<TrialRequest> findByEmployee(Employee employee);
}
