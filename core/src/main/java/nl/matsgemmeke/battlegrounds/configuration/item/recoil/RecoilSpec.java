package nl.matsgemmeke.battlegrounds.configuration.item.recoil;

import nl.matsgemmeke.battlegrounds.validation.constraint.EnumValue;
import nl.matsgemmeke.battlegrounds.validation.constraint.Required;

public class RecoilSpec {

    @Required
    @EnumValue(type = RecoilType.class)
    public String type;

    @Required
    public Float[] horizontal;

    @Required
    public Float[] vertical;

    public Long kickbackDuration;

    public Float recoveryRate;

    public Long recoveryDuration;
}
