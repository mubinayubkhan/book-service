package com.assignment.bookservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * AuthorDTO.java
 *
 * Author resource Data Transfer Object
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorDto {

    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
}
