package fitnessstudio.shop.inventory;

import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.MultiInventory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface FitnessstudioInventory extends MultiInventory<FitnessstudioInventoryItem> {
    @Query("select i from FitnessstudioInventoryItem i where i.expiryDate < ?1")
    Streamable<FitnessstudioInventoryItem> findExpiredItems(LocalDate today);

    @Query("select i from FitnessstudioInventoryItem i where " +
            "i.product.productIdentifier = ?1 and (i.expiryDate is null or i.expiryDate >= ?2) order by i.expiryDate")
    InventoryItems<FitnessstudioInventoryItem> findUnexpiredItemsSorted(ProductIdentifier productIdentifier,
                                                                        LocalDate today);

    @Query("select i from FitnessstudioInventoryItem i where i.product.productIdentifier = ?1 order by i.expiryDate")
    InventoryItems<FitnessstudioInventoryItem> findByProductIdentifierSorted(ProductIdentifier productIdentifier);
}
