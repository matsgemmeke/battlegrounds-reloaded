package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.polymorphic.ObjectReader;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageReader implements ObjectReader<MessageSpec> {

    @Override
    public MessageSpec read(Section section) {
        List<String> lines = new ArrayList<>();

        if (section.isList("lines")) {
            lines.addAll(section.getStringList("lines"));
        } else {
            section.getString("lines").ifPresent(lines::add);
        }

        ChatMessageSpec spec = new ChatMessageSpec();
        spec.setLines(lines);
        return spec;
    }
}
