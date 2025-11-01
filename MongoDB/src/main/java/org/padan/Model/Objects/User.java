package org.padan.Model.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.UUID;

@Data
@NoArgsConstructor
@BsonDiscriminator(key = "clazz", value = "user")
public abstract class User {
    @BsonId
    private ObjectId userId;
    @BsonProperty("first_name")
    private String firstName;
    @BsonProperty("last_name")
    private String lastName;
    @BsonProperty("email")
    private String email;

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public abstract double getDiscount();

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
