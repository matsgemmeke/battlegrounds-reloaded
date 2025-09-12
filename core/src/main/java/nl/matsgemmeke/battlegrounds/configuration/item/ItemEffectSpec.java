package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.ConditionalRequired;
import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

import java.util.HashMap;
import java.util.Map;

public class ItemEffectSpec {

    @Required
    @EnumValue(type = ItemEffectType.class)
    public String type;
    public Map<String, TriggerSpec> triggers = new HashMap<>();
    public String activationSounds;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "DAMAGE")
    @EnumValue(type = DamageType.class)
    public String damageType;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "DAMAGE", "EXPLOSION" })
    public RangeProfileSpec range;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "SMOKE_SCREEN")
    public Double minSize;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "FLASH", "SMOKE_SCREEN" })
    public Double maxSize;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "SMOKE_SCREEN")
    public Double density;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "SMOKE_SCREEN" })
    public Double growth;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "SMOKE_SCREEN" })
    public Long growthInterval;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "GUN_FIRE_SIMULATION", "SMOKE_SCREEN" })
    public Long minDuration;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "GUN_FIRE_SIMULATION", "SMOKE_SCREEN" })
    public Long maxDuration;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "EXPLOSION", "FLASH" })
    public Float power;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "EXPLOSION", "FLASH" })
    public Boolean damageBlocks;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = { "COMBUSTION", "EXPLOSION", "FLASH" })
    public Boolean spreadFire;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "SMOKE_SCREEN")
    public ParticleEffectSpec particleEffect;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "FLASH")
    public PotionEffectSpec potionEffect;
    @ConditionalRequired(conditionalFieldName = "type", matchValues = "GUN_FIRE_SIMULATION")
    public ActivationPatternSpec activationPattern;

    private enum ItemEffectType {
        COMBUSTION, DAMAGE, EXPLOSION, FLASH, GUN_FIRE_SIMULATION, MARK_SPAWN_POINT, SMOKE_SCREEN, SOUND_NOTIFICATION
    }

    private enum DamageType {
        ATTACK_DAMAGE, BULLET_DAMAGE, ENVIRONMENTAL_DAMAGE, EXPLOSIVE_DAMAGE, EXPLOSIVE_ITEM_DAMAGE, FIRE_DAMAGE
    }
}
