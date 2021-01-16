package fitnessstudio.schedule.entry;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Parent-class that represents an abstract entry in the websites schedule.
 */
@Entity
public abstract class ScheduleEntry {

    private @Id @GeneratedValue long id;

    private LocalDateTime start;
    private LocalDateTime end;

    @SuppressWarnings("unused")
    protected ScheduleEntry() {}

    /**
     * constructs a new {@link ScheduleEntry} with the given parameters
     *
     * @param start {@link LocalDateTime} that represents the entries start
     * @param end {@link LocalDateTime} that represents the entries end
     */
    public ScheduleEntry(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public long getId() {
        return id;
    }
}
