package nl.matsgemmeke.battlegrounds.game.component;

import org.jetbrains.annotations.NotNull;

public interface GameContext {

    @NotNull
    CollisionDetector getCollisionDetector();
}
