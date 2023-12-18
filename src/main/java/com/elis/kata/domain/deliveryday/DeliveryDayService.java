package com.elis.kata.domain.deliveryday;

import com.elis.kata.domain.common.BusinessException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryDayService {

    private final DeliveryDayRepository deliveryDayRepository;

    public DeliveryDay get(Long id) {
        return deliveryDayRepository.findById(id)
            .orElseThrow(() -> new BusinessException("The delivery day does not exist"));
    }
}
