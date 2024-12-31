package com.example.demo.service;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;

public class ResponseComparator {
	public static String result = "";

	public static String compareNodes(JsonNode node1, JsonNode node2, String version1, String version2) {
		System.out.println("Comparing nodes V3 and V1:");
		// here node1 is v3 api & node2 is v1 api
		Iterator<String> fieldNames1 = node1.fieldNames();
		while (fieldNames1.hasNext()) {
			String fieldName = fieldNames1.next();
			JsonNode value1 = node1.get(fieldName);
			JsonNode value2 = node2.get(fieldName);

			if (value2 == null) {
				System.out.println("Field " + fieldName + " is missing in " + version1 + " API.");
				result += "\nField " + fieldName + " is missing in " + version1 + "  API.\n";
			} else if (value1.isObject() && value2.isObject()) {
				System.out.println("Comparing nested object: " + fieldName);
				compareNodes(value1, value2, version1, version2);
			} else if (value1.isArray() && value2.isArray()) {
				System.out.println("Comparing array: " + fieldName);
				compareArrays(value1, value2, fieldName, version1, version2);
			} else if (!value1.equals(value2)) {
				if (!compareLocalValues(value1, value2)) {
					System.out.println("Difference in field " + fieldName + ":");
					result += "\nDifference in field " + fieldName + ":\n";
					result += "\n  API 1 (" + version2 + ") Value: " + value1 + "\n";
					result += "\n  API 2 (" + version1 + ") Value: " + value2 + "\n";
				}
			}
		}

		// Check for extra fields in node2
		Iterator<String> fieldNames2 = node2.fieldNames();
		while (fieldNames2.hasNext()) {
			String fieldName = fieldNames2.next();
			if (!node1.has(fieldName)) {
				System.out.println("Field " + fieldName + " is missing in " + version2 + " API.");
				result += "\nField " + fieldName + " is missing in " + version2 + " API.\n";
			}
		}
		return result;
	}

	private static void compareArrays(JsonNode array1, JsonNode array2, String fieldName, String version1,
			String version2) {
		System.out.println("Comparing arrays for field: " + fieldName);
		if (array1.size() != array2.size()) {

			result += "\n  Array size mismatch: API 1 (" + version2 + ") size = " + array1.size() + ", API 2 ("
					+ version1 + ") size = " + array2.size() + "\n";
		} else {
			for (int i = 0; i < array1.size(); i++) {
				JsonNode item1 = array1.get(i);
				JsonNode item2 = array2.get(i);
				if (!item1.equals(item2)) {
					if (item1.isObject() && item2.isObject()) {
						compareNodes(item1, item2, version1, version2);
					} else {
						System.out.println("  Difference at index " + i + ":");
						result += "\n  Difference at index " + i + ":\n";
						result += "\n    API 1 (" + version2 + ") Value: " + item1 + "\n";
						result += "\n    API 2 (" + version1 + ") Value: " + item2 + "\n";
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
