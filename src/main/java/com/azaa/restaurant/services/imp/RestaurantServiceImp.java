package com.azaa.restaurant.services.imp;

import com.azaa.restaurant.domain.GeoLocation;
import com.azaa.restaurant.domain.RestaurantCreateUpdateRequest;
import com.azaa.restaurant.domain.entities.Address;
import com.azaa.restaurant.domain.entities.Photo;
import com.azaa.restaurant.domain.entities.Restaurant;
import com.azaa.restaurant.exceptions.RestaurantNotFoundException;
import com.azaa.restaurant.repositories.RestaurantsRepository;
import com.azaa.restaurant.services.GeoLocationService;
import com.azaa.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImp implements RestaurantService {

    private final RestaurantsRepository restaurantsRepository;
    private final GeoLocationService geoLocationService;

    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest request) {
        Address address = request.getAddress();
        GeoLocation geoLocation = geoLocationService.geiLocate(address);
        GeoPoint geoPoint = new GeoPoint(geoLocation.getLatitude(), geoLocation.getLongitude());

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(photoUrl -> Photo.builder()
                .url(photoUrl)
                .uploadTime(LocalDateTime.now())
                .build()).toList();

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .cuisineType(request.getCuisineType())
                .contactInformation(request.getContactInformation())
                .photos(photos)
                .geoLocation(geoPoint)
                .operatingHours(request.getOperatingHours())
                .averageRating(0f)
                .address(address)
                .build();

        return restaurantsRepository.save(restaurant);
    }

    @Override
    public Page<Restaurant> searchRestaurants(
            String query,
            Float minRating,
            Float latitude,
            Float longitude,
            Float radius,
            Pageable pageable
    ) {
        if(minRating != null && (query == null || query.isEmpty())) {
            return restaurantsRepository.findByAverageRatingGreaterThanEqual(
                    minRating,
                    pageable
            );
        }

        Float searchMinRating = minRating == null ? 0f : minRating;

        if(query != null && !query.trim().isEmpty()) {
            return restaurantsRepository.findByQueryAndMinRating(
                    query,
                    searchMinRating,
                    pageable
            );
        }

        if(latitude != null && longitude != null && radius != null) {
            return restaurantsRepository.findByLocationNear(
                    latitude,
                    longitude,
                    radius,
                    pageable
            );
        }

        return restaurantsRepository.findAll(pageable);

    }

    @Override
    public Optional<Restaurant> getRestaurant(String id) {
        return restaurantsRepository.findById(id);
    }

    @Override
    public Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest request) {
        Restaurant restaurant = getRestaurant(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID does not exist: " + id));

        GeoLocation newGeoLocation = geoLocationService.geiLocate(
                request.getAddress()
        );

        GeoPoint newGeoPoint = new GeoPoint(newGeoLocation.getLatitude(), newGeoLocation.getLongitude());

        List<String> photoIds = request.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(photoUrl -> Photo.builder()
                .url(photoUrl)
                .uploadTime(LocalDateTime.now())
                .build()).toList();

        restaurant.setName(request.getName());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setContactInformation(request.getContactInformation());
        restaurant.setAddress(request.getAddress());
        restaurant.setGeoLocation(newGeoPoint);
        restaurant.setOperatingHours(request.getOperatingHours());

        return restaurantsRepository.save(restaurant);

    }

    @Override
    public void deleteRestaurant(String id) {
        restaurantsRepository.deleteById(id);
    }
}
