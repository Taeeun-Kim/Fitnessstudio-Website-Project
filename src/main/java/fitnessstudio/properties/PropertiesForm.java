package fitnessstudio.properties;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class PropertiesForm {

    @NotEmpty
    private String name;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private List<LocalTime> starts;

    @NotEmpty
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private List<LocalTime> ends;

    @NotNull
    private Float bounty;

    public PropertiesForm(@NotEmpty String name, List<LocalTime> starts, List<LocalTime> ends, @NotNull Float bounty) {
        this.name = name;
        this.starts = starts;
        this.ends = ends;
        this.bounty = bounty;
    }

    public String getName() {
        return name;
    }

    public Float getBounty() {
        return bounty;
    }

    public List<LocalTime> getStarts() {
        return starts;
    }

    public List<LocalTime> getEnds() {
        return ends;
    }

    public void setStarts(List<LocalTime> starts) {
        this.starts = starts;
    }

    public void setEnds(List<LocalTime> ends) {
        this.ends = ends;
    }
}
