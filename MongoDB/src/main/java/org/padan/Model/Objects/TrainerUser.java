package org.padan.Model.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@EqualsAndHashCode(callSuper = true)
@Data
@BsonDiscriminator(key = "clazz", value = "trainer")
@NoArgsConstructor
public class TrainerUser extends User {
    @BsonProperty("is_partner")
    private Boolean isPartner;

    public TrainerUser(String firstName,
                       String lastName,
                       String email,
                       Boolean isPartner) {
        super(firstName, lastName, email);
        this.isPartner = isPartner;
    }

    @Override
    public double getDiscount() {
        return isPartner ? 0.1 : 0.5;
    }

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().length() - 1) +
                ", isPartner=" + isPartner +
                '}';
    }
}
