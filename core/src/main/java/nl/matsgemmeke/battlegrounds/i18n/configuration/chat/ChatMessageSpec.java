package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import jakarta.validation.constraints.NotEmpty;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;
import nl.matsgemmeke.battlegrounds.i18n.validation.ChatLine;

import java.util.List;

public class ChatMessageSpec implements MessageSpec {

    @NotEmpty(message = "chat messages must contain at least one line")
    private List<@ChatLine String> lines;

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }
}
