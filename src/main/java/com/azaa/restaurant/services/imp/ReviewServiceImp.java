package com.azaa.restaurant.services.imp;


import com.azaa.restaurant.domain.ReviewCreateUpdateRequest;
import com.azaa.restaurant.domain.entities.Photo;
import com.azaa.restaurant.domain.entities.Restaurant;
import com.azaa.restaurant.domain.entities.Review;
import com.azaa.restaurant.domain.entities.User;
import com.azaa.restaurant.exceptions.RestaurantNotFoundException;
import com.azaa.restaurant.exceptions.ReviewNotAllowedException;
import com.azaa.restaurant.repositories.RestaurantsRepository;
import com.azaa.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final RestaurantsRepository restaurantsRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExistingReview = restaurant.getReviews().stream()
                .anyMatch(r -> r.getWrittenBy().getId().equals(author.getId()));

        if(hasExistingReview) {
            throw new ReviewNotAllowedException("User has already reviewed this restaurant");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Photo> photos = review.getPhotoIds().stream().map(url -> {
            return Photo.builder()
                    .url(url)
                    .uploadTime(now)
                    .build();
        }).toList();

        String reviewId =  UUID.randomUUID().toString();
        Review reviewToCreated = Review.builder()
                .id(reviewId)
                .content(review.getContent())
                .rating(review.getRating())
                .datePosted(now)
                .lastEdited(now)
                .writtenBy(author)
                .photos(photos)
                .build();

        restaurant.getReviews().add(reviewToCreated);

        updateRestaurantAverageRating(restaurant);

        Restaurant savedRestaurant = restaurantsRepository.save(restaurant);

        return getReviewFromRestaurant(reviewId, savedRestaurant)
                .orElseThrow(() -> new RuntimeException("Error retrieving created review"));


    }

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        List<Review> reviews = restaurant.getReviews();

        Sort sort = pageable.getSort();

        if(sort.isSorted()) {
            Sort.Order order = sort.iterator().next();// نجيب اول شرط ترتيب
            String property = order.getProperty(); // نقرأ اسم الحقل مثلا rating  datePosted
            boolean isAscending = order.getDirection().isAscending();// لمعرفت الاتجاه

            // Comparator هي اداه تقارن بين عنصرين عشان ترتبهم
            Comparator<Review> comparator = switch (property) {
                case "datePosted" -> Comparator.comparing(Review::getDatePosted); // قارن حسب تاريخ النشر
                case "rating" -> Comparator.comparing(Review::getRating); // قارن حسب التقييم
                default -> Comparator.comparing(Review::getDatePosted); //  اذا المستخدم كتب اشي غلط
            };

            // هذا اسمه Ternary Operator
            //  معناه اذا ASC الترتيب عادي
            // و اذا DESC اعكس الترتيب
            reviews.sort(isAscending ? comparator : comparator.reversed());
        } else { // اذا ما في sort
            reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());// الاحدث اولاً
        }

        // رقم اول عنصر لازم نبدأ منه في الصفحه الحاليه
        int start = (int) pageable.getOffset();

        // يعني اذا الصفحه المطلوبه فاضيه --> رجع صفحه فارغة
        if(start >= reviews.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, reviews.size());
        }

        // تحديد اخر عنصر بالصفحه مع عدم تجاوز حجم التقييمات الكلي
        int end = Math.min(start + pageable.getPageSize(), reviews.size());

        return new PageImpl<>(
                reviews.subList(start, end), // البيانات الحاليه (الصفحة)
                pageable, // معلومات الصفحة
                reviews.size() // العدد الكلي
        );
    }

    @Override
    public Optional<Review> getReview(String restaurantId, String reviewId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        return getReviewFromRestaurant(reviewId, restaurant);
    }

    private static Optional<Review> getReviewFromRestaurant(String reviewId, Restaurant restaurant) {
        return restaurant.getReviews()
                .stream()
                .filter(r -> reviewId.equals(r.getId()))
                .findFirst();
    }

    @Override
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest review) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        String authorId = author.getId();
        Review existingReview = getReviewFromRestaurant(reviewId, restaurant)
                .orElseThrow(() -> new ReviewNotAllowedException("Review does not exist"));

        // بتأكد انه الشخص الي بده يعدل نفسه صاحب التقييم
        if (!authorId.equals(existingReview.getWrittenBy().getId())) {
            throw new ReviewNotAllowedException("Cannot update another user's review");
        }

        // اذا مر يومين على الانشاء يمنع التعديل
        if (LocalDateTime.now().isAfter(existingReview.getDatePosted().plusHours(48))) {
            throw new ReviewNotAllowedException("Review can no longer be edited");
        }

        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        existingReview.setLastEdited(LocalDateTime.now());

        existingReview.setPhotos(review.getPhotoIds().stream()
                .map(photoId -> Photo.builder()
                        .url(photoId)
                        .uploadTime(LocalDateTime.now())
                        .build()).toList());

        updateRestaurantAverageRating(restaurant);
        // هنا اصبح لدي review جديد اسمه existingReview جاهز للاضافه

        // فيها كل ال review ما عدا الي بدي اعدله
        // يعني ما فيها القديم
        List<Review> updatedReviews = restaurant.getReviews().stream()
                .filter(r -> !reviewId.equals(r.getId()))
                .collect(Collectors.toList());
        updatedReviews.add(existingReview); // ضفت عليها ال review المعدل

        restaurant.setReviews(updatedReviews); // ضفت الكل بما في ذلك المعدل للمطعم

        restaurantsRepository.save(restaurant); // حفظت المطعم بعد التعديل

        return existingReview; // رجعت ال review المعدل

    }

    @Override
    public void deleteReview(String restaurantId, String reviewId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        List<Review> filteredReview = restaurant.getReviews().stream()
                .filter(r -> !reviewId.equals(r.getId()))
                .toList();
        restaurant.setReviews(filteredReview);

        updateRestaurantAverageRating(restaurant);

        restaurantsRepository.save(restaurant);
    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantsRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        "Restaurant not found with id " + restaurantId
                ));
    }

    private void updateRestaurantAverageRating(Restaurant restaurant) {
        List<Review> reviews = restaurant.getReviews();
        if(reviews.isEmpty()) {
            restaurant.setAverageRating(0.0f);
        } else {
            double averageRating = reviews.stream().mapToDouble(Review::getRating)
                    .average()
                    .orElse(0.0);
            restaurant.setAverageRating((float) averageRating);
        }


    }
}
