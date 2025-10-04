package org.padan.Model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
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
