package tests;

import models.lombok.UsersDataClass;
import models.lombok.SuccessRegister;
import models.lombok.UnsuccessfulRegister;
import models.lombok.VotingWithoutAuthorization;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static listeners.CustomAllureListener.withCustomTemplates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

//с кастомными listeners
public class ApiTests {
    @Test
    void successRegister() {
        /*
        request: https://reqres.in/api/register
        data:
        {
            "email": "eve.holt@reqres.in",
            "password": "pistol"
         }
        response:
       {
            "id": 4,
            "token": "QpwL5tke4Pnpja7X4"
         }
         */


        SuccessRegister successRegister = new SuccessRegister();
        successRegister.setEmail("eve.holt@reqres.in");
        successRegister.setPassword("pistol");


        given()
                .filter(withCustomTemplates())
                .log().all() // вывод в лог запроса
                .body(successRegister)
                .contentType(JSON)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().all() // вывод в лог ответа
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("shemas/successRegister_response_json_shema.json"))
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));

    }


    @Test
    void unsuccessfulRegister(){
         /*
        request: https://reqres.in/api/register
        data:
        {
             "email": "sydney@fife"
        }
        response:
        {
            "error": "Missing password"
        }
         */

        UnsuccessfulRegister unsuccessfulRegister = new UnsuccessfulRegister();
        unsuccessfulRegister.setEmail("sydney@fife");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .body(unsuccessfulRegister)
                .contentType(JSON)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body(matchesJsonSchemaInClasspath("shemas/unsuccessfulRegister_response_json_shema.json"))
                .body("error", is("Missing password"));

    }

    @Test
    void successCreate(){
          /*
        request: https://reqres.in/api/users
        data:
        {
            "name": "morpheus",
            "job": "leader"
        }
        response:
        {
             "name": "morpheus",
                "job": "leader",
        }
         */

        UsersDataClass usersDataClass = new UsersDataClass();
        usersDataClass.setName("morpheus");
        usersDataClass.setJob("leader");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .body(usersDataClass)
                .contentType(JSON)
                .when()
                .post("https://reqres.in/api/users")
                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("shemas/successCreate_response_json_shema.json"))
                .body("name", is("morpheus"))
                .body("job", is("leader"));

    }

    @Test
    void successPut(){
          /*
        request: https://reqres.in/api/users/2
        data:
       {
             "name": "morpheus",
             "job": "zion resident"
        }
        response:
        {
             "name": "morpheus",
               "job": "zion resident",
        }
         */

        UsersDataClass usersDataClass = new UsersDataClass();
        usersDataClass.setName("morpheus");
        usersDataClass.setJob("zion resident");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .body(usersDataClass)
                .contentType(JSON)
                .when()
                .put("https://reqres.in/api/users/2")
                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("shemas/successPut_response_json_shema.json"))
                .body("name", is("morpheus"))
                .body("job", is("zion resident"));

    }

    @Test
    void resourceNotFound() {

          /*
        request: https://reqres.in/api/unknown/23
        data:
       {
             "name": "morpheus",
             "job": "zion resident"
        }
        response:
        {}
         */

        UsersDataClass usersDataClass = new UsersDataClass();
        usersDataClass.setName("morpheus");
        usersDataClass.setJob("zion resident");

        given()
                .filter(withCustomTemplates())
                .log().uri()
                .log().body()
                .body(usersDataClass)
                .contentType(JSON)
                .when()
                .get("https://reqres.in/api/unknown/23")
                .then()
                .log().status()
                .log().body()
                .statusCode(404);

    }

    @Test
    void addItemWishListCardUnauthorizedUser() {
        given()
                .filter(withCustomTemplates())
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
                .body("success", CoreMatchers.is(false));

    }

    @Test
    void votingWithoutAuthorization() {

        VotingWithoutAuthorization votingWithoutAuthorization =
                given()
                        .filter(withCustomTemplates())
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
