package nl.matsgemmeke.battlegrounds.item.reload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceContainerTest {

    private ResourceContainer resourceContainer;

    @BeforeEach
    void setUp() {
        resourceContainer = new ResourceContainer(1, 1, 2, 5);
    }

    @ParameterizedTest
    @CsvSource({ "1,2,false", "2,2,true", "3,2,true" })
    @DisplayName("isReserveFull returns whether reserve amount equals or exceeds max reserve amount")
    void isReserveFull(int reserveAmount, int maxReserveAmount, boolean expectedResult) {
        resourceContainer.setReserveAmount(reserveAmount);
        resourceContainer.setMaxReserveAmount(maxReserveAmount);
        boolean reserveFull = resourceContainer.isReserveFull();

        assertThat(reserveFull).isEqualTo(expectedResult);
    }
}
