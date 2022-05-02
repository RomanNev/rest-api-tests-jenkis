package tests;

import models.lombok.VotingWithoutAuthorization;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

//с allure listeners
public class DemoshopApiTest extends TestBase {

    @Test
    void loginOldUserTest() {
        String authorizationCookie =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .formParam("Email", "golubtestuser@test.com")
                        .formParam("Password", "golubtestuser1")
                        .when()
                        .post(URL + "login")
                        .then()
                        .statusCode(302)
                        .extract()
                        .cookie("NOPCOMMERCE.AUTH");

        open("/wishlist"); // открыть страницу для прокидывания куки
        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authorizationCookie));
        open("");// открыть главную страницу для поиска залогиненого пользователя
        $(".account").shouldHave(text("golubtestuser@test.com"));

    }

    @Test
    void loginOutUserTest() {
        String authorizationCookie =
                given()
                        .log().uri()
                        .log().body()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .formParam("Email", "golubtestuser@test.com")
                        .formParam("Password", "golubtestuser1")
                        .when()
                        .post(URL + "login")
                        .then()
                        .log().status()
                        .log().body()
                        .statusCode(302)
                        .extract()
                        .cookie("NOPCOMMERCE.AUTH");

        open(""); // открыть страницу для прокидывания куки
        getWebDriver().manage().addCookie(new Cookie("NOPCOMMERCE.AUTH", authorizationCookie));
        open("");// открыть главную страницу

        $(".ico-logout").click();
        $(".header-links-wrapper").shouldNotHave(text("golubtestuser@test.com"));

    }

    @Test
    void addItemWishListCardUnauthorizedUser() {
        given()
                .log().uri()
                .log().body()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("giftcard_2.RecipientName=&" +
                        "giftcard_2.RecipientEmail=&" +
                        "giftcard_2.SenderName=&" +
                        "giftcard_2.SenderEmail=&" +
                        "giftcard_2.Message=&" +
                        "addtocart_2.EnteredQuantity=1")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/2/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("success", is(false));

    }

    @Test
    void votingWithoutAuthorization() {

        VotingWithoutAuthorization votingWithoutAuthorization =
        given()
                .log().uri()
                .log().body()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("pollAnswerId=2")
                .when()
                .post("http://demowebshop.tricentis.com/poll/vote")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                //.body("error", is("Only registered users can vote."))
                .extract().as(VotingWithoutAuthorization.class);

        assertThat(votingWithoutAuthorization.getError()).isEqualTo("Only registered users can vote.");

    }
}
