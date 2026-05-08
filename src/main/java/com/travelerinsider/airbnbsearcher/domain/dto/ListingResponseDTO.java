package com.travelerinsider.airbnbsearcher.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponseDTO implements Serializable {
    private Long id;
    private String name;
    private String description;
    private String pictureUrl;
    private String hostName;
    private String neighbourhood;
    private Double latitude;
    private Double longitude;
    private String propertyType;
    private String roomType;
    private Integer accommodates;
    private Float bathrooms;
    private Float bedrooms;
    private Float beds;
    private String amenities;
    private String price;
    private Integer numberOfReviews;
    private Float reviewScoresRating;
    private Boolean instantBookable;
}
