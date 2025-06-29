package nl.matsgemmeke.battlegrounds.item.recoil;

import org.jetbrains.annotations.NotNull;

public class RecoilCreationException extends RuntimeException {

    public RecoilCreationException(@NotNull String message) {
        super(message);
    }
}
