package fitnessstudio.statistics.record;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.salespointframework.core.Currencies;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RestockActivityTests {

    private RestockActivity activity;

    private LocalDateTime timestamp;
    private String product;
    private Integer amount;
    private Money price;

    @BeforeEach
    public void init() {
        this.timestamp = LocalDateTime.of(2020, 1, 1, 12, 0);
        this.product = "Energy Drink";
        this.amount = 1;
        this.price = Money.of(2.99, Currencies.EURO);

        this.activity = new RestockActivity(timestamp, product, amount, price);
    }

    @Test
    void testGetTimestamp() {
        assertEquals(this.timestamp, this.activity.getTimestamp());
    }

    @Test
    void testGetProduct() {
        assertEquals(this.product, this.activity.getProduct());
    }

    @Test
    void testGetAmount() {
        assertEquals(this.amount, this.activity.getAmount());
    }

    @Test
    void testGetPrice() {
        assertEquals(this.price, this.activity.getPrice());
    }

    @Test
    void testSetTimestamp() {
        LocalDateTime timestamp = LocalDateTime.of(2012, 12, 12, 12, 12);
        this.activity.setTimestamp(timestamp);
        assertEquals(timestamp, this.activity.getTimestamp());
    }

    @Test
    void testSetProduct() {
        String product = "Kekse";
        this.activity.setProduct(product);
        assertEquals(product, this.activity.getProduct());
    }

    @Test
    void testSetAmount() {
        Integer amount = 9;
        this.activity.setAmount(amount);
        assertEquals(amount, this.activity.getAmount());
    }

    @Test
    void testSetPrice() {
        Money price = Money.of(0.99, Currencies.EURO);
        this.activity.setPrice(price);
        assertEquals(price, this.activity.getPrice());
    }
}