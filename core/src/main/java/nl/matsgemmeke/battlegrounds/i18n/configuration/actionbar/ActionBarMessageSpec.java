package nl.matsgemmeke.battlegrounds.i18n.configuration.actionbar;

import jakarta.validation.constraints.NotBlank;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class ActionBarMessageSpec implements MessageSpec {

    @NotBlank(message = "action bar messages must contain a text value")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
