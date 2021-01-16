package fitnessstudio.statistics.record;

import org.javamoney.moneta.Money;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
public class RestockActivity {

    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime timestamp;

    @NotNull
    private String product;

    @NotNull @Positive
    private Integer amount;

    @NotNull
    private Money price;

    protected RestockActivity() {}

    public RestockActivity(LocalDateTime timestamp, String product, Integer amount, Money price) {
        this.timestamp = timestamp;
        this.product = product;
        this.amount = amount;
        this.price = price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getProduct() {
        return product;
    }

    public Integer getAmount() {
        return amount;
    }

    public Money getPrice() {
        return price;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setPrice(Money price) {
        this.price = price;
    }
}
