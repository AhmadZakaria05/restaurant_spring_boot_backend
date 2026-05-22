package com.azaa.restaurant.mappers;

import com.azaa.restaurant.domain.ReviewCreateUpdateRequest;
import com.azaa.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.azaa.restaurant.domain.dtos.ReviewDto;
import com.azaa.restaurant.domain.entities.Review;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {
    ReviewCreateUpdateRequest toReviewCreateUpdateRequest(ReviewCreateUpdateRequestDto dto);

    ReviewDto toReviewDto(Review review);

}
