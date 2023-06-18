package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultBattleSound;
import com.github.matsgemmeke.battlegrounds.item.DefaultGun;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GunFactory implements WeaponFactory<Gun> {

    @NotNull
    private BattlegroundsConfig config;
    @NotNull
    private BattleItemConfiguration itemConfiguration;
    @NotNull
    private FiringModeFactory firingModeFactory;

    public GunFactory(
            @NotNull BattlegroundsConfig config,
            @NotNull BattleItemConfiguration itemConfiguration,
            @NotNull FiringModeFactory firingModeFactory
    ) {
        this.config = config;
        this.itemConfiguration = itemConfiguration;
        this.firingModeFactory = firingModeFactory;
    }

    @NotNull
    public Gun make(@NotNull BattleContext context, @NotNull String id) {
        Section section = itemConfiguration.getSection(id);

        if (section == null) {
            throw new InvalidBattleItemFormatException("Weapon id " + id + " is not a valid section");
        }

        // Mandatory attributes
        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setDescription(description);

        // ItemStack creation
        Material material = Material.getMaterial(section.getString("item.material"));
        short durability = section.getShort("item.durability");

        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability(durability);

        gun.setItemStack(itemStack);

        // Other variables
        double accuracy = section.getDouble("accuracy");
        gun.setAccuracy(accuracy);

        double damageAmplifier = config.getFirearmDamageAmplifier();
        gun.setDamageAmplifier(damageAmplifier);

        double headshotDamageMultiplier = section.getDouble("headshot-damage-multiplier");
        gun.setHeadshotDamageMultiplier(headshotDamageMultiplier);

        double recoilAmplifier = config.getFirearmRecoilAmplifier();
        gun.setRecoilAmplifier(recoilAmplifier);

        int magazineAmmo = section.getInt("ammo.magazine");
        gun.setMagazineAmmo(magazineAmmo);

        double shortDamage = section.getDouble("range.short-range.damage");
        gun.setShortDamage(shortDamage);

        double shortRange = section.getDouble("range.short-range.distance");
        gun.setShortRange(shortRange);

        double mediumDamage = section.getDouble("range.medium-range.damage");
        gun.setMediumDamage(mediumDamage);

        double mediumRange = section.getDouble("range.medium-range.distance");
        gun.setMediumRange(mediumRange);

        double longDamage = section.getDouble("range.long-range.damage");
        gun.setLongDamage(longDamage);

        double longRange = section.getDouble("range.long-range.distance");
        gun.setLongRange(longRange);

        FiringMode firingMode = firingModeFactory.make(gun, section.getSection("firing-mode"));
        gun.setFiringMode(firingMode);

        List<BattleSound> shotSounds = DefaultBattleSound.parseSounds(section.getString("sound.shot-sound"));
        gun.setShotSounds(shotSounds);

        List<BattleSound> triggerSounds = DefaultBattleSound.parseSounds(config.getFirearmTriggerSound());
        gun.setTriggerSounds(triggerSounds);

        return gun;
    }
}
