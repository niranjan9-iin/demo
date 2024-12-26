package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.HotelDetailsResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihg.cro.commons.domainObjects.hcm.rest.response.HotelContentWrapper;
import com.ihg.cro.commons.domainObjects.hcm.rest.response.HotelContentsResponseObject;

@Service
public class HotelDetailsService {
	@Autowired
	private RestTemplate restTemplate;

	public String getHotelDetails(String hotelCode, String brandCode) {
		// Generate URLs for v1 and v3
		String v1 = generateURL("v1", hotelCode, brandCode);
		String v3 = generateURL("v3", hotelCode, brandCode);

		// Initialize responses
		Object responseV1 = null;
		Object responseV3 = null;
		String result = "";
		try {
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

	private String generateURL(String version, String hotelCode, String brandCode) {
		return "https://staging-api.ihg.com/hotels/" + version + "/profiles/" + hotelCode + "/details?brandCode="
				+ brandCode + "&fieldset=profile";
	}

	private HttpEntity<String> generateHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("x-ihg-api-key", "EMMyBVyWDU56GpigMGWDlDDf7zucpYjd");
		headers.set("IHG-LANGUAGE", "en_US");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}

}
