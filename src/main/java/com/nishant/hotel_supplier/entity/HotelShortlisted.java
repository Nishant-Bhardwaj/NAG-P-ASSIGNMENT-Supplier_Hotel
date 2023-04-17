package com.nishant.hotel_supplier.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Builder
@Table(name = "tbl_hotel_shortlisted")
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelShortlisted implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private Long userId;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HotelShortlisted{");
        sb.append("id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append('}');
        return sb.toString();
    }
}
