package nl.matsgemmeke.battlegrounds.sound.adapter;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

import java.util.Optional;

public class SoundRegistryAdapter {

    public boolean exists(String sound) {
        return this.getSound(sound).isPresent();
    }

    public Optional<Sound> getSound(String sound) {
        return Optional.ofNullable(NamespacedKey.fromString(sound)).map(Registry.SOUNDS::get);
    }
}
