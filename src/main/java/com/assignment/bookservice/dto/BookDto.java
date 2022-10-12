package com.assignment.bookservice.dto;

import com.assignment.bookservice.validation.ValidateGenreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * BookDTO.java
 *
 * Book resource Data Transfer Object
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @Min(0)
    @NotNull
    private int unitsSold;

    @NotNull(message = "Genre cannot be blank")
    @ValidateGenreType
    private String genre;

    @NotNull(message = "Author Id cannot be null")
    private Long authorId;
}
