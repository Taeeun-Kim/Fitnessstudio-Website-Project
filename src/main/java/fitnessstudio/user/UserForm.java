package fitnessstudio.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class UserForm {

    @Email private final String email;
    @NotEmpty private final String firstname;
    @NotEmpty private final String lastname;
    private final String password;
    @NotEmpty private final String street;
    @NotEmpty private final String number;
    @NotEmpty private final String code;
    @NotEmpty private final String location;

    public UserForm(String email, String firstname, String lastname, String password, String street, String number,
                    String code, String location) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.street = street;
        this.number = number;
        this.code = code;
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getCode() {
        return code;
    }

    public String getLocation() {
        return location;
    }

    public static class Register extends UserForm {

        @NotEmpty
        private final String repeatPassword;

        public Register(String email, String firstname, String lastname, String password,
                       String repeatPassword, String street, String number, String code, String location) {
            super(email, firstname, lastname, password, street, number, code, location);
            this.repeatPassword = repeatPassword;
        }

        public String getRepeatPassword() {
            return repeatPassword;
        }
    }

    public static class Profile extends UserForm {

        private final String oldPw;
        private String repeatPassword;

        public Profile(String email, String firstname, String lastname, String oldPw, String password,
                       String repeatPassword, String street, String number, String code, String location) {
            super(email, firstname, lastname, password, street, number, code, location);
            this.oldPw = oldPw;
            this.repeatPassword = repeatPassword;
        }

        public String getOldPw() {
            return oldPw;
        }

        public String getRepeatPassword() {
            return repeatPassword;
        }
    }
}
