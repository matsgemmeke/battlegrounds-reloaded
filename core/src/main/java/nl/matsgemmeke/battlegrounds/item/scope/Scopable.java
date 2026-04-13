package nl.matsgemmeke.battlegrounds.item.scope;

public interface Scopable {

    boolean applyScope(ScopeUser scopeUser);

    boolean cancelScope();

    boolean changeScopeMagnification();
    
    boolean isUsingScope();
}
