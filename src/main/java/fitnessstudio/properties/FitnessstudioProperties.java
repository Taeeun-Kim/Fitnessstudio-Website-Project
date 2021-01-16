package fitnessstudio.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.DefaultPropertiesPersister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = FitnessstudioProperties.PREFIX)
@PropertySource("classpath:" + FitnessstudioProperties.PREFIX + ".properties")
public class FitnessstudioProperties {

    protected static final String PREFIX = "fitnessstudio";

    private String name;

    // Business Hours
    private List<String> starts;
    private List<String> ends;

    private Float bounty;

    public FitnessstudioProperties() {
        this.starts = new ArrayList<>();
        this.ends = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setBounty(Float bounty) {
        this.bounty = bounty;
    }

    public Float getBounty() {return bounty;}

    public void setStarts(List<String> starts) {
        this.starts = starts;
    }

    public void setEnds(List<String> ends) {
        this.ends = ends;
    }

    public List<AbstractMap.Entry<LocalTime, LocalTime>> getBusinessHours() {
        List<AbstractMap.Entry<LocalTime, LocalTime>> ret = new ArrayList<>();
        for (int i = 0; i < this.starts.size(); i++) {
            ret.add(new AbstractMap.SimpleEntry<>(parseOrNull(this.starts.get(i)), parseOrNull(this.ends.get(i))));
        }
        return ret;
    }

    public void setBusinessHours(List<String> starts, List<String> ends) {
        if (starts.size() != ends.size()) {
            throw new IllegalArgumentException("Lengths of starts and ends lists must be equal!");
        }

        this.starts.clear();
        this.ends.clear();

        this.starts.addAll(starts);
        this.ends.addAll(ends);
    }

    public void setBusinessHoursFromLocalTimes(List<LocalTime> starts, List<LocalTime> ends) {
        List<String> strStarts = starts.stream().map(t -> t == null ? "" : t.toString()).collect(Collectors.toList());
        List<String> strEnds = ends.stream().map(t -> t == null ? "" : t.toString()).collect(Collectors.toList());

        setBusinessHours(strStarts, strEnds);
    }

    public void save() {
        try {
            Properties properties = new Properties();
            properties.setProperty(PREFIX + ".name", name);
            for (int i = 0; i < starts.size(); i++) {
                properties.setProperty(PREFIX + ".starts[" + i + "]", starts.get(i) == null ? "" : starts.get(i));
                properties.setProperty(PREFIX + ".ends[" + i + "]", ends.get(i) == null ? "" : ends.get(i));
            }
            properties.setProperty(PREFIX + ".bounty", bounty.toString());

            File file = new ClassPathResource(PREFIX + ".properties").getFile();
            OutputStream out = new FileOutputStream(file);
            DefaultPropertiesPersister persister = new DefaultPropertiesPersister();
            persister.store(properties, out, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PropertiesForm toForm() {
        List<LocalTime> starts = this.starts.stream().map(this::parseOrNull).collect(Collectors.toList());
        List<LocalTime> ends = this.ends.stream().map(this::parseOrNull).collect(Collectors.toList());

        return new PropertiesForm(name, starts, ends, bounty);
    }

    private LocalTime parseOrNull(String str) {
        return str.isBlank() ? null : LocalTime.parse(str);
    }
}
