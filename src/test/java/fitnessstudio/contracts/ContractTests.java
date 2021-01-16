package fitnessstudio.contracts;

import static org.junit.jupiter.api.Assertions.*;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class ContractTests {

    private static final String TITLE = "Gold";
    private static final String DESCRIPTION = "Toll";
    private static final float FEE = 1.0f;

    @Test
    void addRightContent() {
        Contract contract = new Contract(TITLE, DESCRIPTION, FEE);
        assertEquals(contract.getTitle(), TITLE);
        assertEquals(contract.getDescription(), DESCRIPTION);
        assertEquals(contract.getFee(), Money.of(FEE, "EUR"));
    }

    @Test
    void addContentNotNull() {
        Contract contract = new Contract(TITLE, DESCRIPTION, FEE);
        assertNotNull(contract.getTitle());
        assertNotNull(contract.getDescription());
        assertNotNull(contract.getFee());
    }
}
