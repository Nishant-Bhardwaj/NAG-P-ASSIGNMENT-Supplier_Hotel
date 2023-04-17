package com.nishant.hotel_supplier.repository;

import com.nishant.hotel_supplier.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {

    @Query(
            value = "select * from tbl_hotel hht where hht.name=:hotelName AND hht.city=:hotelCity AND hht.check_in_date=:checkInDate AND hht.check_out_date=:checkOutDate",
            nativeQuery = true
    )
    List<Hotel> findByFilters(String hotelName, String hotelCity, LocalDate checkInDate, LocalDate checkOutDate);

    @Query(
            value = "select * from tbl_hotel hht left join tbl_hotel_bookings hb on hht.hotel_id=hb.hotel_id left join tbl_hotel_shortlisted hs on hht.hotel_id=hs.hotel_id",
            nativeQuery = true
    )
    List<Hotel> findAllHotels();
}
