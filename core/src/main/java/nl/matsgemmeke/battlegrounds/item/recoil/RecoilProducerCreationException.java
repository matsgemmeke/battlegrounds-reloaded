package nl.matsgemmeke.battlegrounds.item.recoil;

import org.jetbrains.annotations.NotNull;

public class RecoilProducerCreationException extends RuntimeException {

    public RecoilProducerCreationException(@NotNull String message) {
        super(message);
    }
}
