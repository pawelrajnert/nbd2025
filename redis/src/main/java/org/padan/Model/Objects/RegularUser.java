package org.padan.Model.Objects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.codecs.pojo.annotations.BsonProperty;

@EqualsAndHashCode(callSuper = true)
@Data
@BsonDiscriminator(key = "clazz", value = "regular")
@NoArgsConstructor
public class RegularUser extends User {

    private static final Double LOYALTY_COEFFICIENT = 0.1;
    private static final Integer LOYALTY_COUNTER_CAP = 20;
    private static final Double LOYALTY_COEFFICIENT_CAP = 0.2;

    @BsonProperty("loyalty_counter")
    private Integer loyaltyCounter;

    public RegularUser(String firstName,
                       String lastName,
                       String email) {
        super(firstName, lastName, email);
        this.loyaltyCounter = 0;
    }

    public void incrementLoyalty() {
        loyaltyCounter++;
    }

    public void decrementLoyalty() {
        loyaltyCounter--;
    }

    @Override
    public String toString() {
        return "RegularUser{" +
                "loyaltyCounter=" + loyaltyCounter +
                "} " + super.toString();
    }
}
