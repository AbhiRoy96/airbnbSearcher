package com.travelerinsider.airbnbsearcher.domain.model;

import com.travelerinsider.airbnbsearcher.domain.listener.ListingEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "listings")
@EntityListeners(ListingEntityListener.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing {

    @Id
    private Long id;

    @Column(name = "listing_url", columnDefinition = "TEXT")
    private String listingUrl;

    @Column(name = "scrape_id")
    private Long scrapeId;

    @Column(name = "last_scraped")
    private LocalDate lastScraped;

    @Column(columnDefinition = "TEXT")
    private String source;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "neighborhood_overview", columnDefinition = "TEXT")
    private String neighborhoodOverview;

    @Column(name = "picture_url", columnDefinition = "TEXT")
    private String pictureUrl;

    @Column(name = "host_id")
    private Long hostId;

    @Column(name = "host_url", columnDefinition = "TEXT")
    private String hostUrl;

    @Column(name = "host_name", columnDefinition = "TEXT")
    private String hostName;

    @Column(name = "host_since")
    private LocalDate hostSince;

    @Column(name = "host_location", columnDefinition = "TEXT")
    private String hostLocation;

    @Column(name = "host_about", columnDefinition = "TEXT")
    private String hostAbout;

    @Column(name = "host_response_time", columnDefinition = "TEXT")
    private String hostResponseTime;

    @Column(name = "host_response_rate", columnDefinition = "TEXT")
    private String hostResponseRate;

    @Column(name = "host_acceptance_rate", columnDefinition = "TEXT")
    private String hostAcceptanceRate;

    @Column(name = "host_is_superhost")
    private Boolean hostIsSuperhost;

    @Column(name = "host_thumbnail_url", columnDefinition = "TEXT")
    private String hostThumbnailUrl;

    @Column(name = "host_picture_url", columnDefinition = "TEXT")
    private String hostPictureUrl;

    @Column(name = "host_neighbourhood", columnDefinition = "TEXT")
    private String hostNeighbourhood;

    @Column(name = "host_listings_count")
    private Integer hostListingsCount;

    @Column(name = "host_total_listings_count")
    private Integer hostTotalListingsCount;

    @Column(name = "host_verifications", columnDefinition = "TEXT")
    private String hostVerifications;

    @Column(name = "host_has_profile_pic")
    private Boolean hostHasProfilePic;

    @Column(name = "host_identity_verified")
    private Boolean hostIdentityVerified;

    @Column(columnDefinition = "TEXT")
    private String neighbourhood;

    @Column(name = "neighbourhood_cleansed", columnDefinition = "TEXT")
    private String neighbourhoodCleansed;

    @Column(name = "neighbourhood_group_cleansed", columnDefinition = "TEXT")
    private String neighbourhoodGroupCleansed;

    private Double latitude;

    private Double longitude;

    @Column(name = "property_type", columnDefinition = "TEXT")
    private String propertyType;

    @Column(name = "room_type", columnDefinition = "TEXT")
    private String roomType;

    private Integer accommodates;

    private Float bathrooms;

    @Column(name = "bathrooms_text", columnDefinition = "TEXT")
    private String bathroomsText;

    private Float bedrooms;

    private Float beds;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    @Column(columnDefinition = "TEXT")
    private String price;

    @Column(name = "minimum_nights")
    private Integer minimumNights;

    @Column(name = "maximum_nights")
    private Integer maximumNights;

    @Column(name = "minimum_minimum_nights")
    private Integer minimumMinimumNights;

    @Column(name = "maximum_minimum_nights")
    private Integer maximumMinimumNights;

    @Column(name = "minimum_maximum_nights")
    private Integer minimumMaximumNights;

    @Column(name = "maximum_maximum_nights")
    private Integer maximumMaximumNights;

    @Column(name = "minimum_nights_avg_ntm")
    private Float minimumNightsAvgNtm;

    @Column(name = "maximum_nights_avg_ntm")
    private Float maximumNightsAvgNtm;

    @Column(name = "calendar_updated", columnDefinition = "TEXT")
    private String calendarUpdated;

    @Column(name = "has_availability")
    private Boolean hasAvailability;

    @Column(name = "availability_30")
    private Integer availability30;

    @Column(name = "availability_60")
    private Integer availability60;

    @Column(name = "availability_90")
    private Integer availability90;

    @Column(name = "availability_365")
    private Integer availability365;

    @Column(name = "calendar_last_scraped")
    private LocalDate calendarLastScraped;

    @Column(name = "number_of_reviews")
    private Integer numberOfReviews;

    @Column(name = "number_of_reviews_ltm")
    private Integer numberOfReviewsLtm;

    @Column(name = "number_of_reviews_l30d")
    private Integer numberOfReviewsL30d;

    @Column(name = "availability_eoy")
    private Integer availabilityEoy;

    @Column(name = "number_of_reviews_ly")
    private Integer numberOfReviewsLy;

    @Column(name = "estimated_occupancy_l365d")
    private Float estimatedOccupancyL365d;

    @Column(name = "estimated_revenue_l365d")
    private BigDecimal estimatedRevenueL365d;

    @Column(name = "first_review")
    private LocalDate firstReview;

    @Column(name = "last_review")
    private LocalDate lastReview;

    @Column(name = "review_scores_rating")
    private Float reviewScoresRating;

    @Column(name = "review_scores_accuracy")
    private Float reviewScoresAccuracy;

    @Column(name = "review_scores_cleanliness")
    private Float reviewScoresCleanliness;

    @Column(name = "review_scores_checkin")
    private Float reviewScoresCheckin;

    @Column(name = "review_scores_communication")
    private Float reviewScoresCommunication;

    @Column(name = "review_scores_location")
    private Float reviewScoresLocation;

    @Column(name = "review_scores_value")
    private Float reviewScoresValue;

    @Column(columnDefinition = "TEXT")
    private String license;

    @Column(name = "instant_bookable")
    private Boolean instantBookable;

    @Column(name = "calculated_host_listings_count")
    private Integer calculatedHostListingsCount;

    @Column(name = "calculated_host_listings_count_entire_homes")
    private Integer calculatedHostListingsCountEntireHomes;

    @Column(name = "calculated_host_listings_count_private_rooms")
    private Integer calculatedHostListingsCountPrivateRooms;

    @Column(name = "calculated_host_listings_count_shared_rooms")
    private Integer calculatedHostListingsCountSharedRooms;

    @Column(name = "reviews_per_month")
    private Float reviewsPerMonth;
}
