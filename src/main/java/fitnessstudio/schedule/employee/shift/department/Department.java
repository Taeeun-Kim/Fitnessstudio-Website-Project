package fitnessstudio.schedule.employee.shift.department;

import fitnessstudio.schedule.employee.shift.Shift;
import fitnessstudio.user.employee.Employee;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Enumeration;

/**
 * {@link Enumeration} helper class that encodes different departments {@link Employee}s can have their {@link Shift}s in
 */
public enum Department {

    TRAINING("training"),
    SHOP("shop"),
    CLEANING("cleaning");

    @NotEmpty(message = "Bereich darf nicht leer sein")
    @NotNull(message = "Bereich darf nicht null sein")
    private final String localization;

    Department(String localization) {
        this.localization = localization;
    }

    public String getLocalization() {
        return localization;
    }
}
