package com.azaa.restaurant.repositories;

import com.azaa.restaurant.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantsRepository extends ElasticsearchRepository<Restaurant, String> {

    Page<Restaurant> findByAverageRatingGreaterThanEqual(Float minRating, Pageable pageable);

    //@Query يعني نفذ هذا الاستعلام المتخصص بدل الاستعلام التلقائي
    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"range\": {\"averageRating\": {\"gte\": ?1}}}" +
            "    ]," +
            "    \"should\": [" +
            "      {\"fuzzy\": {\"name\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}," +
            "      {\"fuzzy\": {\"cuisineType\": {\"value\": \"?0\", \"fuzziness\": \"AUTO\"}}}" +
            "    ]," +
            "    \"minimum_should_match\": 1" +
            "  }" +
            "}" +
            "}")
    Page<Restaurant> findByQueryAndMinRating(String query, Float minRating, Pageable pageable);
    // query  تمثل النص الي كتبه المستخدم
    // minRating اقل تقييم مطلوب


    @Query("{" +
            "  \"bool\": {" +
            "    \"must\": [" +
            "      {\"geo_distance\": {" +
            "        \"distance\": \"?2km\"," +
            "        \"geoLocation\": {" +
            "          \"lat\": ?0," +
            "          \"lon\": ?1" +
            "        }" +
            "      }}" +
            "    ]" +
            "  }" +
            "}")
    Page<Restaurant> findByLocationNear(
            Float latitude,
            Float longitude,
            Float radiusKm,
            Pageable pageable);
}
