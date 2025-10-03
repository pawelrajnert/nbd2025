package org.padan.Model;

public class TrainerUser extends User {
    private Boolean isPartner;

    public TrainerUser(String firstName, String lastName, String email, Boolean isPartner) {
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
