package nl.matsgemmeke.battlegrounds.game.audio;

import org.bukkit.Sound;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultGameSoundTest {

    @Test
    public void canParseSoundFromString() {
        String arg = "ENTITY_BLAZE_HURT-3-2-0";

        GameSound sound = DefaultGameSound.parseSound(arg);

        assertEquals(Sound.ENTITY_BLAZE_HURT, sound.getSound());
        assertEquals(3.0, sound.getVolume(), 0.0);
        assertEquals(2.0, sound.getPitch(), 0.0);
        assertEquals(0, sound.getDelay());
    }

    @Test
    public void canParseMultipleSoundsFromString() {
        String arg = "ENTITY_BLAZE_HURT-3-2-0, ENTITY_ARROW_HIT-1-1-0";

        List<GameSound> sounds = DefaultGameSound.parseSounds(arg);

        assertEquals(2, sounds.size());
        assertEquals(Sound.ENTITY_BLAZE_HURT, sounds.get(0).getSound());
        assertEquals(Sound.ENTITY_ARROW_HIT, sounds.get(1).getSound());
    }

    @Test
    public void detectsInvalidSoundName() {
        String arg = "TEST_TEST-3-2-0";

        assertThrows(IllegalArgumentException.class, () -> DefaultGameSound.parseSound(arg));
    }

    @Test
    public void detectsInvalidVolumeValue() {
        String arg = "ENTITY_BLAZE_HURT-test-2-0";

        assertThrows(IllegalArgumentException.class, () -> DefaultGameSound.parseSound(arg));
    }

    @Test
    public void detectsInvalidPitchValue() {
        String arg = "ENTITY_BLAZE_HURT-3-test-0";

        assertThrows(IllegalArgumentException.class, () -> DefaultGameSound.parseSound(arg));
    }

    @Test
    public void detectsInvalidDelayValue() {
        String arg = "ENTITY_BLAZE_HURT-3-2-test";

        assertThrows(IllegalArgumentException.class, () -> DefaultGameSound.parseSound(arg));
    }
}
