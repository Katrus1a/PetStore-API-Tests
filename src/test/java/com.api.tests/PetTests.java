package com.api.tests;

import com.api.client.ApiClient;
import com.api.utils.Endpoints;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class PetTests {

    private ApiClient apiClient;
    private static final int PET_ID = 9999;

    @BeforeClass
    public void setup() {
        apiClient = new ApiClient(Endpoints.BASE_URL);
    }

    @Test
    @Description("Створення нового улюбленця")
    public void testCreatePet() {
        Map<String, Object> petData = new HashMap<>();
        petData.put("id", PET_ID);
        petData.put("name", "Bobby");
        petData.put("status", "available");

        Response response = apiClient.postRequest(Endpoints.PET, petData);
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець створений");
    }

    @Test(dependsOnMethods = "testCreatePet")
    @Description("Отримання улюбленця за ID")
    public void testGetPet() {
        Response response = apiClient.getRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець знайдений");
        Assert.assertEquals(response.jsonPath().getString("name"), "Bobby", "Перевірка імені улюбленця");
    }

    @Test(dependsOnMethods = "testGetPet")
    @Description("Оновлення інформації про улюбленця")
    public void testUpdatePet() {
        Map<String, Object> petData = new HashMap<>();
        petData.put("id", PET_ID);
        petData.put("name", "BobbyUpdated");
        petData.put("status", "sold");

        Response response = apiClient.putRequest(Endpoints.PET, petData);
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець оновлений");
    }

    @Test(dependsOnMethods = "testUpdatePet")
    @Description("Видалення улюбленця")
    public void testDeletePet() {
        Response response = apiClient.deleteRequest(Endpoints.PET_BY_ID.replace("{petId}", String.valueOf(PET_ID)));
        Assert.assertEquals(response.getStatusCode(), 200, "Переконайтесь, що улюбленець видалений");
    }
}
