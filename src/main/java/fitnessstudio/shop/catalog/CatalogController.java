package fitnessstudio.shop.catalog;

import fitnessstudio.shop.inventory.FitnessstudioInventory;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/shop")
public class CatalogController {
    private final CatalogManager catalog;
    private final BusinessTime businessTime;
    private final FitnessstudioInventory inventory;

    CatalogController(CatalogManager catalog, FitnessstudioInventory inventory, BusinessTime businessTime) {
        this.catalog = catalog;
        this.inventory = inventory;
        this.businessTime = businessTime;

    }

    @GetMapping("")
    public String catalog(Model model) {
        model.addAttribute("catalog", catalog.findAll());

        return "pages/shop/index";
    }

    @GetMapping("/product/{product}")
    public String detail(@PathVariable FitnessstudioProduct product, Model model, HttpSession session) {
        Cart cart = ((Cart) session.getAttribute("cart"));
        Quantity cartQuantity = Quantity.NONE;
        Quantity stockQuantity = inventory.findUnexpiredItemsSorted(
                product.getId(),
                businessTime.getTime().toLocalDate()
        ).getTotalQuantity();

        if (cart != null) {
            for (CartItem item : cart.toSet()) {
                if (item.getProduct().getId().equals(product.getId())) {
                    cartQuantity = cartQuantity.add(item.getQuantity());
                }
            }
        }

        model.addAttribute("product", product);
        model.addAttribute("stock_quantity", stockQuantity);
        model.addAttribute("cart_quantity", cartQuantity);
        return "pages/shop/product";
    }
}
