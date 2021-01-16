package fitnessstudio.schedule.request;

import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Enumeration;

/**
 * {@link Enumeration} helper class that encodes different states an {@link User}s {@link ScheduleEntry}s status can be
 * (assuming it has a status attribute)
 */
public enum RequestStatus {

    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    @NotEmpty(message = "Anfragenstatus darf nicht leer sein")
    @NotNull(message = "Anfragenstatus darf nicht null sein")
    private final String localization;

    RequestStatus(String localization) {
        this.localization = localization;
    }

    public String getLocalization() {
        return localization;
    }
}
