package com.azaa.restaurant.controllers;

import com.azaa.restaurant.domain.ReviewCreateUpdateRequest;
import com.azaa.restaurant.domain.dtos.ReviewCreateUpdateRequestDto;
import com.azaa.restaurant.domain.dtos.ReviewDto;
import com.azaa.restaurant.domain.entities.Review;
import com.azaa.restaurant.domain.entities.User;
import com.azaa.restaurant.mappers.ReviewMapper;
import com.azaa.restaurant.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/restaurants/{restaurantId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview (
            @PathVariable String restaurantId,
            @RequestBody ReviewCreateUpdateRequestDto reviewDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewCreateUpdateRequest reviewCreateUpdateRequest = reviewMapper.toReviewCreateUpdateRequest(reviewDto);

        User user = jwtToUser(jwt);

        Review createdReview = reviewService.createReview(user, restaurantId, reviewCreateUpdateRequest);

        return ResponseEntity.ok(reviewMapper.toReviewDto(createdReview));
    }

    @GetMapping
    public Page<ReviewDto> listReviews(
            @PathVariable String restaurantId,
            @PageableDefault(
                    size = 20,
                    page = 0,
                    sort = "datePosted",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return reviewService
                .listReviews(restaurantId, pageable)
                .map(reviewMapper::toReviewDto);
    }

    @GetMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(
            @PathVariable String restaurantId,
            @PathVariable String reviewId
    ) {
        return reviewService.getReview(restaurantId, reviewId)
                .map(reviewMapper::toReviewDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping(path = "/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(
            @PathVariable String restaurantId,
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewCreateUpdateRequestDto reviewDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        ReviewCreateUpdateRequest reviewCreateUpdateRequest = reviewMapper.toReviewCreateUpdateRequest(reviewDto);

        User user = jwtToUser(jwt);

        Review updatedReview = reviewService.updateReview(
                user,
                restaurantId,
                reviewId,
                reviewCreateUpdateRequest
        );

        return ResponseEntity.ok(reviewMapper.toReviewDto(updatedReview));
    }

    @DeleteMapping(path = "/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable String restaurantId,
            @PathVariable String reviewId
    ) {
        reviewService.deleteReview(restaurantId, reviewId);
        return ResponseEntity.noContent().build();
    }

    private User jwtToUser(Jwt jwt) {
        return User.builder()
                .id(jwt.getSubject())
                .givenName(jwt.getClaimAsString("given_name"))
                .familyName(jwt.getClaimAsString("family_name"))
                .username(jwt.getClaimAsString("preferred_username"))
                .build();
    }

}
