package com.azaa.restaurant.services;

import com.azaa.restaurant.domain.GeoLocation;
import com.azaa.restaurant.domain.entities.Address;
import org.springframework.stereotype.Service;

@Service
public interface GeoLocationService {
    GeoLocation geiLocate(Address address);
}
