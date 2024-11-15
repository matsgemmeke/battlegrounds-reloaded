package nl.matsgemmeke.battlegrounds.event.handler;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.metadata.MetadataValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockSpreadEventHandlerTest {

    private Block block;
    private Block source;
    private BlockState blockState;

    @BeforeEach
    public void setUp() {
        block = mock(Block.class);
        source = mock(Block.class);
        blockState = mock(BlockState.class);
    }

    @Test
    public void shouldNotCancelIfBlockHasNoMetadataValueForSpreadingFire() {
        when(source.getMetadata("battlegrounds-spread-fire")).thenReturn(Collections.emptyList());

        BlockSpreadEvent event = new BlockSpreadEvent(block, source, blockState);

        BlockSpreadEventHandler eventHandler = new BlockSpreadEventHandler();
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldNotCancelIfMetadataValueForSpreadingFireIsTrue() {
        MetadataValue metadataValue = mock(MetadataValue.class);
        when(metadataValue.asBoolean()).thenReturn(true);

        when(source.getMetadata("battlegrounds-spread-fire")).thenReturn(List.of(metadataValue));

        BlockSpreadEvent event = new BlockSpreadEvent(block, source, blockState);

        BlockSpreadEventHandler eventHandler = new BlockSpreadEventHandler();
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelIfMetadataValueForSpreadingFireIsFalse() {
        MetadataValue metadataValue = mock(MetadataValue.class);
        when(metadataValue.asBoolean()).thenReturn(false);

        when(source.getMetadata("battlegrounds-spread-fire")).thenReturn(List.of(metadataValue));

        BlockSpreadEvent event = new BlockSpreadEvent(block, source, blockState);

        BlockSpreadEventHandler eventHandler = new BlockSpreadEventHandler();
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
    }
}
