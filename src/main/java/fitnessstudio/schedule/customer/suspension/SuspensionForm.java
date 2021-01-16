package fitnessstudio.schedule.customer.suspension;

import fitnessstudio.schedule.customer.CustomerForm;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.User;
import fitnessstudio.user.customer.Customer;

import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that collects {@link User} input for {@link Suspension} creation.
 */
public class SuspensionForm extends CustomerForm {

    /**
     * constructs a new {@link SuspensionForm} with the given parameters
     * 
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endDate {@link String} in 'ISO_LOCAL_DATE' format that represents the end date
     * @param endTime {@link LocalTime} that represents the end time
     * @param customerId the id of the {@link Customer} to be suspended
     */
    public SuspensionForm(String startDate, LocalTime startTime, String endDate, LocalTime endTime, Long customerId) {
        super(startDate, startTime, endDate, endTime, customerId);
    }
}
