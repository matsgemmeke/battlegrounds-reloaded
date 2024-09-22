package nl.matsgemmeke.battlegrounds.event.handler;

import nl.matsgemmeke.battlegrounds.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockBurnEventHandler implements EventHandler<BlockBurnEvent> {

    public void handle(@NotNull BlockBurnEvent event) {
        Block block = event.getBlock();
        List<MetadataValue> metadata = block.getMetadata("battlegrounds-burn-blocks");

        if (metadata.isEmpty() || metadata.get(0).asBoolean()) {
            return;
        }

        event.setCancelled(true);
    }
}
