package fitnessstudio.shop.inventory;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.MultiInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Optional;

@Entity
public class FitnessstudioInventoryItem extends MultiInventoryItem {

    private LocalDate expiryDate;

    protected FitnessstudioInventoryItem() {
        super();
    }

    /**
     * Erstellt ein ShopInventoryItem mit oder ohne Verfallsdatum
     *
     * @param product Produkt
     * @param quantity Menge
     * @param expiryDate Verfallsdatum des Produkts. NULL, wenn es keins hat
     */
    public FitnessstudioInventoryItem(Product product, Quantity quantity, @Nullable LocalDate expiryDate) {
        super(product, quantity);

        this.expiryDate = expiryDate;
    }

    /**
     * Erstellt ein ShopInventoryItem ohne Verfallsdatum
     *
     * @param product Produkt
     * @param quantity Menge
     */
    public FitnessstudioInventoryItem(Product product, Quantity quantity) {
        this(product, quantity, null);
    }

    @Override
    @NonNull
    public org.salespointframework.catalog.Product getProduct() {
        return super.getProduct();
    }

    public Optional<LocalDate> getExpiryDate() {
        return Optional.ofNullable(expiryDate);
    }

    public boolean isExpired(BusinessTime time) {
        if (this.getExpiryDate().isEmpty()) {
            return false;
        }

        return this.getExpiryDate().get().isBefore(time.getTime().toLocalDate());
    }
}
