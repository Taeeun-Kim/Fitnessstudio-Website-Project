package fitnessstudio.coupon;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouponTests {

    private static final int ACCURACY = 2000;

    @Test
    void testGetCode() {
        for (int i = 0; i < ACCURACY; i++) {
            Coupon coupon = new Coupon();
            assertEquals(Objects.requireNonNull(coupon.getId()).getIdentifier(),
                    Coupon.getUuidFromCode(coupon.getCode()).toString());
        }
    }
}