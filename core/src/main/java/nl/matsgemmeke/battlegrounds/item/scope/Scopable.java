package nl.matsgemmeke.battlegrounds.item.scope;

import org.jetbrains.annotations.NotNull;

public interface Scopable {

    boolean applyScope(@NotNull ScopeUser user);

    boolean cancelScope();
    
    boolean isUsingScope();
}
