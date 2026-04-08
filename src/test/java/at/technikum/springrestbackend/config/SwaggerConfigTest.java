//#######################################################################
//#######################################################################
//#######################################################################
// imports & packages
package at.technikum.springrestbackend.config;


import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
//#######################################################################
//#######################################################################
//#######################################################################
// class
public class SwaggerConfigTest {

    @Test
    void openAPI_returnsConfiguredOpenAPI() {
        SwaggerConfig config = new SwaggerConfig();
        OpenAPI openAPI = config.openAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getComponents());
        assertTrue(openAPI.getComponents()
                .getSecuritySchemes()
                .containsKey("Bearer Token"));
    }

}
