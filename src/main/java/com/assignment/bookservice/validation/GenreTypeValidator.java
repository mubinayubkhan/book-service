package com.assignment.bookservice.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class GenreTypeValidator implements ConstraintValidator<ValidateGenreType, String> {

    private final List<String> genreTypes = List.of("Fantasy", "Science Fiction", "Romance", "Thriller",
            "Mystery", "Horror", "Autobiography");

    @Override
    public boolean isValid(String genreType, ConstraintValidatorContext constraintValidatorContext) {
        return genreTypes.contains(genreType);
    }
}
