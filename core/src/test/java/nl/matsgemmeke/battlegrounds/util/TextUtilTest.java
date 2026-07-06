package nl.matsgemmeke.battlegrounds.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextUtilTest {

    @ParameterizedTest
    @CsvSource({ "0,books", "1,book", "2,books" })
    @DisplayName("pluralize returns the singular noun when count equals one, otherwise the plural noun")
    void pluralize_countDoesNotEqualOne(int count, String expected) {
        String noun = TextUtil.pluralize(count, "book", "books");

        assertThat(noun).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("toKebabCase throws IllegalArgumentException when given input is null or empty")
    void toKebabCase_invalid(String input) {
        assertThatThrownBy(() -> TextUtil.toKebabCase(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Input must be text with at least one character");
    }

    @ParameterizedTest
    @CsvSource({
            "HelloWorld,hello-world",
            "camelCaseString,camel-case-string",
            "some_snake_case,some-snake-case",
            "Multiple  Spaces,multiple-spaces",
            "HTTPRequestObject,http-request-object"
    })
    @DisplayName("toKebabCase returns converted result from given input")
    void toKebabCase_valid(String input, String expected) {
        String result = TextUtil.toKebabCase(input);

        assertThat(result).isEqualTo(expected);
    }
}
