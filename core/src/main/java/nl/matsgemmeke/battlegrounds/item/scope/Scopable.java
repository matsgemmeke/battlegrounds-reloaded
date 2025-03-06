package nl.matsgemmeke.battlegrounds.item.scope;

import org.jetbrains.annotations.NotNull;

public interface Scopable {

    boolean applyScope(@NotNull ScopeUser scopeUser);

    boolean cancelScope();

    boolean changeScopeMagnification();
    
    boolean isUsingScope();
}
