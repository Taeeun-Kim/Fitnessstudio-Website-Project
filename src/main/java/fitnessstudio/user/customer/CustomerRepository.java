package fitnessstudio.user.customer;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    @Override
    public Streamable<Customer> findAll();

    public Optional<Customer> findByUserAccount(UserAccount userAccount);
}
