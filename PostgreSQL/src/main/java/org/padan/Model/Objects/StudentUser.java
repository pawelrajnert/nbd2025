package org.padan.Model.Objects;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue(value = "student_user")
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
