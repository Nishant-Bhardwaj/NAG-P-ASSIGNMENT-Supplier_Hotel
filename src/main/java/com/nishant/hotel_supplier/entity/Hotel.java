package com.nishant.hotel_supplier.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name = "tbl_hotel")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_id")
    private Long hotelId;

    private String name;

    private String city;

    private int roomsAvailable;

    @JsonIgnore
    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.REMOVE
            // Cascade the remove operation of hotel entity to booking
            // i.e., if hotel is deleted from hotel entity,
            // bookings mapped to that hotel will be deleted as well
    )
    private List<HotelBookings> hotelBookingsList = new ArrayList<HotelBookings>();

    @JsonIgnore
    @OneToMany(
            mappedBy = "hotel",
            cascade = CascadeType.REMOVE
            // Cascade the remove operation of hotel entity to shortlisted hotel
            // i.e., if hotel is deleted from hotel entity,
            // shortlisted mapped to that hotel will be deleted as well
    )
    private List<HotelShortlisted> hotelShortlistedList = new ArrayList<HotelShortlisted>();

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Long price;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Hotel{");
        sb.append("hotelId=").append(hotelId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", rooms='").append(roomsAvailable).append('\'');
        sb.append(", checkInDate='").append(checkInDate).append('\'');
        sb.append(", checkOutDate='").append(checkOutDate).append('\'');
        sb.append(", price='").append(price).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
