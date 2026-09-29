package nl.matsgemmeke.battlegrounds.game.component.membership;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import nl.matsgemmeke.battlegrounds.game.GameContextType;
import nl.matsgemmeke.battlegrounds.game.GameScope;
import nl.matsgemmeke.battlegrounds.game.component.ComponentRouterProvider;

import java.util.Map;

public class MembershipProvider extends ComponentRouterProvider<MembershipService> {

    @Inject
    public MembershipProvider(GameScope gameScope, Map<GameContextType, Provider<MembershipService>> providers, TypeLiteral<MembershipService> typeLiteral) {
        super(gameScope, providers, typeLiteral);
    }
}
