package fitnessstudio.shop.catalog;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static org.salespointframework.core.Currencies.EURO;

@Component
@Order(20)
class CatalogDataInitializer implements DataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

    private final CatalogManager catalog;

    CatalogDataInitializer(CatalogManager catalog) {

        Assert.notNull(catalog, "VideoCatalog must not be null!");

        this.catalog = catalog;
    }

    @Override
    public void initialize() {

        if (catalog.findAll().iterator().hasNext()) {
            return;
        }

        LOG.info("Creating default catalog entries.");

        catalog.getCatalog().save(new FitnessstudioProduct("Quicc Snacc", Money.of(4.99,EURO),
                "Eine schnelle Speise für schnelle Leute", true, "quickschnack.png"));
        catalog.getCatalog().save(new FitnessstudioProduct("Quicc Drincc", Money.of(2.99, EURO),
                "Ein schnelles Getränk für schnelle Leute", true, "quickdrinck.jpg"));
        catalog.getCatalog().save(new FitnessstudioProduct("Handtuch", Money.of(9.99, EURO),
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                        "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                        "ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
                        "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur " +
                        "sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est " +
                        "laborum.", false, "handtuch.jpg"));
        catalog.getCatalog().save(new FitnessstudioProduct("Hantel", Money.of(29.99, EURO),
                "Schwer", false, "hantel.png"));
    }
}
