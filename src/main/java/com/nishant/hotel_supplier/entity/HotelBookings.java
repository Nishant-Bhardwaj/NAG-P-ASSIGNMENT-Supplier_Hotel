package com.nishant.hotel_supplier.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@Table(name = "tbl_hotel_bookings")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelBookings implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private Long userId;

    private String status;

    private String roomNumber;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HotelBookings{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", status=").append(status);
        sb.append(", roomNumber=").append(roomNumber);
        sb.append('}');
        return sb.toString();
    }
}
