package com.example.demo.model;

import java.util.List;

public class HotelDetailRequest {
	 private List<HotelCodeBrandCode> hotelCodeBrandCodeList;

	    // Getters and Setters
	    public List<HotelCodeBrandCode> getHotelCodeBrandCodeList() {
	        return hotelCodeBrandCodeList;
	    }

	    public void setHotelCodeBrandCodeList(List<HotelCodeBrandCode> hotelCodeBrandCodeList) {
	        this.hotelCodeBrandCodeList = hotelCodeBrandCodeList;
	    }

	    // Inner class to represent each object in the list
	    public static class HotelCodeBrandCode {
	        private String hotelCode;
	        private String brandCode;

	        // Getters and Setters
	        public String getHotelCode() {
	            return hotelCode;
	        }

	        public void setHotelCode(String hotelCode) {
	            this.hotelCode = hotelCode;
	        }

	        public String getBrandCode() {
	            return brandCode;
	        }

	        public void setBrandCode(String brandCode) {
	            this.brandCode = brandCode;
	        }
	    }
}
