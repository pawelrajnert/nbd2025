package org.padan.Model;

public class StudentUser extends User {
    public StudentUser(String firstName, String lastName, String email) {
        super(firstName, lastName, email);
    }

    @Override
    public double getDiscount() {
        return 0.25;
    }

    @Override
    public String getTypeName() {
        return "StudentUser";
    }

}
