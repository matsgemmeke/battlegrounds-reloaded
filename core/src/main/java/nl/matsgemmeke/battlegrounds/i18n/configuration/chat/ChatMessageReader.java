package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class ChatMessageReader implements ObjectReader<MessageSpec> {

    @Override
    public MessageSpec read(Section section) {
        String text = section.getString("text").orElse(null);

        ChatMessageSpec spec = new ChatMessageSpec();
        spec.setText(text);
        return spec;
    }
}
