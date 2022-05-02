package tests;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class TestBase {
    static String URL = "http://demowebshop.tricentis.com/";

    @BeforeAll
    static void beforeAll() {
        Configuration.baseUrl = URL;
        Configuration.browserSize = "1920x1080";
        RestAssured.filters(new AllureRestAssured());

    }
}
