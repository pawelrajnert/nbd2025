package org.padan.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RegularUserDTO extends UserDTO {
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
