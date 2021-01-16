package fitnessstudio.schedule.customer.trial;

import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.schedule.request.RequestStatus;
import fitnessstudio.user.User;
import fitnessstudio.user.customer.Customer;
import fitnessstudio.user.employee.Employee;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that collects {@link User} input for {@link TrialRequest} creation.
 */
public class TrialRequestForm extends ScheduleEntryForm {

    @NotNull(message = "ID des Kunden darf nicht leer sein")
    private final Long customerId;

    @NotNull(message = "ID des Mitarbeiters darf nicht leer sein")
    private final Long employeeId;

    private final RequestStatus status;

    /**
     * constructs a new {@link TrialRequestForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endTime {@link LocalTime} that represents the end time
     * @param customerId the id of the {@link Customer} who is going to be instructed
     * @param employeeId the id of the {@link Employee} who is going to be instructing
     * @param status {@link RequestStatus} indicating the status
     */
    public TrialRequestForm(String startDate, LocalTime startTime, LocalTime endTime, Long customerId,
                            Long employeeId, RequestStatus status) {
        super(startDate, startTime, startDate, endTime);
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.status = status;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Long getCustomerId() { return customerId; }

    public RequestStatus getStatus() {
        return status;
    }
}
