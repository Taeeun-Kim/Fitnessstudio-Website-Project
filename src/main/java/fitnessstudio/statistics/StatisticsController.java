package fitnessstudio.statistics;

import fitnessstudio.statistics.repository.CustomerActivityRepository;
import fitnessstudio.statistics.repository.RestockActivityRepository;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/statistics")
@PreAuthorize("hasRole('BOSS')")
public class StatisticsController {

    private final CustomerActivityRepository customerActivities;
    private final RestockActivityRepository restockActivities;
    private final OrderManager<Order> orderManager;

    public StatisticsController(CustomerActivityRepository customerActivities,
                                RestockActivityRepository restockActivities, OrderManager<Order> orderManager) {
        this.customerActivities = customerActivities;
        this.restockActivities = restockActivities;
        this.orderManager = orderManager;
    }

    @GetMapping("")
    public String statistics() {
        return "redirect:/statistics/customer";
    }

    @GetMapping("/customer")
    public String customer(Model model) {
        model.addAttribute("records", customerActivities.findAll());
        return "pages/statistics/customer";
    }

    @GetMapping("/sales")
    public String sales(Model model) {
        handleOrderStatistics(model);
        return "pages/statistics/sales";
    }

    @GetMapping("/revenue")
    public String revenue(Model model) {
        handleOrderStatistics(model);
        return "pages/statistics/revenue";
    }

    @GetMapping("/costs")
    public String costs(Model model) {
        model.addAttribute("records", restockActivities.findAllByOrderByTimestampDesc());
        return "pages/statistics/costs";
    }

    private void handleOrderStatistics(Model model) {
        Comparator<Order> comparator = Comparator.comparing(Order::getDateCreated).reversed();
        Stream<Order> orders = orderManager.findBy(OrderStatus.COMPLETED).stream().sorted(comparator);
        Streamable<Order> records = Streamable.of(orders.collect(Collectors.toList()));

        model.addAttribute("records", records);
    }
}
