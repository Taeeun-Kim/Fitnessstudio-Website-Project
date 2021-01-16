package fitnessstudio.coupon;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class CouponManager {

    private CouponRepository couponRepository;

    public CouponManager(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    /**
     * Sucht im Repository nach einem Coupon mit gegebenem Coupon-Code
     * @param code Coupon-Code
     * @return Coupon
     */
    public Optional<Coupon> findCouponByCode(String code) {
        Optional<Coupon> result = Optional.empty();
        for (Coupon coupon : couponRepository.findAll()) {
            if (Objects.requireNonNull(coupon.getId()).getIdentifier()
                    .equals(Coupon.getUuidFromCode(code).toString())) {
                result = Optional.of(coupon);
            }
        }
        return result;
    }

    public CouponRepository getRepository() {
        return couponRepository;
    }

    /**
     * Erstellt einen Coupon und speichert ihn im Repository
     * @param amount Wert des Coupons
     * @return Coupon
     */
    public Coupon create(Money amount) {
        Coupon coupon = new Coupon(amount);
        return couponRepository.save(coupon);
    }

    /**
     * Löscht einen Coupon aus dem Repository, z.B. bei Benutzung
     * @param coupon Coupon
     */
    public void delete(Coupon coupon) {
        couponRepository.delete(coupon);
    }
}
