package dev.naiarievilo.catalogservice.web;

import dev.naiarievilo.catalogservice.domain.BookNotFoundException;
import dev.naiarievilo.catalogservice.domain.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.BDDMockito.given;

@WebMvcTest(BookController.class)
class BookControllerMvcTests {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BookService bookService;

    @Test
    void whenGetBookDoesNotExist_thenShouldReturnNotFound() {
        String isbn = "7373737373";
        given(bookService.viewBookDetails(isbn)).willThrow(BookNotFoundException.class);

        webTestClient
            .get()
            .uri("/books/" + isbn)
            .exchange()
            .expectStatus().isNotFound();
    }
}
