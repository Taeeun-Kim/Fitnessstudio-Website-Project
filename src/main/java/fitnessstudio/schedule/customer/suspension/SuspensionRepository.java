package fitnessstudio.schedule.customer.suspension;

import fitnessstudio.user.customer.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public interface SuspensionRepository extends CrudRepository<Suspension, Long> {

    @Override
    @NonNull
    Streamable<Suspension> findAll();

    Streamable<Suspension> findByCustomer(Customer customer);
}
