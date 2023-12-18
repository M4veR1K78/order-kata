package com.elis.kata.domain.deliveryday;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DeliveryDay {

    @Id
    private Long id;

    private LocalDate deliveryDate;
}
