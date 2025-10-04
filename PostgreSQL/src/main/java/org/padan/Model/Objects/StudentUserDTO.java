package org.padan.Model.Objects;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue(value = "student_user")
public class StudentUserDTO extends UserDTO {
    public StudentUserDTO(String firstName, String lastName, String email) {
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
