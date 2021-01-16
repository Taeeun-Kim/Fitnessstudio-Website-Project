package fitnessstudio.schedule;

import fitnessstudio.AbstractIntegrationTests;
import fitnessstudio.schedule.customer.training.TrainingForm;
import fitnessstudio.schedule.customer.trial.TrialRequestForm;
import fitnessstudio.schedule.employee.shift.ShiftForm;
import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import fitnessstudio.user.UserManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
public class ScheduleIntegrationTests extends AbstractIntegrationTests {

    private Schedule schedule;

    @Autowired
    ScheduleManager scheduleManager;

    @Autowired
    UserManager userManager;

    @BeforeEach
    void init() {
        List<User> users = List.of(
                userManager.getEmployees().findAll().stream().findFirst().orElseThrow(),
                userManager.getCustomers().findAll().stream().findFirst().orElseThrow()
        );
        schedule = new Schedule(users, scheduleManager, userManager, Optional.empty());
    }

    @Test
    void ScheduleNotNull() {
        assertNotNull(schedule);
    }

    @Test
    void ScheduleEntriesNotNull() {
        assertNotNull(schedule.getEntries());
    }

    @Test
    void ScheduleHasRightProperties() {
        assertEquals(4, schedule.getEntries().toList().size());
    }

    @Test
    void canFitThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> schedule.canFit(null));
    }

    @Test
    void canFitDecidesRightForEmployee() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime endTime = startTime.plusMinutes(360);
        ShiftForm sF = new ShiftForm(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(), endTime.toLocalTime(), userManager.getEmployees().findAll().stream().findFirst().orElseThrow().getId(), Department.TRAINING);
        assertFalse(schedule.canFit(sF));
    }

    @Test
    void canFitDecidesRightForCustomer() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime endTime = startTime.plusMinutes(360);
        TrainingForm tF = new TrainingForm(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(), endTime.toLocalTime(), userManager.getCustomers().findAll().stream().findFirst().orElseThrow().getId());
        assertFalse(schedule.canFit(tF));
    }

    @Test
    void canFitDecidesRightForBoth() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime endTime = startTime.plusMinutes(360);
        TrialRequestForm trF = new TrialRequestForm(startTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(), endTime.toLocalTime(), userManager.getCustomers().findAll().stream().findFirst().orElseThrow().getId(), userManager.getEmployees().findAll().stream().findFirst().orElseThrow().getId(), RequestStatus.PENDING);
        assertFalse(schedule.canFit(trF));
    }

    @Test
    void canFitEditedDecidesRight() {
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 0));
        LocalDateTime endTime = startTime.plusMinutes(120);
        ShiftForm sF = new ShiftForm(startTime.format(DateTimeFormatter.ISO_LOCAL_DATE), startTime.toLocalTime(), endTime.toLocalTime(), userManager.getEmployees().findAll().stream().findFirst().orElseThrow().getId(), Department.TRAINING);
        assertTrue(schedule.canFitEdited(sF, scheduleManager.getShifts().findByEmployee(userManager.getEmployees().findAll().stream().findFirst().orElseThrow()).stream().findFirst().orElseThrow().getId()));
    }
}
