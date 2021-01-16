package fitnessstudio.schedule.entry;

import fitnessstudio.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalTime;

/**
 * Parent-class that represents an abstract form that collects {@link User} input for {@link ScheduleEntry} creation.
 */
public abstract class ScheduleEntryForm {

    @NotEmpty(message = "Startdatum darf nicht leer sein")
    @NotNull(message = "Startdatum darf nicht leer sein")
    @Pattern(regexp = "^([0-9]{4})(-)([0-1][0-9])(-)([0-3][0-9])$")
    private final String startDate;

    @NotNull(message = "Startzeit darf nicht leer sein")
    private final LocalTime startTime;

    @NotEmpty(message = "Enddatum darf nicht leer sein")
    @NotNull(message = "Enddatum darf nicht leer sein")
    @Pattern(regexp = "^([0-9]{4})(-)([0-1][0-9])(-)([0-3][0-9])$")
    private final String endDate;

    @NotNull(message = "Endzeit darf nicht leer sein")
    private final LocalTime endTime;

    /**
     * constructs a new {@link ScheduleEntryForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param endTime {@link LocalTime} that represents the end time
     */
    public ScheduleEntryForm(String startDate, LocalTime startTime, String endDate, LocalTime endTime) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public String getEndDate() { return endDate; }

    public LocalTime getEndTime() { return endTime; }
}
