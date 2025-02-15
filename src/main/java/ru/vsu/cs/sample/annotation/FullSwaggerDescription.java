package ru.vsu.cs.sample.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PutMapping;

@Operation(summary = "set user password")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(
                                description = "default response form server",
                                ref = "#/components/schemas/RegisterRsComplexRs"
                        ))}),
        @ApiResponse(responseCode = "400", description = "Bad Request",
                content = {@Content(mediaType = "application/json",
                        schema = @Schema(
                                description = "error response",
                                implementation = ErrorResponse.class
                        ))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
})
public @interface FullSwaggerDescription {
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String myCustomAnnotationSummary();
}
