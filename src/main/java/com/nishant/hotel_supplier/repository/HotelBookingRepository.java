package com.nishant.hotel_supplier.repository;

import com.nishant.hotel_supplier.entity.HotelBookings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBookings, Long> {
}
