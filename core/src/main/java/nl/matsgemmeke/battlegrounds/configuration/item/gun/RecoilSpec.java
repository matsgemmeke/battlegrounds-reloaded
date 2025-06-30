package nl.matsgemmeke.battlegrounds.configuration.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.validation.Required;

public class RecoilSpec {

    @Required
    public String type;
    @Required
    public Float[] horizontal;
    @Required
    public Float[] vertical;
    public Long kickbackDuration;
    public Float recoveryRate;
    public Long recoveryDuration;
}
