package org.padan.Model.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;


@EqualsAndHashCode(callSuper = true)
@Data
@BsonDiscriminator(key = "clazz", value = "student")
@NoArgsConstructor
public class StudentUser extends User {
//    @BsonCreator
    public StudentUser(String firstName,
                        String lastName,
                       String email) {
        super(firstName, lastName, email);
    }

    @Override
    public double getDiscount() {
        return 0.25;
    }


}
