package nl.matsgemmeke.battlegrounds.item;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import nl.matsgemmeke.battlegrounds.item.collision.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachmentFactory;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;

public class ItemModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(CollisionDetector.class).in(Singleton.class);

        binder.install(new FactoryModuleBuilder()
                .implement(ScopeAttachment.class, DefaultScopeAttachment.class)
                .build(DefaultScopeAttachmentFactory.class));
    }
}
