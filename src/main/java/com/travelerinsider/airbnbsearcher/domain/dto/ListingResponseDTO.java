package com.travelerinsider.airbnbsearcher.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingResponseDTO {
    private Long id;
    private String listingUrl;
    private Long scrapeId;
    private LocalDate lastScraped;
    private String source;
    private String name;
    private String description;
    private String neighborhoodOverview;
    private String pictureUrl;
    private Long hostId;
    private String hostUrl;
    private String hostName;
    private LocalDate hostSince;
    private String hostLocation;
    private String hostAbout;
    private String hostResponseTime;
    private String hostResponseRate;
    private String hostAcceptanceRate;
    private Boolean hostIsSuperhost;
    private String hostThumbnailUrl;
    private String hostPictureUrl;
    private String hostNeighbourhood;
    private Integer hostListingsCount;
    private Integer hostTotalListingsCount;
    private String hostVerifications;
    private Boolean hostHasProfilePic;
    private Boolean hostIdentityVerified;
    private String neighbourhood;
    private String neighbourhoodCleansed;
    private String neighbourhoodGroupCleansed;
    private Double latitude;
    private Double longitude;
    private String propertyType;
    private String roomType;
    private Integer accommodates;
    private Float bathrooms;
    private String bathroomsText;
    private Float bedrooms;
    private Float beds;
    private String amenities;
    private String price;
    private Integer minimumNights;
    private Integer maximumNights;
    private Integer minimumMinimumNights;
    private Integer maximumMinimumNights;
    private Integer minimumMaximumNights;
    private Integer maximumMaximumNights;
    private Float minimumNightsAvgNtm;
    private Float maximumNightsAvgNtm;
    private String calendarUpdated;
    private Boolean hasAvailability;
    private Integer availability30;
    private Integer availability60;
    private Integer availability90;
    private Integer availability365;
    private LocalDate calendarLastScraped;
    private Integer numberOfReviews;
    private Integer numberOfReviewsLtm;
    private Integer numberOfReviewsL30d;
    private Integer availabilityEoy;
    private Integer numberOfReviewsLy;
    private Float estimatedOccupancyL365d;
    private BigDecimal estimatedRevenueL365d;
    private LocalDate firstReview;
    private LocalDate lastReview;
    private Float reviewScoresRating;
    private Float reviewScoresAccuracy;
    private Float reviewScoresCleanliness;
    private Float reviewScoresCheckin;
    private Float reviewScoresCommunication;
    private Float reviewScoresLocation;
    private Float reviewScoresValue;
    private String license;
    private Boolean instantBookable;
    private Integer calculatedHostListingsCount;
    private Integer calculatedHostListingsCountEntireHomes;
    private Integer calculatedHostListingsCountPrivateRooms;
    private Integer calculatedHostListingsCountSharedRooms;
    private Float reviewsPerMonth;
}
