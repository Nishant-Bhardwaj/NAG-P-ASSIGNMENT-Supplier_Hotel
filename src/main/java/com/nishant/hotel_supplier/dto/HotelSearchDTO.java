package com.nishant.hotel_supplier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchDTO {

    private String name;

    private String city;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;
}
