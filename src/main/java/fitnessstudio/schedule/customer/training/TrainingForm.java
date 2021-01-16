package fitnessstudio.schedule.customer.training;

import fitnessstudio.schedule.customer.CustomerForm;
import fitnessstudio.schedule.entry.ScheduleEntryForm;
import fitnessstudio.user.User;
import fitnessstudio.user.customer.Customer;

import java.time.LocalTime;

/**
 * {@link ScheduleEntryForm} class that collects {@link User} input for {@link Training} creation.
 */
public class TrainingForm extends CustomerForm {

    /**
     * constructs a new {@link TrainingForm} with the given parameters
     *
     * @param startDate {@link String} in 'ISO_LOCAL_DATE' format that represents the start date
     * @param startTime {@link LocalTime} that represents the start time
     * @param endTime {@link LocalTime} that represents the end time
     * @param customerId the id of the {@link Customer} who is going to be training
     */
    public TrainingForm(String startDate, LocalTime startTime, LocalTime endTime, Long customerId) {
        super(startDate, startTime, startDate, endTime, customerId);
    }
}
