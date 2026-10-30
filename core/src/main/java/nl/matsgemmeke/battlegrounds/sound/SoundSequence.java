package nl.matsgemmeke.battlegrounds.sound;

import java.util.List;

public record SoundSequence(String id, List<SoundSequenceEntry> entries) {
}
