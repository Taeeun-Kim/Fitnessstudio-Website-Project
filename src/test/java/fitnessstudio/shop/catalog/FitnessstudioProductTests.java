package fitnessstudio.shop.catalog;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.core.Currencies;

import static org.junit.jupiter.api.Assertions.*;

public class FitnessstudioProductTests {

    private final String name = "Example Product";
    private final Money uvp = Money.of(20, Currencies.EURO);
    private final String description = "This is a product description";
    private final boolean canExpire = false;
    private final String image = "/assets/images/example.png";

    private FitnessstudioProduct product;

    @BeforeEach
    public void init() {
        this.product = new FitnessstudioProduct();
        this.product = new FitnessstudioProduct(name, uvp, description, canExpire, image);
    }

    @Test
    public void testUvp() {
        assertEquals(this.uvp, this.product.getUvp());
    }

    @Test
    public void testPrice() {
        assertEquals(this.uvp, this.product.getPrice());
    }

    @Test
    public void testDescription() {
        assertEquals(this.description, this.product.getDescription());
    }

    @Test
    public void testImage() {
        assertEquals(this.image, this.product.getImage());
    }

    @Test
    public void testCanExpire() {
        assertEquals(this.canExpire, this.product.canExpire());
    }

    @Test
    public void testDiscount() {
        this.product.setDiscount(Money.of(10, Currencies.EURO));

        assertTrue(this.product.hasDiscount());
        assertEquals(50, this.product.getDiscountPercentage());
        assertEquals(Money.of(10, Currencies.EURO), this.product.getPrice());
        assertEquals(this.uvp, this.product.getUvp());
        assertEquals(Money.of(10, Currencies.EURO), this.product.getDiscount());

        this.product.setDiscount(Money.of(5, Currencies.EURO));

        assertTrue(this.product.hasDiscount());
        assertEquals(25, this.product.getDiscountPercentage());
        assertEquals(Money.of(15, Currencies.EURO), this.product.getPrice());
        assertEquals(this.uvp, this.product.getUvp());
        assertEquals(Money.of(5, Currencies.EURO), this.product.getDiscount());

        this.product.setDiscount(Money.of(2, Currencies.EURO));

        assertTrue(this.product.hasDiscount());
        assertEquals(10, this.product.getDiscountPercentage());
        assertEquals(Money.of(18, Currencies.EURO), this.product.getPrice());
        assertEquals(this.uvp, this.product.getUvp());
        assertEquals(Money.of(2, Currencies.EURO), this.product.getDiscount());

        this.product.setDiscount(Money.zero(Currencies.EURO));

        assertFalse(this.product.hasDiscount());
        assertEquals(this.uvp, this.product.getPrice());
        assertEquals(this.uvp, this.product.getUvp());
    }
}
