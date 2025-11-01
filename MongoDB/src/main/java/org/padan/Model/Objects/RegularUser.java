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

//    @BsonCreator
    public RegularUser( String firstName,
                        String lastName,
                        String email){
        super(firstName, lastName, email);
        this.loyaltyCounter = 0;
    }

//    public RegularUser(@BsonProperty("first_name") String firstName,
//                       @BsonProperty("last_name") String lastName,
//                       @BsonProperty("email") String email,
//                       @BsonProperty("loyalty_counter") Integer loyaltyCounter) {
//        super(firstName, lastName, email);
//        this.loyaltyCounter = loyaltyCounter;
//    }

    @Override
    public double getDiscount() {
        double discount = loyaltyCounter * LOYALTY_COEFFICIENT;
        return loyaltyCounter <= LOYALTY_COUNTER_CAP ? discount : LOYALTY_COEFFICIENT_CAP;
    }

    public void incrementLoyalty() {
        loyaltyCounter++;
    }

    public void decrementLoyalty() {
        loyaltyCounter--;
    }

}
