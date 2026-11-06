package nl.matsgemmeke.battlegrounds.i18n.message.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;

public class ChatMessage implements Message {

    private final Component component;

    public ChatMessage(Component component) {
        this.component = component;
    }

    @Override
    public void send(Audience audience) {
        audience.sendMessage(component);
    }
}
