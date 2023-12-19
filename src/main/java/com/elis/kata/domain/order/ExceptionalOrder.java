package com.elis.kata.domain.order;

import com.elis.kata.domain.deliveryday.DeliveryDay;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("EXCEPTIONAL")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ExceptionalOrder extends Order {

    public ExceptionalOrder(DeliveryDay deliveryDay, int nbItems) {
        super(deliveryDay, nbItems);
    }
}
