package com.testing.springpractice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for advisor information")
public class AdvisorDTO {
    @Schema(description = "The unique identifier of the advisor", example = "123")
    private Long advisorId;
    @Schema(description = "The name of the advisor", example = "John Doe")
    private String name;
    @Schema(description = "The age of the advisor", example = "30")
    private Integer age;
    @Schema(description = "Email of the advisor", example = "email@email.com")
    private String email;
    @Schema(description = "Password of the advisor", example = "123456Password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
