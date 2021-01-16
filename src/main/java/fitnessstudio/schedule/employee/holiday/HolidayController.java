package fitnessstudio.schedule.employee.holiday;

import fitnessstudio.schedule.ScheduleManager;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for the {@link ScheduleEntry} {@link Holiday}. It controls {@link User}-interactions performable
 * with {@link Holiday}s.
 */
@Controller
@RequestMapping("/admin/holidays")
public class HolidayController {

    private final ScheduleManager scheduleManager;

    /**
     * constructs a new {@link HolidayController} with the given parameters
     *
     * @param scheduleManager {@link ScheduleManager} for database interaction
     */
    public HolidayController(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    /**
     * This method processes the get-request of "/admin/holidays".
     *
     * @param model {@link Model}
     * @return a view of all holidays grouped by their status
     */
    @GetMapping("")
    public String holidays(Model model) {
        model.addAttribute("pending", scheduleManager.getHolidayRequests().findByStatus(RequestStatus.PENDING));
        model.addAttribute("accepted", scheduleManager.getHolidayRequests().findByStatus(RequestStatus.ACCEPTED));
        model.addAttribute("rejected", scheduleManager.getHolidayRequests().findByStatus(RequestStatus.REJECTED));
        return "pages/admin/holidays";
    }

    /**
     * This method processes the post-request of "/admin/holidays/{id}". It finds the referenced {@link Holiday} by the
     * given id and updates the status accordingly.
     *
     * @param id the id of the {@link Holiday} to update
     * @param status the {@link RequestStatus} going to be set
     * @return a view of all holidays grouped by their status containing the newly updated {@link Holiday}
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('BOSS')")
    public String updateHoliday(@PathVariable Long id, @RequestParam RequestStatus status) {
        if (!scheduleManager.getHolidayRequests().findById(id).orElseThrow().getStatus()
                .equals(RequestStatus.ACCEPTED)) {

            scheduleManager.setHolidayRequestStatus(id, status);
        }
        return "redirect:/admin/holidays";
    }
}
