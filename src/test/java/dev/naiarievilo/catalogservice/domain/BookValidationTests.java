package dev.naiarievilo.catalogservice.domain;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookValidationTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void whenAllFieldsCorrect_ThenValidationSucceeds() {
        var book = Book.of("1234567890", "Title", "Author", 9.90, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenIsbnDefinedButIncorrect_ThenValidationFails() {
        var book = Book.of("12345678AB", "Title", "Author", 9.90, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertThat(violations.iterator().next().getMessage()).isEqualTo("The book ISBN must be valid.");
    }

    @Test
    void whenTitleIsNotDefined_ThenValidationFails() {
        var book = Book.of("1234567890", "", "Author", 9.90, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorIsNotDefined_ThenValidationFails() {
        var book = Book.of("1234567890", "Title", "", 9.90, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book author must be defined.");
    }

    @Test
    void whenPriceIsNotDefined_ThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", null, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenPriceDefinedButZero_ThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", 0.0, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPriceDefinedButNegative_ThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", -9.90, null);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("The book price must be greater than zero.");
    }}
