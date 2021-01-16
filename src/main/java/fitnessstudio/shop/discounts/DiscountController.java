package fitnessstudio.shop.discounts;

import fitnessstudio.shop.catalog.CatalogManager;
import fitnessstudio.shop.catalog.FitnessstudioProduct;
import org.javamoney.moneta.Money;
import org.salespointframework.core.Currencies;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Controller
@RequestMapping("/admin/discounts")
public class DiscountController {

    private final CatalogManager catalogManager;

    public DiscountController(CatalogManager catalogManager) {
        this.catalogManager = catalogManager;
    }

    @GetMapping("")
    public String discounts(Model model) {
        model.addAttribute("products", catalogManager.findAll());
        return "pages/admin/discounts";
    }

    @PostMapping("/{product}")
    public String updateDiscount(@PathVariable @NotNull FitnessstudioProduct product, @RequestParam Float discount,
                                 @RequestHeader String referer) {
        product.setDiscount(Money.of(discount, Currencies.EURO));
        catalogManager.getCatalog().save(product);
        return "redirect:"+ referer;
    }
}
