package dev.naiarievilo.catalogservice.web;

import dev.naiarievilo.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookJsonTests {

    @Autowired
    private JacksonTester<Book> json;

    private final Book book = Book.of("1234567890", "Title", "Author", 9.90, null);

    @Test
    void testSerialize() throws Exception {
        JsonContent<Book> jsonContent = json.write(book);

        assertThat(jsonContent).extractingJsonPathValue("@.id").isEqualTo(book.id());
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn").isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title").isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author").isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price").isEqualTo(book.price());
        assertThat(jsonContent).extractingJsonPathValue("@.createdDate").isEqualTo(book.createdDate());
        assertThat(jsonContent).extractingJsonPathValue("@.lastModifiedDate").isEqualTo(book.lastModifiedDate());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version").isEqualTo(book.version());
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"isbn\":\"1234567890\",\"title\":\"Title\",\"author\":\"Author\", \"price\":9.90}";
        assertThat(json.parseObject(content)).isEqualTo(book);
    }
}
