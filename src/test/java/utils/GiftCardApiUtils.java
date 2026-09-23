package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GiftCardApiUtils {
    private static final String BASE_URL = "https://cihuy.triv.id/api/v1/gift/template/";

    /**
     * Ambil 1 slug event acak
     */
    public static String getRandomEventSlug() {
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .get(BASE_URL + "events");

        List<Map<String, Object>> eventsList = response.jsonPath().getList("$");
        Random random = new Random();

        // Ambil 1 map event secara acak dari list
        Map<String, Object> randomEvent = eventsList.get(random.nextInt(eventsList.size()));
        return randomEvent.get("event").toString(); // Mengembalikan misal: "congratulations"
    }

    /**
     * Ambil detail array template berdasarkan event yang terpilih
     */
    public static Map<String, Object> getRandomTemplateByEvent() {
        String randomSlug = getRandomEventSlug();
        System.out.println("[API] Random Event Selected: " + randomSlug);

        // Hit endpoint event spesifik
        String apiUrl = BASE_URL + "events?event=" + randomSlug;
        Response response = RestAssured.given()
                .header("Accept", "application/json")
                .get(apiUrl);

        List<Map<String, Object>> resultList = response.jsonPath().getList("$");

        // Match & filter item yang event-nya sesuai dengan randomSlug
        Map<String, Object> matchedEvent = null;
        for (Map<String, Object> item : resultList) {
            if (randomSlug.equalsIgnoreCase(item.get("event").toString())) {
                matchedEvent = item;
                break;
            }
        }

        if (matchedEvent == null && !resultList.isEmpty()) {
            matchedEvent = resultList.get(0);
        }

        // Ambil list "templates" dari event tersebut
        List<Map<String, Object>> templates = (List<Map<String, Object>>) matchedEvent.get("templates");

        // Jika templates kosong/null, buat fallback template default
        Map<String, Object> selectedTemplate = new HashMap<>();
        selectedTemplate.put("event", randomSlug);

        if (templates != null && !templates.isEmpty()) {
            Random random = new Random();
            Map<String, Object> randomTemplate = templates.get(random.nextInt(templates.size()));

            // Ambil id template (misal: "template01") atau key "id"
            String templateId = randomTemplate.get("id") != null ? randomTemplate.get("id").toString() : "template01";
            selectedTemplate.put("template", templateId);
            selectedTemplate.put("url", randomTemplate.get("url"));
        } else {
            selectedTemplate.put("template", "template01");
        }

        return selectedTemplate; // Mengembalikan Map misal: {event=congratulations, template=template01}
    }
}
