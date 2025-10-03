package org.padan.Model;

public class RegularUser extends User {
    private Integer loyaltyCounter;

    public RegularUser(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
        this.loyaltyCounter = 0;
    }

    public RegularUser(String firstName, String lastName, String email, Integer loyaltyCounter) {
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
