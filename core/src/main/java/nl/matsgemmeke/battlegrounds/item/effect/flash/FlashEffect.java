package nl.matsgemmeke.battlegrounds.item.effect.flash;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemMechanism;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class FlashEffect implements ItemMechanism {

    @NotNull
    private FlashSettings settings;
    @NotNull
    private TargetFinder targetFinder;

    public FlashEffect(@NotNull FlashSettings settings, @NotNull TargetFinder targetFinder) {
        this.settings = settings;
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
        world.createExplosion(location, settings.explosionPower(), settings.explosionSetFire(), settings.explosionBreakBlocks(), holder.getEntity());

        for (GameEntity target : targetFinder.findTargets(holder, location, settings.range())) {
            if (!(target.getEntity() instanceof LivingEntity entity)) {
                continue;
            }

            PotionEffectType potionEffectType = PotionEffectType.BLINDNESS;
            int duration = settings.effectDuration();
            int amplifier = settings.effectAmplifier();
            boolean ambient = settings.effectAmbient();
            boolean particles = settings.effectParticles();
            boolean icon = settings.effectIcon();

            PotionEffect potionEffect = new PotionEffect(potionEffectType, duration, amplifier, ambient, particles, icon);

            entity.addPotionEffect(potionEffect);
        }
    }
}
