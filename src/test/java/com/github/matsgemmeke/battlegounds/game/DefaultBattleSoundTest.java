package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.game.DefaultBattleSound;
import org.bukkit.Sound;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class DefaultBattleSoundTest {

    @Test
    public void canParseSoundFromString() {
        String arg = "ENTITY_BLAZE_HURT-3-2-0";

        BattleSound sound = DefaultBattleSound.parseSound(arg);

        assertEquals(Sound.ENTITY_BLAZE_HURT, sound.getSound());
        assertEquals(3.0, sound.getVolume(), 0.0);
        assertEquals(2.0, sound.getPitch(), 0.0);
        assertEquals(0, sound.getDelay());
    }

    @Test
    public void canParseMultipleSoundsFromString() {
        String arg = "ENTITY_BLAZE_HURT-3-2-0, ENTITY_ARROW_HIT-1-1-0";

        List<BattleSound> sounds = DefaultBattleSound.parseSounds(arg);

        assertEquals(2, sounds.size());
        assertEquals(Sound.ENTITY_BLAZE_HURT, sounds.get(0).getSound());
        assertEquals(Sound.ENTITY_ARROW_HIT, sounds.get(1).getSound());
    }

    @Test(expected = IllegalArgumentException.class)
    public void detectsInvalidSoundName() {
        String arg = "TEST_TEST-3-2-0";

        DefaultBattleSound.parseSound(arg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void detectsInvalidVolumeValue() {
        String arg = "ENTITY_BLAZE_HURT-test-2-0";

        DefaultBattleSound.parseSound(arg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void detectsInvalidPitchValue() {
        String arg = "ENTITY_BLAZE_HURT-3-test-0";

        DefaultBattleSound.parseSound(arg);
    }

    @Test(expected = IllegalArgumentException.class)
    public void detectsInvalidDelayValue() {
        String arg = "ENTITY_BLAZE_HURT-3-2-test";

        DefaultBattleSound.parseSound(arg);
    }
}
