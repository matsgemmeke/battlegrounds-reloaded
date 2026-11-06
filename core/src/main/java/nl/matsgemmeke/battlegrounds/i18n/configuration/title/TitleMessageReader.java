package nl.matsgemmeke.battlegrounds.i18n.configuration.title;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class TitleMessageReader implements ObjectReader<MessageSpec> {

    @Override
    public MessageSpec read(Section section) {
        String titleText = section.getString("title-text").orElse(null);
        String subtitleText = section.getString("subtitle-text").orElse(null);
        Integer fadeInTicks = section.getInt("fade-in-ticks").orElse(0);
        Integer stayTicks = section.getInt("stay-ticks").orElse(0);
        Integer fadeOutTicks = section.getInt("fade-out-ticks").orElse(0);

        TitleMessageSpec spec = new TitleMessageSpec();
        spec.setTitleText(titleText);
        spec.setSubtitleText(subtitleText);
        spec.setFadeInTicks(fadeInTicks);
        spec.setStayTicks(stayTicks);
        spec.setFadeOutTicks(fadeOutTicks);
        return spec;
    }
}
