package fitnessstudio.contracts;

import org.hibernate.validator.constraints.Length;
import org.javamoney.moneta.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Contract {

    private @Id
    @GeneratedValue
    long id;

    private String title;

    @Length(max = 16384)
    private String description;

    private Money fee;

    @SuppressWarnings("unused")
    protected Contract() {}

    public Contract(String title, String description, Float fee) {
        this.title = title;
        this.description = description;
        this.fee = Money.of(fee, "EUR");
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Money getFee() {
        return fee;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFee(Float fee) {
        this.fee = Money.of(fee, "EUR");
    }

    public long getId() {
        return id;
    }
}
