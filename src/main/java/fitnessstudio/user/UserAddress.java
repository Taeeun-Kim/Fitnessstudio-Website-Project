package fitnessstudio.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserAddress {

    @Id
    @GeneratedValue
    private long id;

    private String street;
    private String number;
    private String code;
    private String location;

    public UserAddress(String street, String number, String code, String location) {
        this.street = street;
        this.number = number;
        this.code = code;
        this.location = location;
    }

    @SuppressWarnings("unused")
    protected UserAddress() {}

    @Override
    public String toString() {
        return this.street + " " + this.number + ", " + this.code + " " + this.location;
    }

    public long getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public static UserAddress fromRegisterForm(UserForm form) {
        return new UserAddress(form.getStreet(), form.getNumber(), form.getCode(), form.getLocation());
    }
}
