package fitnessstudio.coupon;

import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<Coupon, ProductIdentifier> {}
