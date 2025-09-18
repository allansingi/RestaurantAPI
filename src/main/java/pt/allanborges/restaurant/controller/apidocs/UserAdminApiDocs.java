package pt.allanborges.restaurant.controller.apidocs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import pt.allanborges.restaurant.controller.handlers.exceptions.StandardError;
import pt.allanborges.restaurant.security.dtos.ApproveUserRequest;
import pt.allanborges.restaurant.security.dtos.UserResponse;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "User Admin Controller", description = "Admin operations for user accounts")
@SecurityRequirement(name = "bearerAuth") // Make sure you defined this security scheme in your OpenAPI config
public interface UserAdminApiDocs {

    @Operation(summary = "List users", description = "List all user accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User list returned",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)))
    })
    List<UserResponse> getUserAccountList();

    @Operation(summary = "Approve user", description = "Approve or enable a user and (optionally) set roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User approved",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StandardError.class)))
    })
    ResponseEntity<UserResponse> approve(
            @Parameter(description = "User id", required = true, example = "42") Long id,
            @Parameter(description = "Approve request body (optional). If omitted, enabled=true is assumed.")
            ApproveUserRequest req,
            @Parameter(hidden = true) java.security.Principal principal
    );
}
