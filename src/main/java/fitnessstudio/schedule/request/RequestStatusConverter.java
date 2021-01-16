package fitnessstudio.schedule.request;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * {@link Converter} class that converts {@link String}s to values of {@link RequestStatus}
 */
@Component
public class RequestStatusConverter implements Converter<String, RequestStatus> {
    @Override
    public RequestStatus convert(String s) {
        return RequestStatus.valueOf(s.toUpperCase());
    }
}
