package fitnessstudio.schedule.employee.shift.department;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * {@link Converter} class that converts {@link String}s to values of {@link Department}
 */
@Component
public class DepartmentConverter implements Converter<String, Department> {
    @Override
    public Department convert(String s) {
        return Department.valueOf(s.toUpperCase());
    }
}
