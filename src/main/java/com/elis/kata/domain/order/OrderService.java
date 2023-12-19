package com.elis.kata.domain.order;

import com.elis.kata.domain.common.BusinessException;
import com.elis.kata.domain.deliveryday.DeliveryDay;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createRegular(DeliveryDay deliveryDay, int nbItems) {
        requireNotExistingOrderFor(deliveryDay);
        requireNotExpired(deliveryDay);
        return orderRepository.save(Order.createRegular(deliveryDay, nbItems));
    }

    private void requireNotExpired(DeliveryDay deliveryDay) {
        if (deliveryDay.getDeliveryDate().isBefore(LocalDate.now())) {
            throw new BusinessException("An order can't be created on an expired delivery day");
        }
    }

    private void requireNotExistingOrderFor(DeliveryDay deliveryDay) {
        if (deliveryDay.getRegularOrder().isPresent()) {
            throw new BusinessException("An order already exists for this delivery day");
        }
    }

    public Order createExceptional(DeliveryDay deliveryDay, int nbItems) {
        requireNotExpired(deliveryDay);
        requireValidatedRegularOrder(deliveryDay);
        return orderRepository.save(Order.createExceptional(deliveryDay, nbItems));
    }

    private void requireValidatedRegularOrder(DeliveryDay deliveryDay) {
        if (!deliveryDay.hasValidatedRegularOrder()) {
            throw new BusinessException("Exceptional order can't be created if the delivery day doesn't have a validated regular order");
        }
    }

}
