package fitnessstudio.shop.catalog;

import org.salespointframework.catalog.Catalog;
import org.salespointframework.catalog.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogManager {

    private final Catalog<FitnessstudioProduct> catalog;

    public CatalogManager(Catalog<FitnessstudioProduct> catalog) {
        this.catalog = catalog;
    }

    public Iterable<FitnessstudioProduct> findAll() {
        List<FitnessstudioProduct> products = new ArrayList<>();
        for (Product product : this.catalog.findAll()) {
            if (product instanceof FitnessstudioProduct) {
                products.add((FitnessstudioProduct) product);
            }
        }
        return products;
    }

    public Catalog<FitnessstudioProduct> getCatalog() {
        return catalog;
    }
}
