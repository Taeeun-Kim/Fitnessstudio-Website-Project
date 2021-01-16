package fitnessstudio.schedule;

import fitnessstudio.schedule.customer.training.TrainingForm;
import fitnessstudio.schedule.employee.shift.ShiftForm;
import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.user.User;
import fitnessstudio.user.UserManager;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * {@link DataInitializer} class that generates standard {@link ScheduleEntry}s on application startup.
 */
@Component
public class ScheduleDataInitializer implements DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleDataInitializer.class);

    private final ScheduleManager scheduleManager;
    private final UserManager userManager;

    /**
     * constructs a new {@link ScheduleDataInitializer} with the given parameters
     *
     * @param scheduleManager {@link ScheduleManager} for database interaction
     * @param userManager {@link UserManager} to get {@link User} information
     */
    public ScheduleDataInitializer(ScheduleManager scheduleManager, UserManager userManager) {
        Assert.notNull(scheduleManager, "ScheduleManager must not be null!");
        Assert.notNull(userManager, "UserManager must not be null!");

        this.scheduleManager = scheduleManager;
        this.userManager = userManager;
    }

    @Override
    public void initialize() {

        // Skip creation if database is already populated
        if (scheduleManager.getShifts().findAll().iterator().hasNext()) {
            return;
        }

        LOG.info("Creating default schedule shifts.");

        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime endTime = startTime.plusMinutes(360);

        LocalDateTime startTime2 = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        LocalDateTime endTime2 = startTime2.plusMinutes(420);

        List.of(new ShiftForm(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(),
                        endTime.toLocalTime(), userManager.getEmployees().findAll().stream().findFirst().get().getId(),
                        Department.SHOP),
                new ShiftForm(startTime2.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime2.toLocalTime(),
                        endTime2.toLocalTime(), userManager.getEmployees().findAll().stream().findFirst().get().getId(),
                        Department.SHOP)
        ).forEach(shift -> scheduleManager.setShift(shift, null));

        List.of(new TrainingForm(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(),
                        endTime.toLocalTime(), userManager.getCustomers().findAll().stream().findFirst().get().getId()),
                new TrainingForm(startTime2.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime2.toLocalTime(),
                        endTime2.toLocalTime(), userManager.getCustomers().findAll().stream().findFirst().get().getId())
        ).forEach(training -> scheduleManager.setTraining(training, null));

    }

}
