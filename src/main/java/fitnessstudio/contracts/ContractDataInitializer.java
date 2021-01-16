package fitnessstudio.contracts;

import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(9)
public class ContractDataInitializer implements DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ContractDataInitializer.class);

    private final ContractRepository contractRepository;

    ContractDataInitializer(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    @Override
    public void initialize() {

        if (contractRepository.findAll().iterator().hasNext()) {
            return;
        }

        LOG.info("Creating default contracts.");

        contractRepository.save(new Contract("Standard", "Lorem ipsum dolor sit amet, consectetur adipiscing" +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam," +
                " quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure" +
                " dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum.", 9.99f));
        contractRepository.save(new Contract("Gold", "Lorem ipsum dolor sit amet, consectetur adipiscing" +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam," +
                " quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure" +
                " dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum.", 19.99f));
        contractRepository.save(new Contract("Platinum", "Lorem ipsum dolor sit amet, consectetur adipiscing" +
                "elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam," +
                " quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure" +
                " dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum.", 29.99f));
    }
}
