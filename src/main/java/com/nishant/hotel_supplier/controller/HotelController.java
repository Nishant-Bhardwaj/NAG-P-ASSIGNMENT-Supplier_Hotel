package com.nishant.hotel_supplier.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nishant.hotel_supplier.dto.HotelBookingDTO;
import com.nishant.hotel_supplier.dto.HotelSearchDTO;
import com.nishant.hotel_supplier.dto.HotelShortlistedDTO;
import com.nishant.hotel_supplier.entity.HotelBookings;
import com.nishant.hotel_supplier.service.HotelService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/supplier/hotel")
public class HotelController {

    Logger logger = LogManager.getLogger(HotelController.class);

    @Autowired
    HotelService hotelService;

    // Search All Hotels
    @GetMapping(path = "/all",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllHotels(){
        logger.info("Request Received, Search All Hotels");
        return new ResponseEntity<>(hotelService.findAllHotels(), HttpStatus.OK);
    }

    // Search Hotels with parameters
    @PostMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> getSearchedHotels(@RequestBody HotelSearchDTO hotel) throws JsonProcessingException {
        logger.info("Request Received, Search Hotel : "+ hotel);
        return hotelService.searchHotel(hotel);
    }

    // Shortlist Hotel, takes hotelId and userId
    @PostMapping(path = "/shortlist")
    public Mono<String> shortlistHotels(@RequestBody HotelShortlistedDTO hotelShortlistedDTO) throws Exception {
        logger.info("Request Received, Shortlist Hotels : "+ hotelShortlistedDTO);
        return hotelService.shortlistHotel(hotelShortlistedDTO);
    }

    // Get Shortlist Hotel, takes userId
    @PostMapping(path = "/shortlist/get")
    public String getShortlistHotels(@RequestParam("userId") Long userId) throws Exception {
        logger.info("Get Shortlist Hotels for user: "+ userId);
        return hotelService.getShortlistHotel(userId);
    }

    // Hotels booking
    @PostMapping(path = "/book")
    public ResponseEntity<HotelBookings> bookHotels(@RequestBody HotelBookingDTO hotelBookingDTO) throws Exception {
        logger.info("Request Received, Book Hotels : "+ hotelBookingDTO);
        return new ResponseEntity<HotelBookings>(hotelService.bookHotel(hotelBookingDTO), HttpStatus.OK);
    }


    // Flight booking payment endpoints
    @PostMapping(path = "/payment")
    public ResponseEntity<String> bookingPayment(@RequestBody HotelBookings hotelBookings) throws Exception {
        logger.info("Request for Hotel Payment : " + hotelBookings);

        CompletableFuture.runAsync(() -> {
            try {
                hotelService.bookingPaymentAndConfirm(hotelBookings);
            } catch (Exception e) {
                logger.info("Sent failure Notification for: " + e.getMessage());
            }
        });

        return new ResponseEntity<>(
                "Notification/Update is sent to Consumer App via RMQ.",
                HttpStatus.OK
        );
    }
}
