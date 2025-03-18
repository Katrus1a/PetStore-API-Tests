package com.api.utils;

public class Endpoints {
    public static final String BASE_URL = "https://petstore3.swagger.io/api/v3"; // Оновлена версія API
    public static final String PET = BASE_URL + "/pet"; // Основний ендпоінт для Pet
    public static final String PET_BY_ID = BASE_URL + "/pet/{petId}"; // Отримати/видалити конкретного Pet
}
