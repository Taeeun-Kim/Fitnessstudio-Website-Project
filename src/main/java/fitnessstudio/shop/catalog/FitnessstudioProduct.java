package fitnessstudio.shop.catalog;

import org.hibernate.validator.constraints.Length;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.Currencies;
import org.springframework.lang.NonNull;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;

@Entity
public class FitnessstudioProduct extends Product {

    @Length(max = 16384)
    private String description;

    private Boolean canExpire;

    private String image;

    private Money discount;

    protected FitnessstudioProduct() {}

    public FitnessstudioProduct(String name, Money price, String description, boolean canExpire, String image) {
        super(name, price);
        this.description = description;
        this.canExpire = canExpire;
        this.image = image;
        this.discount = Money.from(Currencies.ZERO_EURO);
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    @Override
    @NonNull
    public MonetaryAmount getPrice() {
        return super.getPrice().subtract(this.discount);
    }

    public boolean canExpire() {
        return canExpire;
    }

    public Money getDiscount() {
        return discount;
    }

    public void setDiscount(Money discount) {
        this.discount = discount;
    }

    public boolean hasDiscount() {
        return !discount.isZero();
    }

    /**
     * Gibt den vollen Preis ohne Rabatt zurück
     */
    @NonNull
    public MonetaryAmount getUvp() {
        return super.getPrice();
    }

    /**
     * Berechnet den prozentualen Rabatt. Ist dieser so gering, dass er gerundet 0% ergibt, so wird auf 1% aufgerundet.
     */
    public int getDiscountPercentage() {
        int percentage = (int) (100 * getDiscount().getNumber().floatValue() / getUvp().getNumber().floatValue());
        return percentage == 0 ? 1 : percentage;
    }
}
