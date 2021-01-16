package fitnessstudio.contracts;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContractManagerTests {

    @Mock
    private ContractRepository contractRepository;

    @InjectMocks
    private ContractManager contractManager;

    private static final String TITLE = "Gold";
    private static final String DESCRIPTION = "Toll";
    private static final float FEE = 1.0f;

    private ContractForm contractForm;
    private Contract platinContract;


    @BeforeEach
    void setup() {
        contractForm = new ContractForm(TITLE, DESCRIPTION, FEE);
        platinContract = new Contract("Platin", "Super", 1.0f);
        when(contractRepository.save(any())).then(i -> i.getArgument(0));
    }

    @Test
    void addContractNotNull() {
        Contract savedContract = contractManager.addContract(contractForm);
        assertNotNull(savedContract);
    }

    @Test
    void addRightContract() {
        Contract contract = contractManager.addContract(contractForm);
        assertEquals(contract.getTitle(), TITLE);
        assertEquals(contract.getDescription(), DESCRIPTION);
        assertEquals(contract.getFee(), Money.of(FEE, "EUR"));
    }

    @Test
    void editContractNotNull() {
        Contract editedContract = contractManager.editContract(contractForm, platinContract);
        assertNotNull(editedContract);
    }

    @Test
    void editContractRight() {
        Contract editedContract = contractManager.editContract(contractForm, platinContract);
        assertEquals(editedContract.getTitle(), TITLE);
        assertEquals(editedContract.getDescription(), DESCRIPTION);
        assertEquals(editedContract.getFee(), Money.of(FEE, "EUR"));
    }
}
