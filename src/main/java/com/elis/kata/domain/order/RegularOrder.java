package com.elis.kata.domain.order;

import com.elis.kata.domain.deliveryday.DeliveryDay;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("REGULAR")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class RegularOrder extends Order {

    public RegularOrder(DeliveryDay deliveryDay, int nbItems) {
        super(deliveryDay, nbItems);
    }
}
