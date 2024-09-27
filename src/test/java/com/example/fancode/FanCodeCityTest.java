package com.example.fancode;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class FanCodeCityTest {
		private static final String BASE_URL = "http://jsonplaceholder.typicode.com";

		public List<Map<String, Object>> getUsers() {
		Response response = RestAssured.get(BASE_URL + "/users");
		return response.jsonPath().getList("$");
	}

	public List<Map<String, Object>> getTodosForUser(int userId) {
		Response response = RestAssured.get(BASE_URL + "/todos?userId=" + userId);
		return response.jsonPath().getList("$");
	}

		public boolean isFanCodeCityUser(Map<String, Object> user) {
		Map<String, String> geo = (Map<String, String>) ((Map<String, Object>) user.get("address")).get("geo");
		double lat = Double.parseDouble(geo.get("lat"));
		double lng = Double.parseDouble(geo.get("lng"));
		return lat >= -40 && lat <= 5 && lng >= 5 && lng <= 100;
	}

	public double calculateCompletedPercentage(List<Map<String, Object>> todos) {
		long totalTasks = todos.size();
		long completedTasks = todos.stream().filter(todo -> (Boolean) todo.get("completed")).count();
		return (totalTasks > 0) ? (double) completedTasks / totalTasks * 100 : 0;
	}

	// Test case for checking users of FanCode city
	@Test
	public void testFanCodeCityUsersCompletion() {
		List<Map<String, Object>> users = getUsers();

			users.stream().filter(this::isFanCodeCityUser).forEach(user -> {
			int userId = (Integer) user.get("id");
			String userName = (String) user.get("name");

			List<Map<String, Object>> todos = getTodosForUser(userId);

			double completedPercentage = calculateCompletedPercentage(todos);

			Assertions.assertTrue(completedPercentage > 50, userName + " has less than 50% tasks completed!");
		});
	}

}
