package fitnessstudio.schedule.customer.training;

import fitnessstudio.user.customer.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public interface TrainingRepository extends CrudRepository<Training, Long> {

    @Override
    @NonNull
    Streamable<Training> findAll();

    Streamable<Training> findByCustomer(Customer customer);

}
