package com.elis.kata.domain.order;

import com.elis.kata.domain.common.BusinessException;
import com.elis.kata.domain.deliveryday.DeliveryDay;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "`order`")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected int nbItems;

    @Enumerated(EnumType.STRING)
    protected OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    protected DeliveryDay deliveryDay;

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, updatable = false)
    protected OrderType type;

    public Order(DeliveryDay deliveryDay, int nbItems) {
        requireAtLeastOneItem(nbItems);
        this.deliveryDay = deliveryDay;
        this.nbItems = nbItems;
        this.status = OrderStatus.DRAFT;
    }

    public static Order createRegular(DeliveryDay deliveryDay, int nbItems) {
        return new RegularOrder(deliveryDay, nbItems);
    }

    public static Order createExceptional(DeliveryDay deliveryDay, int nbItems) {
        return new ExceptionalOrder(deliveryDay, nbItems);
    }

    private void requireAtLeastOneItem(int nbItems) {
        if (nbItems <= 0) {
            throw new BusinessException("An order must have at least one item");
        }
    }

    public enum OrderType {
        REGULAR,
        EXCEPTIONAL
    }

    public enum OrderStatus {
        DRAFT,
        VALIDATED
    }
}
