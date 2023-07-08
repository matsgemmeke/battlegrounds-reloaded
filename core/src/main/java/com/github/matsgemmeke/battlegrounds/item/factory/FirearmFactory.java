package com.github.matsgemmeke.battlegrounds.item.factory;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.game.DefaultBattleSound;
import com.github.matsgemmeke.battlegrounds.item.DefaultFirearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.FireMode;
import com.github.matsgemmeke.battlegrounds.item.mechanics.ReloadSystem;
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
    private FireModeFactory fireModeFactory;
    @NotNull
    private ReloadSystemFactory reloadSystemFactory;

    public FirearmFactory(
            @NotNull BattlegroundsConfig config,
            @NotNull BattleItemConfiguration itemConfiguration,
            @NotNull FireModeFactory fireModeFactory,
            @NotNull ReloadSystemFactory reloadSystemFactory
    ) {
        this.config = config;
        this.itemConfiguration = itemConfiguration;
        this.fireModeFactory = fireModeFactory;
        this.reloadSystemFactory = reloadSystemFactory;
    }

    @NotNull
    public Firearm make(@NotNull BattleContext context, @NotNull String id) {
        Section section = itemConfiguration.getSection(id);

        if (section == null) {
            throw new InvalidBattleItemFormatException("Weapon id " + id + " is not a valid section");
        }

        // Descriptive attributes
        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setDescription(description);
        firearm.setName(name);

        // Other variables
        double accuracy = section.getDouble("shooting.accuracy");
        firearm.setAccuracy(accuracy);

        double damageAmplifier = config.getFirearmDamageAmplifier();
        firearm.setDamageAmplifier(damageAmplifier);

        double headshotDamageMultiplier = section.getDouble("shooting.headshot-damage-multiplier");
        firearm.setHeadshotDamageMultiplier(headshotDamageMultiplier);

        double recoilAmplifier = config.getFirearmRecoilAmplifier();
        firearm.setRecoilAmplifier(recoilAmplifier);

        int magazineSize = section.getInt("ammo.magazine");
        int reserveAmmo = section.getInt("ammo.default-supply") * magazineSize;

        firearm.setMagazineAmmo(magazineSize);
        firearm.setMagazineSize(magazineSize);
        firearm.setReserveAmmo(reserveAmmo);

        double shortDamage = section.getDouble("shooting.range.short-range.damage");
        firearm.setShortDamage(shortDamage);

        double shortRange = section.getDouble("shooting.range.short-range.distance");
        firearm.setShortRange(shortRange);

        double mediumDamage = section.getDouble("shooting.range.medium-range.damage");
        firearm.setMediumDamage(mediumDamage);

        double mediumRange = section.getDouble("shooting.range.medium-range.distance");
        firearm.setMediumRange(mediumRange);

        double longDamage = section.getDouble("shooting.range.long-range.damage");
        firearm.setLongDamage(longDamage);

        double longRange = section.getDouble("shooting.range.long-range.distance");
        firearm.setLongRange(longRange);

        FireMode fireMode = fireModeFactory.make(firearm, section.getSection("shooting.fire-mode"));
        firearm.setFireMode(fireMode);

        ReloadSystem reloadSystem = reloadSystemFactory.make(firearm, section.getSection("reloading"));
        firearm.setReloadSystem(reloadSystem);

        List<BattleSound> shotSounds = DefaultBattleSound.parseSounds(section.getString("shooting.shot-sound"));
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
