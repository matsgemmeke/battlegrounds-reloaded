package nl.matsgemmeke.battlegrounds.item.projectile.effect.sound;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;

import java.util.List;

public record SoundProperties(List<GameSound> sounds, Long delay, List<Long> intervals) { }
