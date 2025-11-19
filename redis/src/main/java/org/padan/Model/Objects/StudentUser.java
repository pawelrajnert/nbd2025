package org.padan.Model.Objects;

import lombok.*;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;


@EqualsAndHashCode(callSuper = true)
@Data
@BsonDiscriminator(key = "clazz", value = "student")
@NoArgsConstructor
public class StudentUser extends User {

    public StudentUser(String firstName,
                       String lastName,
                       String email) {
        super(firstName, lastName, email);
    }


    @Override
    public String toString() {
        return "StudentUser{} " + super.toString();
    }
}
