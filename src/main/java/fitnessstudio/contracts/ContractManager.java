package fitnessstudio.contracts;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ContractManager {

    private ContractRepository contractRepository;

    public ContractManager(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public Contract addContract(ContractForm contractForm) {

        var title = contractForm.getTitle();
        var description = contractForm.getDescription();
        var fee = contractForm.getFee();

        return contractRepository.save(new Contract(title, description, fee));
    }

    public Contract editContract(ContractForm contractForm, Contract contract) {
        contract.setTitle(contractForm.getTitle());
        contract.setDescription(contractForm.getDescription());
        contract.setFee(contractForm.getFee());

        return contractRepository.save(contract);
    }

    public Optional<Contract> findByTitle(String title) {
        return contractRepository.findByTitle(title);
    }

    public void deleteContract(Contract contract) {
        contractRepository.deleteById(contract.getId());
    }

    public Iterable<Contract> findAll() {
        return contractRepository.findAll();
    }

    public Optional<Contract> findById(Long id) {
        return contractRepository.findById(id);
    }
}
