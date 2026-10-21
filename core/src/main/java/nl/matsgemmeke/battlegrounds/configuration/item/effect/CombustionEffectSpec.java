package nl.matsgemmeke.battlegrounds.configuration.item.effect;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.configuration.item.RangeProfileSpec;

public class CombustionEffectSpec extends ItemEffectSpec {

    @NotNull
    public Double growth;

    public String activationSoundSequence;

    @NotNull
    @Valid
    public RangeProfileSpec range;

    @NotNull
    public Double minSize;

    @NotNull
    public Double maxSize;

    @NotNull
    public Long growthInterval;

    @NotNull
    public Long minDuration;

    @NotNull
    public Long maxDuration;

    @NotNull
    public Boolean damageBlocks;

    @NotNull
    public Boolean spreadFire;
}
