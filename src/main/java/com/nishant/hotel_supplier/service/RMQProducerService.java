package com.nishant.hotel_supplier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.hotel_supplier.entity.HotelBookings;
import org.springframework.stereotype.Service;

@Service
public interface RMQProducerService {

    void sendRMQConfirmationNotification(HotelBookings hotelBookingResponse) throws JsonProcessingException;

    void sendRMQCancellationNotification(HotelBookings hotelBookingResponse) throws JsonProcessingException;
}
