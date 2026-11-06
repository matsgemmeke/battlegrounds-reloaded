package nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class ActionBarMessageReader implements ObjectReader<MessageSpec> {

    @Override
    public MessageSpec read(Section section) {
        String text = section.getString("text").orElse(null);

        ActionBarMessageSpec spec = new ActionBarMessageSpec();
        spec.setText(text);
        return spec;
    }
}
