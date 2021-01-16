package fitnessstudio.shop.inventory;

import fitnessstudio.shop.catalog.CatalogManager;
import fitnessstudio.shop.catalog.FitnessstudioProduct;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Stream;

@Component
@Order(30)
public class InventoryDataInitializer implements DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryDataInitializer.class);

    private final FitnessstudioInventory inventory;
    private final CatalogManager catalog;
    private final BusinessTime businessTime;

    InventoryDataInitializer(FitnessstudioInventory inventory, CatalogManager catalog,
                             BusinessTime businessTime) {
        this.inventory = inventory;
        this.catalog = catalog;
        this.businessTime = businessTime;
    }

    @Override
    public void initialize() {

        if (inventory.findAll().iterator().hasNext()) {
            return;
        }

        LOG.info("Creating default inventory items.");

        LocalDate today = businessTime.getTime().toLocalDate();
        final Quantity defaultQuantity = Quantity.of(100);

        for (FitnessstudioProduct product : catalog.findAll()) {
            if (product.canExpire()) {
                Stream.of(today.plusDays(7), today.plusDays(14))
                        .map(it -> new FitnessstudioInventoryItem(product, defaultQuantity, it))
                        .forEach(inventory::save);
                Stream.of(today.minusDays(3)) // Produkte, die drei Tage abgelaufen sind
                        .map(it -> new FitnessstudioInventoryItem(product, Quantity.of(5), it))
                        .forEach(inventory::save);
            } else {
                inventory.save(new FitnessstudioInventoryItem(product, defaultQuantity));
            }
        }
    }
}