package nl.matsgemmeke.battlegrounds.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilTest {

    @ParameterizedTest
    @CsvSource({ "0,books", "1,book", "2,books" })
    @DisplayName("pluralize returns the singular noun when count equals one, otherwise the plural noun")
    void pluralize_countDoesNotEqualOne(int count, String expected) {
        String noun = TextUtil.pluralize(count, "book", "books");

        assertThat(noun).isEqualTo(expected);
    }
}
