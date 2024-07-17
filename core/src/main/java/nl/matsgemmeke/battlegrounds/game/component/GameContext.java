package nl.matsgemmeke.battlegrounds.game.component;

import org.jetbrains.annotations.NotNull;

public interface GameContext {

    @NotNull
    AudioEmitter getAudioEmitter();

    @NotNull
    CollisionDetector getCollisionDetector();
}
