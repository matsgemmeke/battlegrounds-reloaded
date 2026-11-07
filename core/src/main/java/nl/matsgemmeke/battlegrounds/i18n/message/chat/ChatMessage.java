package nl.matsgemmeke.battlegrounds.i18n.message.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;

import java.util.List;

public class ChatMessage implements Message {

    private final List<Component> components;

    public ChatMessage(List<Component> components) {
        this.components = components;
    }

    @Override
    public void send(Audience audience) {
        components.forEach(audience::sendMessage);
    }
}
