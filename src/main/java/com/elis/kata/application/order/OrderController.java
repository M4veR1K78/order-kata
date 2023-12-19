package com.elis.kata.application.order;

import com.elis.kata.application.common.ConnectedUser;
import com.elis.kata.domain.deliveryday.DeliveryDayService;
import com.elis.kata.domain.order.ExceptionalOrder;
import com.elis.kata.domain.order.Order;
import com.elis.kata.domain.order.Order.OrderStatus;
import com.elis.kata.domain.order.Order.OrderType;
import com.elis.kata.domain.order.OrderService;
import com.elis.kata.domain.order.RegularOrder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
class OrderController {

    private final OrderService orderService;

    private final DeliveryDayService deliveryDayService;

    @PostMapping("/delivery-day/{deliveryDayId}/order")
    @PreAuthorize("hasAuthority('EXTERNAL_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    OrderDto createOrder(@PathVariable Long deliveryDayId, @RequestBody OrderCreateDto orderCreateDto, @AuthenticationPrincipal ConnectedUser connectedUser) {
        Order order = orderCreateDto.type() == OrderType.REGULAR
            ? orderService.createRegular(deliveryDayService.get(deliveryDayId), orderCreateDto.nbItems())
            : orderService.createExceptional(deliveryDayService.get(deliveryDayId), orderCreateDto.nbItems());
        return OrderDto.asDto(order);
    }

    record OrderCreateDto(int nbItems, OrderType type) {

    }

    record OrderDto(Long id, OrderStatus status, int nbItems, long deliveryDayId, OrderType type) {

        public static OrderDto asDto(Order order) {
            return new OrderDto(order.getId(), order.getStatus(), order.getNbItems(), order.getDeliveryDay().getId(), asType(order));
        }

        private static OrderType asType(Order order) {
            return switch (order) {
                case RegularOrder ignored -> OrderType.REGULAR;
                case ExceptionalOrder ignored -> OrderType.EXCEPTIONAL;
                default -> throw new IllegalStateException("Unknown type of order");
            };
        }
    }
}
