package com.travelerinsider.airbnbsearcher.domain.elastic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(indexName = "listings", createIndex = false)
@Setting(shards = 1, replicas = 0)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String listingUrl;

    @Field(type = FieldType.Long)
    private Long scrapeId;

    @Field(type = FieldType.Date)
    private LocalDate lastScraped;

    @Field(type = FieldType.Text)
    private String source;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Text)
    private String neighborhoodOverview;

    @Field(type = FieldType.Text)
    private String pictureUrl;

    @Field(type = FieldType.Long)
    private Long hostId;

    @Field(type = FieldType.Text)
    private String hostUrl;

    @Field(type = FieldType.Text)
    private String hostName;

    @Field(type = FieldType.Date)
    private LocalDate hostSince;

    @Field(type = FieldType.Text)
    private String hostLocation;

    @Field(type = FieldType.Text)
    private String hostAbout;

    @Field(type = FieldType.Keyword)
    private String hostResponseTime;

    @Field(type = FieldType.Text)
    private String hostResponseRate;

    @Field(type = FieldType.Text)
    private String hostAcceptanceRate;

    @Field(type = FieldType.Boolean)
    private Boolean hostIsSuperhost;

    @Field(type = FieldType.Text)
    private String hostThumbnailUrl;

    @Field(type = FieldType.Text)
    private String hostPictureUrl;

    @Field(type = FieldType.Keyword)
    private String hostNeighbourhood;

    @Field(type = FieldType.Integer)
    private Integer hostListingsCount;

    @Field(type = FieldType.Integer)
    private Integer hostTotalListingsCount;

    @Field(type = FieldType.Text)
    private String hostVerifications;

    @Field(type = FieldType.Boolean)
    private Boolean hostHasProfilePic;

    @Field(type = FieldType.Boolean)
    private Boolean hostIdentityVerified;

    @Field(type = FieldType.Keyword)
    private String neighbourhood;

    @Field(type = FieldType.Keyword)
    private String neighbourhoodCleansed;

    @Field(type = FieldType.Keyword)
    private String neighbourhoodGroupCleansed;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

    @Field(type = FieldType.Keyword)
    private String propertyType;

    @Field(type = FieldType.Keyword)
    private String roomType;

    @Field(type = FieldType.Integer)
    private Integer accommodates;

    @Field(type = FieldType.Float)
    private Float bathrooms;

    @Field(type = FieldType.Text)
    private String bathroomsText;

    @Field(type = FieldType.Float)
    private Float bedrooms;

    @Field(type = FieldType.Float)
    private Float beds;

    @Field(type = FieldType.Text)
    private String amenities;

    @Field(type = FieldType.Keyword)
    private String price;

    @Field(type = FieldType.Integer)
    private Integer minimumNights;

    @Field(type = FieldType.Integer)
    private Integer maximumNights;

    @Field(type = FieldType.Integer)
    private Integer minimumMinimumNights;

    @Field(type = FieldType.Integer)
    private Integer maximumMinimumNights;

    @Field(type = FieldType.Integer)
    private Integer minimumMaximumNights;

    @Field(type = FieldType.Integer)
    private Integer maximumMaximumNights;

    @Field(type = FieldType.Float)
    private Float minimumNightsAvgNtm;

    @Field(type = FieldType.Float)
    private Float maximumNightsAvgNtm;

    @Field(type = FieldType.Text)
    private String calendarUpdated;

    @Field(type = FieldType.Boolean)
    private Boolean hasAvailability;

    @Field(type = FieldType.Integer)
    private Integer availability30;

    @Field(type = FieldType.Integer)
    private Integer availability60;

    @Field(type = FieldType.Integer)
    private Integer availability90;

    @Field(type = FieldType.Integer)
    private Integer availability365;

    @Field(type = FieldType.Date)
    private LocalDate calendarLastScraped;

    @Field(type = FieldType.Integer)
    private Integer numberOfReviews;

    @Field(type = FieldType.Integer)
    private Integer numberOfReviewsLtm;

    @Field(type = FieldType.Integer)
    private Integer numberOfReviewsL30d;

    @Field(type = FieldType.Integer)
    private Integer availabilityEoy;

    @Field(type = FieldType.Integer)
    private Integer numberOfReviewsLy;

    @Field(type = FieldType.Float)
    private Float estimatedOccupancyL365d;

    @Field(type = FieldType.Double)
    private BigDecimal estimatedRevenueL365d;

    @Field(type = FieldType.Date)
    private LocalDate firstReview;

    @Field(type = FieldType.Date)
    private LocalDate lastReview;

    @Field(type = FieldType.Float)
    private Float reviewScoresRating;

    @Field(type = FieldType.Float)
    private Float reviewScoresAccuracy;

    @Field(type = FieldType.Float)
    private Float reviewScoresCleanliness;

    @Field(type = FieldType.Float)
    private Float reviewScoresCheckin;

    @Field(type = FieldType.Float)
    private Float reviewScoresCommunication;

    @Field(type = FieldType.Float)
    private Float reviewScoresLocation;

    @Field(type = FieldType.Float)
    private Float reviewScoresValue;

    @Field(type = FieldType.Text)
    private String license;

    @Field(type = FieldType.Boolean)
    private Boolean instantBookable;

    @Field(type = FieldType.Integer)
    private Integer calculatedHostListingsCount;

    @Field(type = FieldType.Integer)
    private Integer calculatedHostListingsCountEntireHomes;

    @Field(type = FieldType.Integer)
    private Integer calculatedHostListingsCountPrivateRooms;

    @Field(type = FieldType.Integer)
    private Integer calculatedHostListingsCountSharedRooms;

    @Field(type = FieldType.Float)
    private Float reviewsPerMonth;
}
