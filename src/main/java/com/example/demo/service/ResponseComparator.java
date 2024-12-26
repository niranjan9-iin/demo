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
				compareNodes(value1, value2);
			} else if (value1.isArray() && value2.isArray()) {
				System.out.println("Comparing array: " + fieldName);
				compareArrays(value1, value2, fieldName);
			} else if (!value1.equals(value2)) {
				if (!compareLocalValues(value1, value2)) {
					System.out.println("Difference in field " + fieldName + ":");
					result += "\nDifference in field " + fieldName + ":\n";
					System.out.println("  API 1 (V3) Value: " + value1);
					result += "\n  API 1 (V3) Value: " + value1 + "\n";
					System.out.println("  API 2 (V1) Value: " + value2);
					result += "\n  API 2 (V1) Value: " + value2 + "\n";
				}
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
			result += "\n  Array size mismatch: API 1 (v3) size = " + array1.size() + ", API 2 (v1) size = "
					+ array2.size() + "\n";
		} else {
			for (int i = 0; i < array1.size(); i++) {
				JsonNode item1 = array1.get(i);
				JsonNode item2 = array2.get(i);
				if (!item1.equals(item2)) {
					if (item1.isObject() && item2.isObject()) {
						compareNodes(item1, item2);
					} else {
						System.out.println("  Difference at index " + i + ":");
						result += "\n  Difference at index " + i + ":\n";
						System.out.println("    API 1 (v3) Value: " + item1);
						result += "\n    API 1 (v3) Value: " + item1 + "\n";
						System.out.println("    API 2 (v1) Value: " + item2);
						result += "\n    API 2 (v1) Value: " + item2 + "\n";
					}
				}
			}
		}
	}

	// This method will compare the en_US locale with Value from v1 and v3
	private static boolean compareLocalValues(JsonNode value1, JsonNode value2) {
		return value1.isArray() && value1.get(0).get("locale").asText().equals("en_US")
				&& value1.get(0).get("value").equals(value2);
	}

}
