package nl.matsgemmeke.battlegrounds.i18n.configuration.chat;

import jakarta.validation.constraints.NotBlank;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class ChatMessageSpec implements MessageSpec {

    @NotBlank(message = "chat messages must contain a text value")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
