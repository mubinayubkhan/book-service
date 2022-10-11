package com.assignment.bookservice.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenreTypeValidatorTest {

    private final GenreTypeValidator genreTypeValidator = new GenreTypeValidator();

    @Test
    void verifyInvalidGenreTypeReturnsFalse() {
        assertThat(genreTypeValidator.isValid("invalid type", null)).isFalse();
    }

    @Test
    void verifyValidGenreTypeReturnsTrue() {
        assertThat(genreTypeValidator.isValid("Fantasy", null)).isTrue();
    }
}