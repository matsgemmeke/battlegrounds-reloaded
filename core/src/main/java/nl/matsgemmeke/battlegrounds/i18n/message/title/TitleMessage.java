package nl.matsgemmeke.battlegrounds.i18n.message.title;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import nl.matsgemmeke.battlegrounds.i18n.message.Message;

public class TitleMessage implements Message {

    private final Title title;

    public TitleMessage(Title title) {
        this.title = title;
    }

    @Override
    public void send(Audience audience) {
        audience.showTitle(title);
    }
}
