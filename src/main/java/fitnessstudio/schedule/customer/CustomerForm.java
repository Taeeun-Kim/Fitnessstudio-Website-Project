package fitnessstudio.schedule.customer;

import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.customer.Customer;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that is parent-class for {@link ScheduleEntryForm}s regarding the {@link Customer}.
 */
public abstract class CustomerForm extends ScheduleEntryForm {

    @NotNull(message = "ID des Kunden darf nicht leer sein")
    private final Long customerId;

    /**
     * constructs a new {@link CustomerForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param endTime {@link LocalTime} that represents the end time
     * @param customerId the id of the {@link Customer}
     */
    public CustomerForm(String startDate, LocalTime startTime, String endDate, LocalTime endTime, Long customerId) {
        super(startDate, startTime, endDate, endTime);
        this.customerId = customerId;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
