package com.apassistant.alexa;

import com.amazonaws.util.StringUtils;

/**
 * Price Enum that maps different price slot values into one of the three buckets.
 * TODO: For proper implementation look at using a pre initialed  hash map to create various buckets
 */
public enum PriceEnum {
    CHEAP("low"), AVERAGE("moderately"), EXPENSIVE("high");

    private String priceTxt;

    PriceEnum(String priceTxt) {
        this.priceTxt = priceTxt;
    }

    public static PriceEnum mapStringToPrice(String slotValue) {
        if(StringUtils.isNullOrEmpty(slotValue)) {
            return AVERAGE;
        }

        slotValue = slotValue.trim();

        if("moderately".equalsIgnoreCase(slotValue) || "moderately priced".equalsIgnoreCase(slotValue)) {
            return AVERAGE;
        }

        if("hight".equalsIgnoreCase(slotValue) || "high end".equalsIgnoreCase(slotValue)) {
            return EXPENSIVE;
        }

        if("low".equalsIgnoreCase(slotValue) || "cheap".equalsIgnoreCase(slotValue)) {
            return CHEAP;
        }

        return AVERAGE;
    }

    public String getPriceTxt() {
        return this.priceTxt;
    }
}
