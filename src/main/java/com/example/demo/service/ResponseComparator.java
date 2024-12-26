package com.example.demo.service;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseComparator {
	public static String result = "";

	public static String compareNodes(JsonNode node1, JsonNode node2) {
		System.out.println("Comparing nodes V3 and V1:");
		// here node1 is v3 api & node2 is v1 api
		Iterator<String> fieldNames1 = node1.fieldNames();
		while (fieldNames1.hasNext()) {
			String fieldName = fieldNames1.next();
			JsonNode value1 = node1.get(fieldName);
			JsonNode value2 = node2.get(fieldName);

			if (value2 == null) {
				System.out.println("Field " + fieldName + " is missing in V1 API.");
				result += "\nField " + fieldName + " is missing in V1 API.\n";
			} else if (value1.isObject() && value2.isObject()) {
				System.out.println("Comparing nested object: " + fieldName);
				result += "\nComparing nested object: " + fieldName + "\n";
				compareNodes(value1, value2);
			} else if (value1.isArray() && value2.isArray()) {
				System.out.println("Comparing array: " + fieldName);
				result += "\nComparing array: " + fieldName + "\n";
				compareArrays(value1, value2, fieldName);
			} else if (!value1.equals(value2)) {
				System.out.println("Difference in field " + fieldName + ":");
				result += "\nDifference in field " + fieldName + ":\n";
				System.out.println("  API 1 (V3) Value: " + value1);
				result += "\n  API 1 (V3) Value: \" + " + value1 + "\n";
				System.out.println("  API 2 (V1) Value: " + value2);
				result += "\n  API 2 (V1) Value: " + value2 + "\n";
			}
		}

		// Check for extra fields in node2
		Iterator<String> fieldNames2 = node2.fieldNames();
		while (fieldNames2.hasNext()) {
			String fieldName = fieldNames2.next();
			if (!node1.has(fieldName)) {
				System.out.println("Field " + fieldName + " is missing in V3 API.");
				result += "\nField " + fieldName + " is missing in V3 API.\n";
			}
		}
		return result;
	}

	private static void compareArrays(JsonNode array1, JsonNode array2, String fieldName) {
		System.out.println("Comparing arrays for field: " + fieldName);

		if (array1.size() != array2.size()) {
			System.out.println("  Array size mismatch: API 1 (v3) size = " + array1.size() + ", API 2 (v1) size = "
					+ array2.size());
		} else {
			for (int i = 0; i < array1.size(); i++) {
				JsonNode item1 = array1.get(i);
				JsonNode item2 = array2.get(i);
				if (!item1.equals(item2)) {
					System.out.println("  Difference at index " + i + ":");
					System.out.println("    API 1 (v3) Value: " + item1);
					System.out.println("    API 2 (v1) Value: " + item2);
				}
			}
		}
	}
	/*
	 * public static void parseResponse(String jsonResponse, ObjectMapper
	 * objectMapper) { try { // Parse the JSON string JsonNode rootNode =
	 * objectMapper.readTree(jsonResponse); // Handle "hotelContent" or "hotelInfo"
	 * nodes dynamically if (rootNode.has("hotelContent")) { JsonNode
	 * hotelContentArray = rootNode.get("hotelContent"); if
	 * (hotelContentArray.isArray()) { for (JsonNode hotel : hotelContentArray) {
	 * parseHotelObject(hotel); } } else {
	 * System.out.println("hotelContent is not an array."); } } else if
	 * (rootNode.has("hotelInfo")) { JsonNode hotelInfoNode =
	 * rootNode.get("hotelInfo"); parseHotelObject(hotelInfoNode); } else {
	 * System.out.println("No recognizable hotel node found."); } } catch (Exception
	 * e) { e.printStackTrace(); } }
	 * 
	 * private static void parseHotelObject(JsonNode hotelNode) {
	 * System.out.println("Hotel Object:"); Iterator<String> fieldNames =
	 * hotelNode.fieldNames();
	 * 
	 * while (fieldNames.hasNext()) { String fieldName = fieldNames.next(); JsonNode
	 * fieldValue = hotelNode.get(fieldName);
	 * 
	 * if (fieldValue.isObject()) { System.out.println("  " + fieldName +
	 * " (object):"); Iterator<String> subFieldNames = fieldValue.fieldNames();
	 * while (subFieldNames.hasNext()) { String subFieldName = subFieldNames.next();
	 * JsonNode subFieldValue = fieldValue.get(subFieldName); if
	 * (subFieldValue.isArray()) { System.out.println("    " + subFieldName +
	 * " (array):"); for (JsonNode arrayItem : subFieldValue) {
	 * System.out.println("      - " + arrayItem.asText()); } } else {
	 * System.out.println("    " + subFieldName + ": " + subFieldValue); } } } else
	 * if (fieldValue.isArray()) { System.out.println("  " + fieldName +
	 * " (array):"); for (JsonNode arrayItem : fieldValue) {
	 * System.out.println("    - " + arrayItem.asText()); } } else {
	 * System.out.println("  " + fieldName + ": " + fieldValue); } } }
	 */

}
