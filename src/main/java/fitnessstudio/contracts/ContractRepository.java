package fitnessstudio.contracts;


import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ContractRepository extends CrudRepository<Contract, Long> {
    Optional<Contract> findByTitle(String title);
}
