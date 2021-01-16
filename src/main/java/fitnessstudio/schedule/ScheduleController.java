package fitnessstudio.schedule;

import fitnessstudio.schedule.customer.suspension.Suspension;
import fitnessstudio.schedule.customer.training.Training;
import fitnessstudio.schedule.customer.training.TrainingForm;
import fitnessstudio.schedule.customer.trial.TrialRequest;
import fitnessstudio.schedule.customer.trial.TrialRequestForm;
import fitnessstudio.schedule.employee.holiday.Holiday;
import fitnessstudio.schedule.employee.holiday.HolidayForm;
import fitnessstudio.schedule.employee.shift.Shift;
import fitnessstudio.schedule.employee.shift.ShiftForm;
import fitnessstudio.schedule.employee.shift.ShiftRepository;
import fitnessstudio.schedule.employee.shift.department.Department;
import fitnessstudio.schedule.entry.ScheduleEntry;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import fitnessstudio.user.UserManager;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;
import org.salespointframework.time.BusinessTime;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static fitnessstudio.user.UserManager.*;

/**
 * Controller class for the websites schedule. It controls {@link User}-interactions performable with
 * {@link Shift}, {@link Holiday}, {@link Training}, {@link Suspension}
 * and {@link TrialRequest}.
 */
@Controller
public class ScheduleController {

    private final ScheduleManager scheduleManager;
    private final UserManager userManager;
    private BusinessTime businessTime;

    /**
     * constructs a new {@link ScheduleController} with the given parameters
     *
     * @param scheduleManager {@link ScheduleManager} for database interaction
     * @param userManager {@link UserManager} to get {@link User} information
     * @param businessTime applications {@link BusinessTime}
     */
    public ScheduleController(ScheduleManager scheduleManager, UserManager userManager, BusinessTime businessTime) {
        Assert.notNull(scheduleManager, "ScheduleManager must not be null!");
        Assert.notNull(userManager, "UserManager must not be null!");

        this.scheduleManager = scheduleManager;
        this.userManager = userManager;
        this.businessTime = businessTime;
    }

    /**
     * This method processes the get-request of "/schedule".
     *
     * @param model {@link Model}
     * @param userAccount {@link UserAccount} to find the {@link Employee}
     * @param filter Optional filter. Can be 'department' to filter for a specific {@link Department}, 'personal' to filter for
     *               only personal {@link ScheduleEntry}s or empty to not filter.
     * @param department Attribute for the {@link Department} filter. Can be 'training', 'shop' or 'cleaning' and filters
     *                   displayed {@link Shift}s for the given {@link Department}.
     * @param date {@link LocalDate} to choose the start of the schedule
     * @return the {@link User}s chosen view of his schedule
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/schedule")
    @PreAuthorize("isAuthenticated()")
    public String scheduleGet(Model model, @LoggedIn Optional<UserAccount> userAccount,
                              @RequestParam(required = false, defaultValue = "") String filter,
                              @RequestParam(required = false, defaultValue = "") Department department,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> date) {
        // Redirect if not authenticated
        if (userAccount.isEmpty()) {
            return "redirect:/";
        }

        // Customer
        if (userAccount.get().hasRole(ROLE_CUSTOMER)) {
            Customer customer = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow();
            Schedule schedule = new Schedule(List.of(customer), scheduleManager, userManager,
                    Optional.empty());

            model.addAttribute("customerId", customer.getId());
            model.addAttribute("employees", userManager.getEmployees().findAll());
            TrainingForm trainingForm = new TrainingForm(businessTime.getTime().toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), businessTime.getTime().toLocalTime(),
                    businessTime.getTime().toLocalTime(), customer.getId());
            model.addAttribute("trainingForm", trainingForm);
            model.addAttribute("trainingList", schedule.getEntries().filter(Training.class::isInstance));
            model.addAttribute("suspensionList", schedule.getEntries().filter(Suspension.class::isInstance));
            TrialRequestForm trialRequestForm = new TrialRequestForm(businessTime.getTime().toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), businessTime.getTime().toLocalTime(),
                    businessTime.getTime().toLocalTime(), customer.getId(), (long) 1, RequestStatus.PENDING);
            model.addAttribute("trialRequestForm", trialRequestForm);
            model.addAttribute("trialList", schedule.getEntries().filter(TrialRequest.class::isInstance));
        } else

        // Employees
        if (userAccount.get().hasRole(ROLE_EMPLOYEE) || userAccount.get().hasRole(ROLE_BOSS)) {
            Employee employee = userManager.getEmployees().findByUserAccount(userAccount.get()).orElseThrow();
            Schedule schedule = new Schedule(List.of(employee), scheduleManager, userManager,
                    Optional.empty());

            model.addAttribute("employeeId", employee.getId());
            model.addAttribute("departments", Department.values());
            ShiftForm shiftForm = new ShiftForm(businessTime.getTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    businessTime.getTime().toLocalTime(), businessTime.getTime().toLocalTime(), employee.getId(),
                    Department.TRAINING);
            model.addAttribute("shiftForm", shiftForm);
            model.addAttribute("trialList", schedule.getEntries().filter(TrialRequest.class::isInstance));
            model.addAttribute("holidayList", schedule.getEntries().filter(Holiday.class::isInstance));
            model.addAttribute("trialRequestList", schedule.getEntries()
                    .filter(TrialRequest.class::isInstance)
                    .filter(trial -> ((TrialRequest)trial).getStatus() == RequestStatus.PENDING));

            // Filter
            ShiftRepository shifts = scheduleManager.getShifts();
            if (filter.equalsIgnoreCase("PERSONAL")) {
                // Assigned Shifts
                model.addAttribute("shiftList", shifts.findByEmployee(employee));
            } else {
                if (department == null) {
                    model.addAttribute("shiftList", shifts.findAll());
                } else {
                    model.addAttribute("shiftList", shifts.findByDepartment(department));
                }
            }
        }

        model.addAttribute("today", date.orElse(businessTime.getTime().toLocalDate()));
        model.addAttribute("bt", businessTime);
        model.addAttribute("hours", scheduleManager.getProperties().getBusinessHours());

        return "pages/schedule/index";
    }

    // Shifts

    /**
     * This method processes the post-request of "/schedule/shift". It creates a new {@link Shift} with the information
     * from the {@link ShiftForm} and saves it.
     *
     * @param model {@link Model}
     * @param userAccount {@link UserAccount} to find the {@link Employee}
     * @param shiftForm {@link ShiftForm} to build the {@link Shift} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly added {@link Shift}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/schedule/shift")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String addShiftPost(Model model, @LoggedIn Optional<UserAccount> userAccount,
                               @Valid ShiftForm shiftForm, Errors result) {
        validateScheduleEntryForm(shiftForm, result);

        Employee employee = userManager.getEmployees().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        Schedule s = new Schedule(List.of(employee), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFit(shiftForm))) {
            result.rejectValue("endTime", "endTime.cantFit",
                    "Zeitraum überschneidet sich mit anderen Dienstplaneinträgen");
        }

        if (result.hasErrors()) {
            model.addAttribute("today", businessTime.getTime().toLocalDate());
            model.addAttribute("bt", businessTime);
            model.addAttribute("hours", scheduleManager.getProperties().getBusinessHours());
            model.addAttribute("employeeId", employee.getId());
            model.addAttribute("departments", Department.values());
            model.addAttribute("holidayList", s.getEntries().filter(Holiday.class::isInstance));
            model.addAttribute("trialList", s.getEntries().filter(TrialRequest.class::isInstance));
            model.addAttribute("trialRequestList", s.getEntries()
                    .filter(TrialRequest.class::isInstance)
                    .filter(trial -> ((TrialRequest)trial).getStatus() == RequestStatus.PENDING));
            model.addAttribute("shiftList", scheduleManager.getShifts().findAll());
            return "pages/schedule/index";
        }
        scheduleManager.setShift(shiftForm, null);
        return "redirect:/schedule";
    }

    /**
     * This method processes the get-request of "/schedule/shift/{id}".
     *
     * @param model {@link Model}
     * @param id the id that specifies which {@link Shift} to edit
     * @param userAccount the {@link Employee}s {@link UserAccount} (used to determine the employeeId)
     * @return a new view that enables the {@link Employee} to edit the chosen {@link Shift}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/schedule/shift/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String editShiftGet(Model model, @PathVariable Long id, @LoggedIn Optional<UserAccount> userAccount) {
        if (userAccount.isEmpty()) {
            return "redirect:/schedule";
        }
        Long loggedInEmployeeId = userManager.getEmployees().findByUserAccount(userAccount.get()).orElseThrow().getId();
        if (!loggedInEmployeeId.equals(scheduleManager.getShifts().findById(id).orElseThrow().getEmployee().getId())) {
            return "redirect:/schedule";
        }

        Shift shift = scheduleManager.getShifts().findById(id).orElseThrow();
        ShiftForm shiftForm = new ShiftForm(shift.getStart().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                shift.getStart().toLocalTime(), shift.getEnd().toLocalTime(), shift.getEmployee().getId(),
                shift.getDepartment());

        model.addAttribute("shift", shift);
        model.addAttribute("shiftForm", shiftForm);
        model.addAttribute("employeeId", loggedInEmployeeId);
        model.addAttribute("departmentList", Department.values());
        return "pages/schedule/editShift";
    }

    /**
     * This method processes the post-request of "/schedule/shift/{id}" with the parameter 'save'.
     * It finds the referenced {@link Shift} by the given id, edits its parameters to match the information given in the
     * {@link ShiftForm} and saves it.
     *
     * @param model {@link Model}
     * @param id the id that specifies which {@link Shift} to edit
     * @param userAccount {@link UserAccount} to find the {@link Employee}
     * @param shiftForm {@link ShiftForm} to build the {@link Shift} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly edited {@link Shift}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping(value = "/schedule/shift/{id}", params = "save")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String editShiftPost(Model model, @PathVariable Long id, @LoggedIn Optional<UserAccount> userAccount,
                                @Valid ShiftForm shiftForm, Errors result) {
        Employee employee = userManager.getEmployees().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        if (!(employee.getId() == scheduleManager.getShifts().findById(id).orElseThrow().getEmployee().getId())) {
            return "redirect:/schedule";
        }

        validateScheduleEntryForm(shiftForm, result);

        Schedule s = new Schedule(List.of(employee), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFitEdited(shiftForm, id))) {
            result.rejectValue("endTime", "endTime.invalid",
                    "Zeitraum überschneidet sich mit anderen Dienstplaneinträgen");
        }

        if (result.hasErrors()) {
            Shift shift = scheduleManager.getShifts().findById(id).orElseThrow();

            model.addAttribute("shift", shift);
            model.addAttribute("employeeId", employee.getId());
            model.addAttribute("departmentList", Department.values());
            return "pages/schedule/editShift";
        }

        scheduleManager.setShift(shiftForm, id);
        return "redirect:/schedule";
    }

    /**
     * This method processes the post-request of "/schedule/shift/{id}" with the parameter 'delete'.
     * It finds the referenced {@link Shift} by the given id and deletes it.
     *
     * @param id the id that specifies which {@link Shift} to remove
     * @return the standard view of the users schedule not containing the deleted {@link Shift}
     */
    @PostMapping(value = "/schedule/shift/{id}", params = "delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String removeShift(@PathVariable Long id) {
        scheduleManager.removeShift(id);
        return "redirect:/schedule";
    }

    // Holidays

    /**
     * This method processes the get-request of "/schedule/holiday".
     *
     * @param model {@link Model}
     * @param userAccount the {@link Employee}s {@link UserAccount} (used to determine the employeeId)
     * @param holidayForm parameter for validation
     * @return a new view that enables the {@link Employee} to specify certain holiday parameters such as startDate,
     *         endDate, etc.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/schedule/holiday")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String requestHolidayGet(Model model, @LoggedIn Optional<UserAccount> userAccount,
                                    HolidayForm holidayForm) {
        if (userAccount.isEmpty()) {
            return "redirect:/";
        }
        Employee employee = userManager.getEmployees().findByUserAccount(userAccount.get()).orElseThrow();
        Holiday holiday = new Holiday(businessTime.getTime().toLocalDate(),
                businessTime.getTime().toLocalDate(), employee, RequestStatus.PENDING);
        model.addAttribute("holiday", holiday);
        model.addAttribute("holidayForm", holidayForm);
        model.addAttribute("employeeId", employee.getId());
        return "pages/schedule/requestHoliday";
    }

    /**
     * This method processes the post-request of "/schedule/holiday". It creates a new {@link Holiday} with the
     * information from the {@link HolidayForm} and saves it.
     *
     * @param model {@link Model}
     * @param userAccount the {@link Employee}s {@link UserAccount} (used to determine the employeeId)
     * @param holidayForm {@link HolidayForm} to build the {@link Holiday} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly added {@link Holiday}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/schedule/holiday")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String requestHolidayPost(Model model, @LoggedIn Optional<UserAccount> userAccount,
                                     @Valid HolidayForm holidayForm, Errors result) {
        validateScheduleEntryForm(holidayForm, result);

        Employee employee = userManager.getEmployees().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        Schedule s = new Schedule(List.of(employee), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFit(holidayForm))) {
            result.rejectValue("endDate", "endDate.invalid",
                    "Zeitraum überschneidet sich mit anderen Dienstplaneinträgen");
        }

        if (result.hasErrors()) {
            Holiday holiday = new Holiday(LocalDate.parse(holidayForm.getStartDate()),
                    LocalDate.parse(holidayForm.getEndDate()), employee, RequestStatus.PENDING);
            model.addAttribute("holiday", holiday);
            model.addAttribute("holidayForm", holidayForm);
            model.addAttribute("employeeId", employee.getId());
            return "pages/schedule/requestHoliday";
        }

        scheduleManager.addHolidayRequest(holidayForm);
        return "redirect:/schedule";
    }

    /**
     * This method processes the post-request of "/schedule/holiday/{id}/delete". It finds the referenced
     * {@link Holiday} by the given id and deletes it.
     *
     * @param id the id that specifies which {@link Holiday} to remove
     * @return the standard view of the users schedule not containing the deleted {@link Holiday}
     */
    @PostMapping("/schedule/holiday/{id}/delete")
    @PreAuthorize("hasRole('BOSS')")
    public String removeHoliday(@PathVariable Long id) {
        scheduleManager.removeHolidayRequest(id);
        return "redirect:/schedule";
    }

    // Training

    /**
     * This method processes the post-request of "/schedule/training". It creates a new {@link Training} with the
     * information from the {@link TrainingForm} and saves it.
     *
     * @param model {@link Model}
     * @param userAccount the {@link Customer}s {@link UserAccount} (used to determine the customerId)
     * @param trainingForm {@link TrainingForm} to build the {@link Training} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly added {@link Training}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/schedule/training")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String addTrainingPost(Model model, @LoggedIn Optional<UserAccount> userAccount,
                                  @Valid TrainingForm trainingForm, Errors result) {
        validateScheduleEntryForm(trainingForm, result);

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        Schedule s = new Schedule(List.of(customer), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFit(trainingForm))) {
            result.rejectValue("endTime", "endTime.invalid",
                    "Zeitraum passt nicht in die Öffnungszeiten oder überschneidet sich mit anderen " +
                            "Einträgen im Trainingsplan");
        }

        if (result.hasErrors()) {
            model.addAttribute("bt", businessTime);
            model.addAttribute("today", businessTime.getTime().toLocalDate());
            model.addAttribute("customerId", customer.getId());
            model.addAttribute("hours", scheduleManager.getProperties().getBusinessHours());
            model.addAttribute("trainingList", s.getEntries().filter(Training.class::isInstance));
            TrialRequestForm trialRequestForm = new TrialRequestForm(businessTime.getTime().toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), businessTime.getTime().toLocalTime(),
                    businessTime.getTime().toLocalTime(), customer.getId(), (long) 1, RequestStatus.PENDING);
            model.addAttribute("trialRequestForm", trialRequestForm);
            model.addAttribute("employees", userManager.getEmployees().findAll());
            model.addAttribute("suspensionList", s.getEntries().filter(Suspension.class::isInstance));
            model.addAttribute("trialList", s.getEntries().filter(TrialRequest.class::isInstance));
            return "pages/schedule/index";
        }

        scheduleManager.setTraining(trainingForm, null);
        return "redirect:/schedule";
    }

    /**
     * This method processes the get-request of "/schedule/training/{id}".
     *
     * @param model {@link Model}
     * @param id the id that specifies which {@link Training} to edit
     * @param userAccount the {@link Customer}s {@link UserAccount} (used to determine the customerId)
     * @return a new view that enables the {@link Customer} to edit the chosen {@link Training}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @GetMapping("/schedule/training/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String editTrainingGet(Model model, @PathVariable Long id,
                                  @LoggedIn Optional<UserAccount> userAccount) {
        if (userAccount.isEmpty()) {
            return "redirect:/schedule";
        }
        Long customerId = userManager.getCustomers().findByUserAccount(userAccount.get()).orElseThrow().getId();
        if (!customerId.equals(scheduleManager.getTrainings().findById(id).orElseThrow().getCustomer().getId())) {
            return "redirect:/schedule";
        } else {
            Training training = scheduleManager.getTrainings().findById(id).orElseThrow();
            TrainingForm trainingForm = new TrainingForm(training.getStart().toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), training.getStart().toLocalTime(),
                    training.getEnd().toLocalTime(), customerId);
            model.addAttribute("training", training);
            model.addAttribute("trainingForm", trainingForm);
            model.addAttribute("customerId", customerId);
            return "pages/schedule/editTraining";
        }
    }

    /**
     * This method processes the post-request of "/schedule/training/{id}" with the parameter 'save'.
     * It finds the referenced {@link Training} by the given id, edits its parameters to match the information given in
     * the {@link TrainingForm} and saves it.
     *
     * @param model {@link Model}
     * @param id the id that specifies which {@link Training} to edit
     * @param userAccount the {@link Customer}s {@link UserAccount} (used to determine the customerId)
     * @param trainingForm {@link TrainingForm} to build the {@link Training} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly edited {@link Training}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping(value = "/schedule/training/{id}", params = "save")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String editTrainingPost(Model model, @PathVariable Long id, @LoggedIn Optional<UserAccount> userAccount,
                                   @Valid TrainingForm trainingForm, Errors result) {
        validateScheduleEntryForm(trainingForm, result);

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        Schedule s = new Schedule(List.of(customer), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFitEdited(trainingForm, id))) {
            result.rejectValue("endTime", "endTime.invalid",
                    "Zeitraum passt nicht in die Öffnungszeiten oder überschneidet sich mit anderen " +
                            "Einträgen im Trainingsplan");
        }

        if (result.hasErrors()) {
            Training training = scheduleManager.getTrainings().findById(id).orElseThrow();
            model.addAttribute("training", training);
            model.addAttribute("trainingForm", trainingForm);
            model.addAttribute("customerId", customer.getId());
            return "pages/schedule/editTraining";
        }

        scheduleManager.setTraining(trainingForm, id);
        return "redirect:/schedule";
    }

    /**
     * This method processes the post-request of "/schedule/training/{id}" with the parameter 'delete'.
     * It finds the referenced {@link Training} by the given id and deletes it.
     *
     * @param id the id that specifies which {@link Training} to remove
     * @return the standard view of the users schedule not containing the deleted {@link Training}
     */
    @PostMapping(value = "/schedule/training/{id}", params = "delete")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String removeTraining(@PathVariable Long id) {
        scheduleManager.removeTraining(id);
        return "redirect:/schedule";
    }

//    /**
//     * This method processes the post-request of "/schedule/suspension". It creates a new {@link Suspension} with the
//     * information from the {@link SuspensionForm} and saves it. This results in a {@link Suspension} of the
//     * {@link Customer}s {@link UserAccount} which lasts exactly one month and prevents him from being able to log in to
//     * the website.
//     *
//     * @param suspensionForm {@link SuspensionForm} to build the {@link Suspension} from
//     * @param result possible {@link Errors}
//     * @return a view of the websites login-Site
//     */
//    @PostMapping("/schedule/suspension")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public String addSuspension(SuspensionForm suspensionForm, Errors result) {
//        Schedule s = new Schedule(
//                List.of(userManager.getCustomers().findById(suspensionForm.getCustomerId()).orElseThrow()),
//                scheduleManager,
//                userManager,
//                Optional.empty()
//        );
//        if (s.canFit(suspensionForm)) {
//            scheduleManager.addSuspension(suspensionForm);
//        }
//        return "redirect:/schedule";
//    }

//    /**
//     * This method processes the post-request of "/schedule/suspension/{id}/delete". It finds the referenced
//     * {@link Suspension} by the given id and deletes it.
//     *
//     * @param id the id that specifies which {@link Suspension} to remove
//     * @return the standard view of the users schedule
//     */
//    @PostMapping("/schedule/suspension/{id}/delete")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public String removeSuspension(@PathVariable Long id) {
//        scheduleManager.removeSuspension(id);
//        return "redirect:/schedule";
//    }

    // Trial Requests

    /**
     * This method processes the post-request of "/schedule/trial". It creates a new {@link TrialRequest} with the
     * information from the {@link TrialRequestForm} and saves it.
     *
     * @param model {@link Model}
     * @param userAccount the {@link Customer}s {@link UserAccount} (used to determine the customerId)
     * @param trialRequestForm {@link TrialRequestForm} to build the {@link TrialRequest} from
     * @param result possible {@link Errors}
     * @return the standard view of the users schedule containing the newly added {@link TrialRequest}
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @PostMapping("/schedule/trial")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String requestTrialPost(Model model, @LoggedIn Optional<UserAccount> userAccount,
                                   @Valid TrialRequestForm trialRequestForm, Errors result) {
        validateScheduleEntryForm(trialRequestForm, result);

        Customer customer = userManager.getCustomers().findByUserAccount(userAccount.orElseThrow()).orElseThrow();
        Employee employee = userManager.getEmployees().findById(trialRequestForm.getEmployeeId()).orElseThrow();
        Schedule s = new Schedule(List.of(customer, employee), scheduleManager, userManager, Optional.empty());

        if (!(result.hasErrors() || s.canFit(trialRequestForm))) {
            result.rejectValue("employeeId", "employeeId.invalid",
                    "Zeitraum passt nicht in die Öffnungszeiten, überschneidet sich mit anderen " +
                            "Einträgen im Trainingsplan oder der Trainer ist nicht verfügbar");
        }

        if (result.hasErrors()) {
            model.addAttribute("today", businessTime.getTime().toLocalDate());
            model.addAttribute("bt", businessTime);
            model.addAttribute("hours", scheduleManager.getProperties().getBusinessHours());
            model.addAttribute("customerId", customer.getId());
            model.addAttribute("employees", userManager.getEmployees().findAll());
            Schedule s2 = new Schedule(List.of(customer), scheduleManager, userManager, Optional.empty());
            model.addAttribute("trainingList", s2.getEntries().filter(Training.class::isInstance));
            model.addAttribute("trialRequestForm", trialRequestForm);
            TrainingForm trainingForm = new TrainingForm(businessTime.getTime().toLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE), businessTime.getTime().toLocalTime(),
                    businessTime.getTime().toLocalTime(), customer.getId());
            model.addAttribute("trainingForm", trainingForm);
            model.addAttribute("suspensionList", s.getEntries().filter(Suspension.class::isInstance));
            model.addAttribute("trialList", s.getEntries().filter(TrialRequest.class::isInstance));
            return "pages/schedule/index";
        }

        scheduleManager.addTrialRequest(trialRequestForm);
        return "redirect:/schedule";
    }

    /**
     * This method processes the post-request of "/schedule/trial/{id}". It finds the referenced {@link TrialRequest}
     * by the given id and sets its {@link RequestStatus} to 'ACCEPTED'.
     *
     * @param id the id that specifies which {@link TrialRequest} to accept
     * @return the standard view of the users schedule containing the newly accepted {@link TrialRequest}
     */
    @PostMapping("/schedule/trial/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String acceptTrial(@PathVariable Long id) {
        if (!scheduleManager.getTrialRequests().findById(id).orElseThrow().getStatus()
                .equals(RequestStatus.ACCEPTED)) {
            scheduleManager.setTrialRequestStatus(id, RequestStatus.ACCEPTED);
        }
        return "redirect:/schedule";
    }

    /**
     * This method processes the post-request of "/schedule/trial/{id}/delete". It finds the referenced
     * {@link TrialRequest} by the given id and deletes it.
     *
     * @param id the id that specifies which {@link TrialRequest} to remove
     * @return the standard view of the users schedule not containing the deleted {@link TrialRequest}
     */
    @PostMapping("/schedule/trial/{id}/delete")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public String removeTrial(@PathVariable Long id) {
        scheduleManager.removeTrialRequest(id);
        return "redirect:/schedule";
    }

    //Helper functions

    /**
     * This method validates a {@link ScheduleEntryForm}
     * @param form {@link ScheduleEntryForm} to validate
     * @param result {@link Errors}
     */
    private void validateScheduleEntryForm(ScheduleEntryForm form, Errors result) {
        if (LocalDate.parse(form.getStartDate()).isBefore(ChronoLocalDate.from(businessTime.getTime()))) {
            result.rejectValue("startDate", "startDate.inPast",
                    "Startdatum darf nicht in der Vergangenheit liegen");
        }

        if (form.getStartTime().isAfter(form.getEndTime())) {
            result.rejectValue("endTime", "endTime.invalid", "Ende muss nach dem Beginn liegen");
        }

        if (LocalDate.parse(form.getStartDate()).isAfter(LocalDate.parse(form.getEndDate()))) {
            result.rejectValue("endDate", "endDate.invalid", "Ende muss vor dem Beginn liegen");
        }
    }
}
