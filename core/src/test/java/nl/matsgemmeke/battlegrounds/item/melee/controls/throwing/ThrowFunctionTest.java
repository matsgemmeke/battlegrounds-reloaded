package nl.matsgemmeke.battlegrounds.item.melee.controls.throwing;

import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ThrowFunctionTest {

    @Mock
    private MeleeWeapon meleeWeapon;
    @InjectMocks
    private ThrowFunction function;

    @Test
    void isAvailableAlwaysReturnsTrue() {
        boolean available = function.isAvailable();

        assertThat(available).isTrue();
    }

    @Test
    void isBlockingAlwaysReturnsTrue() {
        boolean blocking = function.isBlocking();

        assertThat(blocking).isTrue();
    }

    @Test
    void isPerformingAlwaysReturnsFalse() {
        boolean performing = function.isPerforming();

        assertThat(performing).isFalse();
    }

    @Test
    void cancelAlwaysReturnsFalse() {
        boolean cancelled = function.cancel();

        assertThat(cancelled).isFalse();
    }

    @Test
    void performDoesSomething() {
        MeleeWeaponHolder holder = mock(MeleeWeaponHolder.class);

        assertThatCode(() -> function.perform(holder)).doesNotThrowAnyException();
    }
}
