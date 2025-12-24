package com.schoolagenda.application.web.controller.doc;

//import com.schoolagenda.application.web.dto.request.AuthenticateRequest;
//import com.schoolagenda.application.web.dto.request.RefreshTokenRequest;
//import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
//import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
//import com.schoolagenda.domain.exception.StandardError;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//
//import jakarta.validation.Valid;
//
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

//@RequestMapping("/api/auth")
public interface AuthenticationControllerDoc {

//    @Operation(summary = "Authenticate user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "User authenticated", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthenticationResponse.class))),
//            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "401", description = "Bad credentials", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "404", description = "Username not found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
//    })
//    @PostMapping("/login")
//    ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticateRequest request) throws Exception;
//
//    // Endpoint (contrato) para o "Refresh Token"
//    @Operation(summary = "Refresh Token")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Token refreshed", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RefreshTokenResponse.class))),
//            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "404", description = "Username not found", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class))),
//            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = StandardError.class)))
//    })
//    @RequestMapping("/refresh-token")
//    ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody final RefreshTokenRequest request);


}
