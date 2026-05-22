package com.azaa.restaurant.exceptions;

public class RestaurantNotFoundException extends BaseException {

  public RestaurantNotFoundException(String message) {
    super(message);
  }

  public RestaurantNotFoundException() {
  }

  public RestaurantNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public RestaurantNotFoundException(Throwable cause) {
    super(cause);
  }
}
