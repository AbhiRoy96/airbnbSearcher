
-- Creates the listings table
CREATE TABLE listings (
                          id BIGINT PRIMARY KEY,
                          listing_url TEXT,
                          scrape_id BIGINT,
                          last_scraped DATE,
                          source TEXT,

                          name TEXT,
                          description TEXT,
                          neighborhood_overview TEXT,
                          picture_url TEXT,

                          host_id BIGINT,
                          host_url TEXT,
                          host_name TEXT,
                          host_since DATE,
                          host_location TEXT,
                          host_about TEXT,

                          host_response_time TEXT,
                          host_response_rate TEXT,
                          host_acceptance_rate TEXT,
                          host_is_superhost BOOLEAN,

                          host_thumbnail_url TEXT,
                          host_picture_url TEXT,
                          host_neighbourhood TEXT,

                          host_listings_count INT,
                          host_total_listings_count INT,

                          host_verifications TEXT, -- can convert to JSONB later
                          host_has_profile_pic BOOLEAN,
                          host_identity_verified BOOLEAN,

                          neighbourhood TEXT,
                          neighbourhood_cleansed TEXT,
                          neighbourhood_group_cleansed TEXT,

                          latitude DOUBLE PRECISION,
                          longitude DOUBLE PRECISION,

                          property_type TEXT,
                          room_type TEXT,

                          accommodates INT,
                          bathrooms FLOAT,
                          bathrooms_text TEXT,
                          bedrooms FLOAT,
                          beds FLOAT,

                          amenities TEXT, -- ideally JSONB if cleaned

                          price TEXT,
                          minimum_nights INT,
                          maximum_nights INT,

                          minimum_minimum_nights INT,
                          maximum_minimum_nights INT,
                          minimum_maximum_nights INT,
                          maximum_maximum_nights INT,

                          minimum_nights_avg_ntm FLOAT,
                          maximum_nights_avg_ntm FLOAT,

                          calendar_updated TEXT,
                          has_availability BOOLEAN,

                          availability_30 INT,
                          availability_60 INT,
                          availability_90 INT,
                          availability_365 INT,

                          calendar_last_scraped DATE,

                          number_of_reviews INT,
                          number_of_reviews_ltm INT,
                          number_of_reviews_l30d INT,

                          availability_eoy INT,
                          number_of_reviews_ly INT,

                          estimated_occupancy_l365d FLOAT,
                          estimated_revenue_l365d NUMERIC,

                          first_review DATE,
                          last_review DATE,

                          review_scores_rating FLOAT,
                          review_scores_accuracy FLOAT,
                          review_scores_cleanliness FLOAT,
                          review_scores_checkin FLOAT,
                          review_scores_communication FLOAT,
                          review_scores_location FLOAT,
                          review_scores_value FLOAT,

                          license TEXT,
                          instant_bookable BOOLEAN,

                          calculated_host_listings_count INT,
                          calculated_host_listings_count_entire_homes INT,
                          calculated_host_listings_count_private_rooms INT,
                          calculated_host_listings_count_shared_rooms INT,

                          reviews_per_month FLOAT
);



-- Creates the indexes
CREATE INDEX idx_location ON listings(latitude, longitude);
CREATE INDEX idx_price ON listings(price);
CREATE INDEX idx_reviews ON listings(review_scores_rating);


-- v2 Migration Scripts.
ALTER TABLE listings
    ADD COLUMN currency VARCHAR(3),
    ADD COLUMN price_numeric NUMERIC;


-- Updates pricing info in the listings table
UPDATE listings
SET
    currency = CASE
                   WHEN price LIKE '$%' THEN 'USD'
                   WHEN price LIKE '₹%' THEN 'INR'
                   WHEN price LIKE '€%' THEN 'EUR'
                   ELSE NULL
        END,

    price_numeric = REPLACE(
            REPLACE(
                    REPLACE(price, '$', ''),
                    ',', ''),
            '₹', '')::NUMERIC;