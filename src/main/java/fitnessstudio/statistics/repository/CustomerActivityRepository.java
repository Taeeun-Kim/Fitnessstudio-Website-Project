package fitnessstudio.statistics.repository;

import fitnessstudio.statistics.record.CustomerActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public interface CustomerActivityRepository extends CrudRepository<CustomerActivity, Long> {

    @Override
    @NonNull
    Streamable<CustomerActivity> findAll();
}
