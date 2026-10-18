package nl.matsgemmeke.battlegrounds.sound.api;

import java.util.List;

public record SoundSequence(String id, List<SoundSequenceEntry> entries) {
}
