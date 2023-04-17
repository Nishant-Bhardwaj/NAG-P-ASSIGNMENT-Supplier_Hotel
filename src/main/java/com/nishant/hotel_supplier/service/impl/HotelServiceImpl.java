package com.nishant.hotel_supplier.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nishant.hotel_supplier.dto.HotelBookingDTO;
import com.nishant.hotel_supplier.dto.HotelSearchDTO;
import com.nishant.hotel_supplier.dto.HotelShortlistedDTO;
import com.nishant.hotel_supplier.entity.Hotel;
import com.nishant.hotel_supplier.entity.HotelBookings;
import com.nishant.hotel_supplier.entity.HotelShortlisted;
import com.nishant.hotel_supplier.repository.HotelBookingRepository;
import com.nishant.hotel_supplier.repository.HotelRepository;
import com.nishant.hotel_supplier.repository.HotelShortlistRepository;
import com.nishant.hotel_supplier.service.HotelService;
import com.nishant.hotel_supplier.service.RMQProducerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelServiceImpl implements HotelService {

    Logger logger = LogManager.getLogger(HotelServiceImpl.class);

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HotelShortlistRepository hotelShortlistRepository;

    @Autowired
    HotelBookingRepository hotelBookingRepository;

    @Autowired
    RMQProducerService rmqProducerService;

    @Override
    public List<Hotel> findAllHotels() {
        logger.info("Find all Hotels: Service Start");

        List<Hotel> hotels = hotelRepository.findAllHotels();

        logger.info("Total Hotels: "+ hotels.size());

        return hotels;
    }

    @Override
    public Mono<String> searchHotel(HotelSearchDTO hotel) throws JsonProcessingException {

        logger.info("Search Hotels: Service Start");

        String hotelName = hotel.getName();
        String hotelCity = hotel.getCity();
        LocalDate checkInDate = hotel.getCheckInDate();
        LocalDate checkOutDate = hotel.getCheckOutDate();

        List<Hotel> hotels = hotelRepository.findByFilters(hotelName, hotelCity, checkInDate, checkOutDate);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String response = mapper.writeValueAsString(hotels);

        return Mono.just(response);
    }

    @Override
    public Mono<String> shortlistHotel(HotelShortlistedDTO hotelShortlistedDTO) throws Exception {

        logger.info("Shortlist Hotel:: START");

        // User is already validated at consumer service

        // 1. Find and Validate Hotel:
        Optional<Hotel> hotel = hotelRepository.findById(hotelShortlistedDTO.getHotelId());

        if(!hotel.isPresent()){
            throw new Exception("Hotel not found !!");
        }
        else{
            // 2. Prepare Entity for hotel shortlisting:

            HotelShortlisted hotelShortlisted = HotelShortlisted.builder()
                    .hotel(hotel.get())
                    .userId(hotelShortlistedDTO.getUserId())
                    .build();

            // 3. Save shortlisting information:
            hotelShortlistRepository.save(hotelShortlisted);

        }
        return Mono.just("Hotel : "+hotel.get().getName());
    }

    @Override
    public String getShortlistHotel(Long userId) {
        List<HotelShortlisted> shortlistedDTOList
                = hotelShortlistRepository.findByUserId(userId);

        List<String> hotelNames= shortlistedDTOList.stream().map(x->x.getHotel().getName() + "->" + x.getHotel().getCity()).distinct().collect(Collectors.toList());

        hotelNames = hotelNames.stream().distinct().collect(Collectors.toList());

        StringBuilder response = new StringBuilder();

        for(String hotelName : hotelNames){
            response.append(hotelName+",");
        }

        return response.substring(0,response.toString().length()-1);
    }

    @Override
    public void bookingPaymentAndConfirm(HotelBookings hotelBookings) throws Exception {

        logger.info("Hotel Booking Payment Request Received!");

        // 1. Find and validate Hotel:
        Optional<Hotel> hotel = hotelRepository.findById(hotelBookings.getHotel().getHotelId());

        if(!hotel.isPresent()){
            sendBookingFailedNotification(hotelBookings, "Invalid Request!");
            throw new Exception("Invalid Request!");
        }

        else if(hotel.get().getRoomsAvailable()<1){
            sendBookingFailedNotification(hotelBookings, "Sorry, All Rooms Booked");
            throw new Exception("Sorry, All Rooms booked");
        }
        else{
            logger.info("Payment is Successful!");

            // reduce seat count:
            hotel.get().setRoomsAvailable(hotel.get().getRoomsAvailable()-1);

            // update hotel:
            hotelRepository.save(hotel.get());

            // send notification:
            sendBookingSuccessNotification(hotelBookings, "");
        }
    }

    @Override
    public HotelBookings bookHotel(HotelBookingDTO hotelBookingDTO) throws Exception {

        logger.info("Book Hotel: " + hotelBookingDTO);

        // Logic to book hotel:

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 1. Find and validate Flight:
        Optional<Hotel> hotel = hotelRepository.findById(hotelBookingDTO.getHotelId());

        if(!hotel.isPresent()){
            throw new Exception("Hotel Not Found!");
        }

        // 2. verify rooms are available:
        if(hotel.get().getRoomsAvailable()<1){
            throw new Exception("All Rooms Booked!");
        }
        else {

            HotelBookings hotelBookings = HotelBookings.builder()
                    .hotel(hotel.get())
                    .userId(hotelBookingDTO.getUserId())
                    .status("Payment Pending")
                    .roomNumber("After Payment")
                    .build();

            // 3. persist flight booking:
            hotelBookings = hotelBookingRepository.save(hotelBookings);

            return hotelBookings;
        }
    }

    private void sendBookingFailedNotification(HotelBookings hotelBookings, String message) throws Exception {

        logger.info("Send Booking Failed Notification");

        // 1. Find and validate Flight:
        Optional<Hotel> flight = hotelRepository.findById(hotelBookings.getHotel().getHotelId());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Update status:
        hotelBookings.setRoomNumber("Not Allocated");
        hotelBookings.setStatus("Failed, REFUND Initiated "+ message);

        // Notification for Booking Cancellation:
        rmqProducerService.sendRMQCancellationNotification(hotelBookings);
    }


    private void sendBookingSuccessNotification(HotelBookings hotelBookings, String message) throws Exception {

        logger.info("Send Booking Success Notification");

        // 1. Find and validate Flight:
        Optional<Hotel> hotel = hotelRepository.findById(hotelBookings.getHotel().getHotelId());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Fetch hotel Booking:
        Optional<HotelBookings> hotelBookingsFinal = hotelBookingRepository.findById(hotelBookings.getId());

        if(!hotelBookingsFinal.isPresent()){
            throw new Exception("Invalid Request!");
        }

        HotelBookings hotelBookingsResponse = hotelBookingsFinal.get();

        // Update status:
        hotelBookingsResponse.setRoomNumber(String.valueOf(hotel.get().getRoomsAvailable()));
        hotelBookingsResponse.setStatus("Confirmed/Booked");

        // 2. persist hotel booking:
        hotelBookingsResponse = hotelBookingRepository.save(hotelBookingsResponse);

        // Notification for Booking Confirmation:
        rmqProducerService.sendRMQConfirmationNotification(hotelBookingsResponse);
    }
}
