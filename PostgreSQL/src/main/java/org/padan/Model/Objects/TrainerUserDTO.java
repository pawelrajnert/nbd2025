package org.padan.Model.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue(value = "trainer_user")
public class TrainerUserDTO extends UserDTO {
    @NotNull
    @Column(name = "is_partner")
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
