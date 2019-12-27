import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class RESTAssuredEmployeeJSONTests {

    private int empId ;

    @Before
    public void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4000;
        empId = 8;
    }

    public Response getEmployeeList()
    {
        Response response = RestAssured.get("/emp/list");
        return response;
    }

    @Test
    public void onCallingList_ReturnEmployeeList() {
        Response response = getEmployeeList();
        System.out.println("AT FIRST:" + response.asString());
        response.then().body("id", Matchers.hasItems(1,3,5));
        response.then().body("name",Matchers.hasItems("Pankaj"));
    }

    @Test
    public void givenEmployee_OnPost_ShouldReturnAddedEmployee() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"Raju\",\"salary\": \"2000\"}")
                .when()
                .post("/emp/create");
        String respAsStr = response.asString();
        JsonObject jsonObject = new Gson().fromJson(respAsStr, JsonObject.class);
        int id = jsonObject.get("id").getAsInt();
        response.then().body("id",Matchers.any(Integer.class));
        response.then().body("name",Matchers.is("Raju"));
    }

    @Test
    public void givenEmployee_OnUpdate_ShouldReturnUpdatedEmployee() {
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"name\": \"Raju\",\"salary\": \"20000\"}")
                .when()
                .put("/emp/update/"+empId);

        String respAsStr = response.asString();
        System.out.println(respAsStr);
        response.then().body("id",Matchers.any(Integer.class));
        response.then().body("name",Matchers.is("Raju"));
        response.then().body("salary",Matchers.is("20000"));
    }

    @Test
    public void givenEmployeeId_OnDelete_ShouldReturnSuccessStatus() {
        Response response = RestAssured.delete("/emp/delete/"+empId);
        String respAsStr = response.asString();
        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
        MatcherAssert.assertThat(statusCode, CoreMatchers.is(200));
        response = getEmployeeList();
        System.out.println("AT END: " + response.asString());
        response.then().body("id",Matchers.not(empId));
    }
}


