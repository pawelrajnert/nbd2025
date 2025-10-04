package org.padan.Model.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerUserDTO extends UserDTO {
    private Boolean isPartner;

    public TrainerUserDTO(String firstName, String lastName, String email, Boolean isPartner) {
        super(firstName, lastName, email);
        this.isPartner = isPartner;
    }

    @Override
    public double getDiscount() {
        return isPartner ? 0.1 : 0.5;
    }

    @Override
    public String getTypeName() {
        return "TrainerUser";
    }

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 1) +
                ", isPartner=" + isPartner +
                '}';
    }
}
