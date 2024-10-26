package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.PotionEffectSettings;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FlashEffect implements ItemEffect {

    @NotNull
    private FlashSettings flashSettings;
    @NotNull
    private PotionEffectSettings potionEffectSettings;
    @NotNull
    private TargetFinder targetFinder;

    public FlashEffect(@NotNull FlashSettings flashSettings, @NotNull PotionEffectSettings potionEffectSettings, @NotNull TargetFinder targetFinder) {
        this.flashSettings = flashSettings;
        this.potionEffectSettings = potionEffectSettings;
        this.targetFinder = targetFinder;
    }

    public void activate(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        holder.removeItem(itemStack);

        this.activate(holder, holder.getLocation(), holder.getWorld());
    }

    public void activate(@NotNull ItemHolder holder, @NotNull Deployable object) {
        object.remove();

        this.activate(holder, object.getLocation(), object.getWorld());
    }

    private void activate(@NotNull ItemHolder holder, @NotNull Location location, @NotNull World world) {
        float power = flashSettings.explosionPower();
        boolean setFire = flashSettings.explosionSetFire();
        boolean breakBlocks = flashSettings.explosionBreakBlocks();

        world.createExplosion(location, power, setFire, breakBlocks, holder.getEntity());

        for (GameEntity target : targetFinder.findTargets(holder, location, flashSettings.range())) {
            if (!(target.getEntity() instanceof LivingEntity entity)) {
                continue;
            }

            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = potionEffectSettings.duration();
            int amplifier = potionEffectSettings.amplifier();
            boolean ambient = potionEffectSettings.ambient();
            boolean particles = potionEffectSettings.particles();
            boolean icon = potionEffectSettings.icon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            entity.addPotionEffect(potionEffect);
        }
    }
}
