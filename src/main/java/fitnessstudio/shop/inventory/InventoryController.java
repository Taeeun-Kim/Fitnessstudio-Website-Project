package fitnessstudio.shop.inventory;

import fitnessstudio.shop.catalog.CatalogManager;
import fitnessstudio.shop.catalog.FitnessstudioProduct;
import fitnessstudio.statistics.record.RestockActivity;
import fitnessstudio.statistics.repository.RestockActivityRepository;
import org.javamoney.moneta.Money;
import org.salespointframework.inventory.InventoryItemIdentifier;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping("/shop/stock")
public class InventoryController {
    private final FitnessstudioInventory inventory;;
    private final CatalogManager catalog;
    private final BusinessTime businessTime;
    private final RestockActivityRepository restockActivities;

    InventoryController(FitnessstudioInventory inventory, CatalogManager catalog, BusinessTime businessTime,
                        RestockActivityRepository restockActivities) {
        this.inventory = inventory;
        this.catalog = catalog;
        this.businessTime = businessTime;
        this.restockActivities = restockActivities;
    }

    @GetMapping("")
    public String stock(Model model, @RequestParam(required = false, defaultValue = "") String filter) {
        Map<FitnessstudioProduct, InventoryItems<FitnessstudioInventoryItem>> map = new HashMap<>();

        for (FitnessstudioProduct product : catalog.findAll()) {
            map.put(product, inventory.findByProductIdentifierSorted(Objects.requireNonNull(product.getId())));
        }

        if (filter.equalsIgnoreCase("sold")) {
            // Entferne alle Produkte, die >= 0 auf Lager sind.
            for (FitnessstudioProduct product : catalog.findAll()) {
                if (!map.get(product).getTotalQuantity().isZeroOrNegative()) {
                    map.remove(product);
                }
            }
        } else if (filter.equalsIgnoreCase("expired")) {
            // Entferne alle Produkte, die keine verfallenen Pakete haben.
            for (FitnessstudioProduct product : catalog.findAll()) {
                AtomicBoolean clear = new AtomicBoolean(true);
                map.get(product).get().forEach(item -> {
                    if (item.isExpired(businessTime)) {
                        clear.set(false);
                    }
                });
                if (clear.get()) {
                    map.remove(product);
                }
            }
        }

        model.addAttribute("map", map);
        model.addAttribute("time", businessTime);

        return "pages/shop/stock";
    }

    @PostMapping("/{product}")
    public String restockProduct(@PathVariable @NonNull FitnessstudioProduct product,
                                 @RequestParam @Positive Integer amount) {
        if (product.canExpire()) {
            LocalDate expiryDate = businessTime.getTime().toLocalDate().plusDays(30);
            inventory.save(new FitnessstudioInventoryItem(product, Quantity.of(amount), expiryDate));
        } else {
            FitnessstudioInventoryItem item = inventory.findByProductIdentifier(product.getId()).get().findFirst()
                    .orElse(new FitnessstudioInventoryItem(product, Quantity.NONE));
            item.increaseQuantity(Quantity.of(amount));
            inventory.save(item);
        }

        Money price = Money.from(product.getUvp().multiply(amount));
        restockActivities.save(new RestockActivity(businessTime.getTime(), product.getName(), amount, price));

        return "redirect:/shop/stock";
    }

    @PostMapping("/delete/{id}")
    public String removeItem(@PathVariable @NonNull InventoryItemIdentifier id) {
        inventory.deleteById(id);
        return "redirect:/shop/stock";
    }
}