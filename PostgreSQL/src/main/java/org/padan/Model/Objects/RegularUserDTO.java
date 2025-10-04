package org.padan.Model.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue(value = "regular_user")
public class RegularUserDTO extends UserDTO {
    @Column(name = "loyalty_counter")
    private Integer loyaltyCounter;

    public RegularUserDTO(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.loyaltyCounter = 0;
    }

    public RegularUserDTO(String firstName, String lastName, String email, Integer loyaltyCounter) {
        super(firstName, lastName, email);
        this.loyaltyCounter = loyaltyCounter;
    }

    @Override
    public double getDiscount() {
        double discount = loyaltyCounter * 0.01;
        return loyaltyCounter <= 20 ? discount : 0.2;
    }

    @Override
    public String getTypeName() {
        return this.getClass().getName();
    }

    public void incrementLoyalty() {
        loyaltyCounter++;
    }

    public void decrementLoyalty() {
        loyaltyCounter--;
    }

}
