package com.api.tests;

import com.api.client.ApiClient;
import com.api.utils.Endpoints;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("PetStore API Testing")
@Feature("Pet Management")
public class PetTests {

    private ApiClient apiClient;
    private static final int PET_ID = 9999;
    private static final Logger logger = LoggerFactory.getLogger(PetTests.class);

    @BeforeClass
    public void setup() {
        apiClient = new ApiClient(Endpoints.BASE_URL);
    }

    /**
     * Отримання улюбленця з повторними спробами, якщо спочатку його не знайдено.
     */
    private Response getPetWithRetries(int retries, int delayMs) {
        for (int i = 0; i < retries; i++) {
            Response response = apiClient.getRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
            if (response.getStatusCode() == 200) {
                return response;
            }
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Thread was interrupted during retry", e);
                break;
            }
        }
        return apiClient.getRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
    }

    @Story("Create new pet")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Створення нового улюбленця з перевіркою JSON-схеми")
    @Test
    public void testCreatePet() {
        logger.info("Checking if pet with ID {} already exists.", PET_ID);

        Response existingPet = apiClient.getRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
        if (existingPet.getStatusCode() == 200) {
            logger.info("Pet with ID {} already exists. Deleting before test.", PET_ID);
            apiClient.deleteRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
        }

        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Dog");

        Map<String, Object> petData = new HashMap<>();
        petData.put("id", PET_ID);
        petData.put("name", "Bobby");
        petData.put("category", category);
        petData.put("photoUrls", new String[]{"https://example.com/dog.jpg"});
        petData.put("tags", new Object[]{});
        petData.put("status", "available");

        Response response = apiClient.postRequest(Endpoints.PET, petData);
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець створений");

        logger.info("Response body after creation: {}", response.getBody().asString());
    }

    @Story("Get pet by ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("Отримання улюбленця за ID та перевірка JSON-схеми")
    @Test(dependsOnMethods = "testCreatePet")
    public void testGetPet() {
        logger.info("Requesting pet with ID: {}", PET_ID);

        Response response = getPetWithRetries(3, 500);
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець знайдений");
        Assert.assertEquals(response.jsonPath().getString("name"), "Bobby", "Перевірка імені улюбленця");

        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/pet-schema.json"));
    }

    @Story("Update pet information")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Оновлення інформації про улюбленця та перевірка відповіді")
    @Test(dependsOnMethods = "testGetPet")
    public void testUpdatePet() {
        Response existingPet = getPetWithRetries(3, 500);
        Assert.assertEquals(existingPet.getStatusCode(), 200, "Улюбленець має існувати перед оновленням");

        Map<String, Object> category = new HashMap<>();
        category.put("id", 1);
        category.put("name", "Dog");

        Map<String, Object> petData = new HashMap<>();
        petData.put("id", PET_ID);
        petData.put("name", "BobbyUpdated");
        petData.put("category", category);
        petData.put("photoUrls", new String[]{"https://example.com/dog-updated.jpg"});
        petData.put("tags", new Object[]{});
        petData.put("status", "sold");

        Response response = apiClient.putRequest(Endpoints.PET, petData);
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець оновлений");
    }

    @Story("Delete pet")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Видалення улюбленця з системи")
    @Test(dependsOnMethods = "testUpdatePet")
    public void testDeletePet() {
        logger.info("Deleting pet with ID {}", PET_ID);

        Response response = apiClient.deleteRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
        logger.info("Delete response: {}", response.getBody().asString());
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець видалений");

        logger.info("Verifying pet deletion...");
        boolean isDeleted = false;
        for (int i = 0; i < 5; i++) { // Збільшуємо до 5 перевірок
            Response checkDeleted = apiClient.getRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
            logger.info("Attempt {} - Get after delete: Status {} | Response: {}", i+1, checkDeleted.getStatusCode(), checkDeleted.getBody().asString());

            if (checkDeleted.getStatusCode() == 404) {
                isDeleted = true;
                break;
            }

            try {
                Thread.sleep(1000); // Збільшуємо затримку до 1 секунди
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Assert.assertTrue(isDeleted, "Переконайтесь, що улюбленець більше не існує (очікуємо 404)");
    }

}
