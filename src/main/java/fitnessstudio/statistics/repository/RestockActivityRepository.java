package fitnessstudio.statistics.repository;

import fitnessstudio.statistics.record.RestockActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface RestockActivityRepository extends CrudRepository<RestockActivity, Long> {

    Streamable<RestockActivity> findAllByOrderByTimestampDesc();
}
