package nl.matsgemmeke.battlegrounds.freeplay;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameKey;

public class FreeplayGameContext extends GameContext {

    public FreeplayGameContext() {
        super(GameKey.ofFreeplay(), GameContextType.FREEPLAY_MODE);
    }
}
