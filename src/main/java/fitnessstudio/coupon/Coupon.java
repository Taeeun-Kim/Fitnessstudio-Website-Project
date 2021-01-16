package fitnessstudio.coupon;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Coupon extends Product {

    public Coupon() {}

    public Coupon(Money amount) {
        super("Coupon", amount);
    }

    /**
     * Kodiert den ProductIdentifier (UUID) in eine kürzere 32 bit Zeichenkette,
     * welche von einem Mitglied eingelöst werden kann um Guthaben aufzuladen.
     *
     * @return Kodierte UUID
     */
    public String getCode() {
        String id = Objects.requireNonNull(this.getId()).getIdentifier().replaceAll("-", "");
        BigInteger code = new BigInteger(id, 16);
        return code.toString(32);
    }

    /**
     * Dekodiert eine 32 bit Zeichenkette zurück in eine UUID
     *
     * @return Dekodierte UUID
     */
    public static UUID getUuidFromCode(String code) {
        String s = String.format("%032x", new BigInteger(code, 32));
        s = String.format("%s-%s-%s-%s-%s", s.substring(0, 8), s.substring(8, 12), s.substring(12, 16),
                s.substring(16, 20), s.substring(20));
        return UUID.fromString(s);
    }

    public static class Form {
        @NotNull
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}
