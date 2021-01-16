package fitnessstudio.schedule.employee.shift;

import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.user.employee.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;

public interface ShiftRepository extends CrudRepository<Shift, Long> {

    @Override
    @NonNull
    Streamable<Shift> findAll();

    Streamable<Shift> findByDepartment(Department department);

    Streamable<Shift> findByEmployee(Employee employee);

}
