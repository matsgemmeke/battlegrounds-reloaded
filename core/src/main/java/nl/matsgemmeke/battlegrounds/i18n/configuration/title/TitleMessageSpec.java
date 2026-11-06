package nl.matsgemmeke.battlegrounds.i18n.configuration.title;

import jakarta.validation.constraints.Min;
import nl.matsgemmeke.battlegrounds.i18n.configuration.MessageSpec;

public class TitleMessageSpec implements MessageSpec {

    private String titleText;
    private String subtitleText;
    @Min(value = 0, message = "fade in ticks must be 0 or higher")
    private Integer fadeInTicks;
    @Min(value = 0, message = "stay ticks must be 0 or higher")
    private Integer stayTicks;
    @Min(value = 0, message = "fade out ticks must be 0 or higher")
    private Integer fadeOutTicks;

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getSubtitleText() {
        return subtitleText;
    }

    public void setSubtitleText(String subtitleText) {
        this.subtitleText = subtitleText;
    }

    public Integer getFadeInTicks() {
        return fadeInTicks;
    }

    public void setFadeOutTicks(Integer fadeOutTicks) {
        this.fadeOutTicks = fadeOutTicks;
    }

    public Integer getStayTicks() {
        return stayTicks;
    }

    public void setStayTicks(Integer stayTicks) {
        this.stayTicks = stayTicks;
    }

    public Integer getFadeOutTicks() {
        return fadeOutTicks;
    }

    public void setFadeInTicks(Integer fadeInTicks) {
        this.fadeInTicks = fadeInTicks;
    }
}
