package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultBattleSound;
import com.github.matsgemmeke.battlegrounds.item.DefaultFirearm;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FirearmFactory implements WeaponFactory<Firearm> {

    @NotNull
    private BattlegroundsConfig config;
    @NotNull
    private BattleItemConfiguration itemConfiguration;
    @NotNull
    private FiringModeFactory firingModeFactory;

    public FirearmFactory(
            @NotNull BattlegroundsConfig config,
            @NotNull BattleItemConfiguration itemConfiguration,
            @NotNull FiringModeFactory firingModeFactory
    ) {
        this.config = config;
        this.itemConfiguration = itemConfiguration;
        this.firingModeFactory = firingModeFactory;
    }

    @NotNull
    public Firearm make(@NotNull BattleContext context, @NotNull String id) {
        Section section = itemConfiguration.getSection(id);

        if (section == null) {
            throw new InvalidBattleItemFormatException("Weapon id " + id + " is not a valid section");
        }

        // Mandatory attributes
        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultFirearm firearm = new DefaultFirearm(id, name, context);
        firearm.setDescription(description);

        // Other variables
        double accuracy = section.getDouble("accuracy");
        firearm.setAccuracy(accuracy);

        double damageAmplifier = config.getFirearmDamageAmplifier();
        firearm.setDamageAmplifier(damageAmplifier);

        double headshotDamageMultiplier = section.getDouble("headshot-damage-multiplier");
        firearm.setHeadshotDamageMultiplier(headshotDamageMultiplier);

        double recoilAmplifier = config.getFirearmRecoilAmplifier();
        firearm.setRecoilAmplifier(recoilAmplifier);

        int magazineAmmo = section.getInt("ammo.magazine");
        int reserveAmmo = section.getInt("ammo.supply") * magazineAmmo;

        firearm.setMagazineAmmo(magazineAmmo);
        firearm.setReserveAmmo(reserveAmmo);

        double shortDamage = section.getDouble("range.short-range.damage");
        firearm.setShortDamage(shortDamage);

        double shortRange = section.getDouble("range.short-range.distance");
        firearm.setShortRange(shortRange);

        double mediumDamage = section.getDouble("range.medium-range.damage");
        firearm.setMediumDamage(mediumDamage);

        double mediumRange = section.getDouble("range.medium-range.distance");
        firearm.setMediumRange(mediumRange);

        double longDamage = section.getDouble("range.long-range.damage");
        firearm.setLongDamage(longDamage);

        double longRange = section.getDouble("range.long-range.distance");
        firearm.setLongRange(longRange);

        FiringMode firingMode = firingModeFactory.make(firearm, section.getSection("firing-mode"));
        firearm.setFiringMode(firingMode);

        List<BattleSound> shotSounds = DefaultBattleSound.parseSounds(section.getString("sound.shot-sound"));
        firearm.setShotSounds(shotSounds);

        List<BattleSound> triggerSounds = DefaultBattleSound.parseSounds(config.getFirearmTriggerSound());
        firearm.setTriggerSounds(triggerSounds);

        // ItemStack creation
        Material material = Material.getMaterial(section.getString("item.material"));
        short durability = section.getShort("item.durability");

        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability(durability);

        // Set and update the item stack
        firearm.setItemStack(itemStack);
        firearm.update();

        return firearm;
    }
}
