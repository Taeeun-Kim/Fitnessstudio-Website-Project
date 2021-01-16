package fitnessstudio.contracts;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ContractForm {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    @Positive
    private Float fee;

    public ContractForm(String title, String description, Float fee) {
        this.title = title;
        this.description = description;
        this.fee = fee;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Float getFee() {
        return fee;
    }
}
