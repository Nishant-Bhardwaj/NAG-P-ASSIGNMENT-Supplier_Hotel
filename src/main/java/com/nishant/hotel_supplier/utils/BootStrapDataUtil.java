package com.nishant.hotel_supplier.utils;

import com.nishant.hotel_supplier.entity.Hotel;
import com.nishant.hotel_supplier.entity.HotelBookings;
import com.nishant.hotel_supplier.repository.HotelBookingRepository;
import com.nishant.hotel_supplier.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class BootStrapDataUtil {

    @Autowired
    HotelRepository hotelRepository;

    @Autowired
    HotelBookingRepository bookingRepository;

    @PostConstruct
    private void createHotelBootstrapData() {

        ArrayList<Hotel> hotelArrayList = new ArrayList<>();

        hotelArrayList.add(Hotel.builder()
                .hotelId(1L).city("Meerut").name("Taj").roomsAvailable(100)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(6500L)
                .build());

        hotelArrayList.add(Hotel.builder()
                .hotelId(2L).city("Meerut").name("Lemon").roomsAvailable(56)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(2500L)
                .build());

        hotelArrayList.add(Hotel.builder()
                .hotelId(3L).city("Noida").name("Taj").roomsAvailable(45)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(1500L)
                .build());

        hotelArrayList.add(Hotel.builder()
                .hotelId(4L).city("Noida").name("Lemon").roomsAvailable(58)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(3500L)
                .build());

        hotelArrayList.add(Hotel.builder()
                .hotelId(5L).city("Delhi").name("Lemon").roomsAvailable(62)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(1500L)
                .build());

        hotelArrayList.add(Hotel.builder()
                .hotelId(6L).city("Delhi").name("Taj").roomsAvailable(49)
                .checkInDate(LocalDate.of(2023, 03, 25))
                .checkOutDate(LocalDate.of(2023, 03, 26))
                .price(4500L)
                .build());

        hotelArrayList.forEach(x -> {
            Hotel hotel = hotelRepository.save(x);

            // sample bookings:
            bookingRepository.save(HotelBookings.builder()
                    .hotel(Hotel.builder().city(hotel.getCity()).name(hotel.getName()).hotelId(hotel.getHotelId()).build())
                    .userId(1L).id(hotel.getHotelId())
                    .build());

            // deduct 1 room:
            hotel.setRoomsAvailable(hotel.getRoomsAvailable() - 1);

            // update hotel track
            hotelRepository.save(hotel);
        });

    }
}
