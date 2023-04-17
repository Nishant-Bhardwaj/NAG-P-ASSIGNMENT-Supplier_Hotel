package com.nishant.hotel_supplier.repository;


import com.nishant.hotel_supplier.dto.HotelShortlistedDTO;
import com.nishant.hotel_supplier.entity.HotelShortlisted;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelShortlistRepository extends JpaRepository<HotelShortlisted, Long> {

    List<HotelShortlisted> findByUserId(Long userId);
}
