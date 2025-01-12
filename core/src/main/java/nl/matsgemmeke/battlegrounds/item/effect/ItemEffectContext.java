package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.jetbrains.annotations.NotNull;

public class ItemEffectContext {

    @NotNull
    private ItemEffectSource source;
    @NotNull
    private ItemHolder holder;

    public ItemEffectContext(@NotNull ItemHolder holder, @NotNull ItemEffectSource source) {
        this.holder = holder;
        this.source = source;
    }

    @NotNull
    public ItemHolder getHolder() {
        return holder;
    }

    public void setHolder(@NotNull ItemHolder holder) {
        this.holder = holder;
    }

    @NotNull
    public ItemEffectSource getSource() {
        return source;
    }

    public void setSource(@NotNull ItemEffectSource source) {
        this.source = source;
    }
}
