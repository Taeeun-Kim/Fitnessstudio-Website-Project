package fitnessstudio.schedule.employee.holiday;

import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.employee.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public interface HolidayRepository extends CrudRepository<Holiday, Long> {

    @Override
    @NonNull
    Streamable<Holiday> findAll();

    Streamable<Holiday> findByStatus(RequestStatus status);

    Streamable<Holiday> findByEmployee(Employee employee);
}
