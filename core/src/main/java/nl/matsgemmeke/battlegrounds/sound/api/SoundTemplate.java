package nl.matsgemmeke.battlegrounds.sound.api;

import java.util.List;

public record SoundTemplate(String id, List<SoundCue> cues) {
}
