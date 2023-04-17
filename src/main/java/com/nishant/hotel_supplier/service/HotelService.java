package com.nishant.hotel_supplier.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.hotel_supplier.dto.HotelBookingDTO;
import com.nishant.hotel_supplier.dto.HotelSearchDTO;
import com.nishant.hotel_supplier.dto.HotelShortlistedDTO;
import com.nishant.hotel_supplier.entity.Hotel;
import com.nishant.hotel_supplier.entity.HotelBookings;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface HotelService {

    List<Hotel> findAllHotels();

    Mono<String> searchHotel(HotelSearchDTO hotel) throws JsonProcessingException;

    Mono<String> shortlistHotel(HotelShortlistedDTO hotelShortlistedDTO) throws Exception;

    HotelBookings bookHotel(HotelBookingDTO hotelBookingDTO) throws Exception;

    String getShortlistHotel(Long userId);

    void bookingPaymentAndConfirm(HotelBookings hotelBookings) throws Exception;
}
