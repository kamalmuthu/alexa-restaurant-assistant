package com.apassistant.alexa;

import com.amazonaws.util.StringUtils;

/**
 * Ratings Enum that maps different ratings slot values into one of the three buckets.
 * TODO: For proper implementation look at using a pre initialed  hash map to create various buckets */
public enum RatingsEnum {

    LOW("two"), MEDIUM("three"), HIGH("four");

    private String stars = "four";

    RatingsEnum(String stars) {
        this.stars = stars;
    }

    public static RatingsEnum mapStringToRatings(String slotValue) {
        if(StringUtils.isNullOrEmpty(slotValue)) {
            return HIGH;
        }

        slotValue = slotValue.trim();

        if("four".equalsIgnoreCase(slotValue) || "good".equalsIgnoreCase(slotValue)) {
            return HIGH;
        }

        if("three".equalsIgnoreCase(slotValue) || "average".equalsIgnoreCase(slotValue)) {
            return MEDIUM;
        }

        if("two".equalsIgnoreCase(slotValue) || "okay".equalsIgnoreCase(slotValue)) {
            return LOW;
        }

        return HIGH;
    }

    public String getStars() {
        return this.stars;
    }
}
