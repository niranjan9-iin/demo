package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.HotelDetailRequest;
import com.example.demo.model.HotelDetailRequest.HotelCodeBrandCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class HotelDetailsService {
	// staging key
	 private static final String api_key = "EMMyBVyWDU56GpigMGWDlDDf7zucpYjd";
	// int key
	//private static final String api_key = "uG3jpwBVdq71uqfT6dvwGFavAjmpQ4Vr";
	// staging hostname
	public static final String hostName ="https://staging-api.ihg.com/hotels/";
	// int hostname
	//public static final String hostName = "https://int-api.ihg.com/hotels/";
	@Autowired
	private RestTemplate restTemplate;
	public static ArrayList<String> fieldsets = new ArrayList<>(Arrays.asList("accessibility", "address",
			"alternatePayments", "attractions", "awardsAndRatings", "badges", "barAndLounge", "brandInfo",
			"businessCenter", "contact", "contactInformation", "corporatePrograms", "evCharging", "facilities", "fee",
			"foodAndBeverage", "greenEngage", "highlights", "hotelFacilities", "location", "marketing", "media",
			"nonRoomInventory", "parking", "policies", "poolAndFitness", "profile", "publicAreas", "recreations",
			"renovations", "restaurant", "room", "roomTypes", "safety", "services", "shoppingFacilities", "tax",
			"technology", "transportation", "weddingDetails", "payments"));
	//public static ArrayList<String> fieldsets = new ArrayList<>(Arrays.asList("profile"));

	public String getHotelDetails(String hotelCode, String brandCode) {
		// Generate URLs for v1 and v3
		String v1 = generateURL("v1", hotelCode, brandCode);
		String v3 = generateURL("v3", hotelCode, brandCode);

		// Initialize responses
		Object responseV1 = null;
		Object responseV3 = null;
		String result = "";
		try {
			System.out.println("getHotelDetails URL:" + v1);
			// Execute first request
			responseV1 = restTemplate.exchange(v1, HttpMethod.GET, generateHeaders(), Object.class).getBody();

			// Execute second request
			responseV3 = restTemplate.exchange(v3, HttpMethod.GET, generateHeaders(), Object.class).getBody();
			// Convert responseBody to JSON string
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse1 = objectMapper.writeValueAsString(responseV1);
			String jsonResponse2 = objectMapper.writeValueAsString(responseV3);
			// Parse JSON strings
			JsonNode rootNode1 = objectMapper.readTree(jsonResponse1);
			JsonNode rootNode2 = objectMapper.readTree(jsonResponse2);

			// Compare "hotelContent" and "hotelInfo" nodes
			if (rootNode2.has("hotelContent") && rootNode1.has("hotelInfo")) {
				result = ResponseComparator.compareNodes(rootNode2.get("hotelContent").get(0),
						rootNode1.get("hotelInfo"));
			} else {
				System.out.println("No matching nodes (hotelContent and hotelInfo) found for comparison.");
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error while fetching hotel details: " + ex.getMessage(), ex);
		}

		return result;
	}

	public String getHotelDetailsPost(HotelDetailRequest hdr) {
		Object responsev3 = null;
		Object responsev2 = null;
		String result = "";
		try {
			responsev3 = restTemplate
					.exchange(generateURL("v3"), HttpMethod.POST, generateHeadersPost(hdr, false), Object.class)
					.getBody();
			responsev2 = restTemplate
					.exchange(generateURL("v2"), HttpMethod.POST, generateHeadersPost(hdr, true), Object.class)
					.getBody();
			// Convert responseBody to JSON string
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonResponse1 = objectMapper.writeValueAsString(responsev2);
			String jsonResponse2 = objectMapper.writeValueAsString(responsev3);
			// Parse JSON strings
			JsonNode rootNode1 = objectMapper.readTree(jsonResponse1);
			JsonNode rootNode2 = objectMapper.readTree(jsonResponse2);
			result = ResponseComparator.compareNodes(rootNode2.get("hotelContents").get(0).get("hotelContent"),
					rootNode1.get(0));
		} catch (Exception e) {
			// TODO: handle exception
			result = "Exception occured while hotelsearch";
		}

		return result;
	}

	private static String generateURL(String version, String hotelCode, String brandCode) {
		return hostName + version + "/profiles/" + hotelCode + "/details?brandCode=" + brandCode + "&fieldset="
				+ String.join(",", fieldsets);
	}

	private String generateURL(String version) {
		String result = hostName + version + "/profiles/details?fieldset=" + String.join(",", fieldsets);
		System.out.println("POST URL:" + result);
		return result;
	}

	private HttpEntity<String> generateHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-ihg-api-key", api_key);
		headers.set("IHG-LANGUAGE", "en_US");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity<Object> generateHeadersPost(HotelDetailRequest hdr, boolean isV2) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-ihg-api-key", api_key);
		headers.set("IHG-LANGUAGE", "en_US");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> entity;
		if (isV2) {
			entity = new HttpEntity<>(hdr.getHotelCodeBrandCodeList(), headers);
		} else {
			entity = new HttpEntity<>(hdr, headers);
		}
		return entity;
	}

}
