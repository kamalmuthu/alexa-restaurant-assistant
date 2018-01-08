package com.apassistant.alexa;

import java.util.ArrayList;
import java.util.List;

/**
 * Proxy service to search records from Yelp. Actual implementation should pull records from yelp via an API gateway
 */
public class YelpProxyService {

    public List<RARestaurant> findRestaurants(String foodType, PriceEnum price, RatingsEnum ratings) {
        RARestaurant ra1 = new RARestaurant();
        ra1.setName("Snappy Dragon");
        ra1.setRating("five");

        RARestaurant ra2 = new RARestaurant();
        ra2.setName("Din Tai Fung University Village");
        ra2.setRating("four point six");

        RARestaurant ra3 = new RARestaurant();
        ra3.setName("Ballard Mandarin Chinese Restaurant");
        ra3.setRating("four point two");

        RARestaurant ra4 = new RARestaurant();
        ra4.setName("Ming China Bistro");
        ra4.setRating("four point two");

        RARestaurant ra5 = new RARestaurant();
        ra5.setName("Jade Restaurant and Lounge");
        ra5.setRating("four");

        List<RARestaurant> result = new ArrayList<>();
        result.add(ra1);
        result.add(ra2);
        result.add(ra3);
        result.add(ra4);
        result.add(ra5);

        return result;
    }
}
