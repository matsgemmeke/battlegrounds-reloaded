package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.validation.EnumValue;
import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class PotionEffectSpec {

    @Required
    @EnumValue(type = PotionEffectType.class)
    public String type;
    @Required
    public Integer duration;
    @Required
    public Integer amplifier;
    @Required
    public Boolean ambient;
    @Required
    public Boolean particles;
    @Required
    public Boolean icon;

    private enum PotionEffectType {
        SPEED,
        SLOW,
        FAST_DIGGING,
        SLOW_DIGGING,
        INCREASE_DAMAGE,
        HEAL,
        HARM,
        JUMP,
        CONFUSION,
        REGENERATION,
        DAMAGE_RESISTANCE,
        FIRE_RESISTANCE,
        WATER_BREATHING,
        INVISIBILITY,
        BLINDNESS,
        NIGHT_VISION,
        HUNGER,
        WEAKNESS,
        POISON,
        WITHER,
        HEALTH_BOOST,
        ABSORPTION,
        SATURATION,
        GLOWING,
        LEVITATION,
        LUCK,
        UNLUCK,
        SLOW_FALLING,
        CONDUIT_POWER,
        DOLPHINS_GRACE,
        BAD_OMEN,
        HERO_OF_THE_VILLAGE,
        DARKNESS
    }
}
