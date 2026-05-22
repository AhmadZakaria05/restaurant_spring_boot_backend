package com.azaa.restaurant.mappers;

import com.azaa.restaurant.domain.RestaurantCreateUpdateRequest;
import com.azaa.restaurant.domain.dtos.GeoPointDto;
import com.azaa.restaurant.domain.dtos.RestaurantCreateUpdateRequestDto;
import com.azaa.restaurant.domain.dtos.RestaurantDto;
import com.azaa.restaurant.domain.dtos.RestaurantSummaryDto;
import com.azaa.restaurant.domain.entities.Restaurant;
import com.azaa.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {

    RestaurantCreateUpdateRequest toRestaurantCreateUpdateRequest(RestaurantCreateUpdateRequestDto dto);

    @Mapping(source = "reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantDto toRestaurantDto(Restaurant restaurant);

    // reviews  المصدر يعني الي جاييني
    // totalReviews الي هدفنا نعبيه
    // ولانه هذا مجموعه تقييمات و هذا عدد التقييمات ف ما بيقدر مياشره يحول
    // populateTotalReviews رح يستخدمها في التحويل
    @Mapping(source = "reviews", target = "totalReviews", qualifiedByName = "populateTotalReviews")
    RestaurantSummaryDto toRestaurantSummaryDto(Restaurant restaurant);

    @Named("populateTotalReviews")
    default Integer populateTotalReview(List<Review> reviews) {
        return reviews.size();
    }

    @Mapping(target = "latitude", expression = "java(geoPoint.getLat())")
    @Mapping(target = "longitude", expression = "java(geoPoint.getLon())")
    GeoPointDto toGeoPointDto(GeoPoint geoPoint);
}
