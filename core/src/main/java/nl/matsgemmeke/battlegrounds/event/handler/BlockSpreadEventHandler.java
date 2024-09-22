package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockSpreadEventHandler implements EventHandler<BlockSpreadEvent> {

    public void handle(@NotNull BlockSpreadEvent event) {
        Block source = event.getSource();
        List<MetadataValue> metadata = source.getMetadata("battlegrounds-spread-fire");

        if (metadata.isEmpty() || metadata.get(0).asBoolean()) {
            return;
        }

        event.setCancelled(true);
    }
}
