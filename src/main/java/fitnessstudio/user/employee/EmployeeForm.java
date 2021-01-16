package fitnessstudio.user.employee;


import fitnessstudio.user.UserForm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

public class EmployeeForm extends UserForm.Register {

    @NotNull
    @PositiveOrZero
    private Float salary;

    public EmployeeForm(String email, String firstname, String lastname, String password, String repeatPassword,
                        String street, String number, String code, String location, String salary) {
        super(email, firstname, lastname, password, repeatPassword, street, number, code, location);
        if (salary != null) {
            this.salary = Float.parseFloat(salary);
        } else {
            this.salary = null;
        }

    }

    public Float getSalary() {
        return salary;
    }

    public static class Edit extends UserForm.Profile {

        @NotNull
        @PositiveOrZero
        private Float salary;

        public Edit(String email, String firstname, String lastname, String street, String number, String code,
                    String location, Float salary) {
            super(email, firstname, lastname, null, null, null, street, number, code, location);
            this.salary = salary;
        }

        public Float getSalary() {
            return salary;
        }
    }
}
