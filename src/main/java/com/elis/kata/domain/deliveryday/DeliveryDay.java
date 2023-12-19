package com.elis.kata.domain.deliveryday;

import com.elis.kata.domain.order.ExceptionalOrder;
import com.elis.kata.domain.order.Order.OrderStatus;
import com.elis.kata.domain.order.RegularOrder;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DeliveryDay {

    @Id
    private Long id;

    private LocalDate deliveryDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "deliveryDay")
    private RegularOrder regularOrder;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "deliveryDay")
    private ExceptionalOrder exceptionalOrder;

    public Optional<RegularOrder> getRegularOrder() {
        return Optional.ofNullable(regularOrder);
    }

    public Optional<ExceptionalOrder> getExceptionalOrder() {
        return Optional.ofNullable(exceptionalOrder);
    }

    public boolean hasValidatedRegularOrder() {
        return getRegularOrder()
            .filter(order -> order.getStatus() == OrderStatus.VALIDATED)
            .isPresent();
    }
}
