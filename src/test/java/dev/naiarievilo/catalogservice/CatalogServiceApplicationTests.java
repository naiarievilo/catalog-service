package dev.naiarievilo.catalogservice;

import dev.naiarievilo.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPostRequest_ThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, null);

        webTestClient
            .post()
            .uri("/books")
            .bodyValue(expectedBook)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class).value(actualBook -> {
                assertThat(actualBook).isNotNull();
                assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
            });
    }

    @Test
    void whenGetRequestWithId_ThenBookReturned() {
        var bookIsbn = "1231231230";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, null);
        Book expectedBook = webTestClient
            .post()
            .uri("/books")
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
            .returnResult().getResponseBody();

        webTestClient
            .get()
            .uri("/books/" + bookIsbn)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(Book.class).value(actualBook -> {
                assertThat(actualBook).isNotNull();
                assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
            });
    }

    @Test
    void whenPutRequest_ThenBookUpdated() {
        var bookIsbn = "1231231232";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, null);
        Book createdBook = webTestClient
            .post()
            .uri("/books")
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
            .returnResult().getResponseBody();
        var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), createdBook.title(), createdBook.author(),
            7.95, createdBook.publisher(), createdBook.createdDate(), createdBook.lastModifiedDate(),
            createdBook.version());

        webTestClient
            .put()
            .uri("/books/" + bookIsbn)
            .bodyValue(bookToUpdate)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Book.class).value(actualBook -> {
                assertThat(actualBook).isNotNull();
                assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
            });
    }

    @Test
    void whenDeleteRequest_ThenBookDeleted() {
        var bookIsbn = "1231231233";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, null);
        webTestClient
            .post()
            .uri("/books")
            .bodyValue(bookToCreate)
            .exchange()
            .expectStatus().isCreated();

        webTestClient
            .delete()
            .uri("/books/" + bookIsbn)
            .exchange()
            .expectStatus().isNoContent();

        webTestClient
            .get()
            .uri("/books/" + bookIsbn)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody(String.class).value(errorMessage ->
                assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
            );
    }

}
