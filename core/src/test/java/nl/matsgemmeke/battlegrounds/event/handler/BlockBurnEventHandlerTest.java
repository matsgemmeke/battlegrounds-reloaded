package nl.matsgemmeke.battlegrounds.event.handler;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.metadata.MetadataValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockBurnEventHandlerTest {

    private Block block;
    private Block ignitingBlock;

    @Before
    public void setUp() {
        block = mock(Block.class);
        ignitingBlock = mock(Block.class);
    }

    @Test
    public void shouldNotCancelIfBlockHasNoMetadataValueForBurningBlocks() {
        when(block.getMetadata("battlegrounds-burn-blocks")).thenReturn(Collections.emptyList());

        BlockBurnEvent event = new BlockBurnEvent(block, ignitingBlock);

        BlockBurnEventHandler eventHandler = new BlockBurnEventHandler();
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldNotCancelIfMetadataValueForBurningBlocksIsTrue() {
        MetadataValue metadataValue = mock(MetadataValue.class);
        when(metadataValue.asBoolean()).thenReturn(true);

        when(block.getMetadata("battlegrounds-burn-blocks")).thenReturn(List.of(metadataValue));

        BlockBurnEvent event = new BlockBurnEvent(block, ignitingBlock);

        BlockBurnEventHandler eventHandler = new BlockBurnEventHandler();
        eventHandler.handle(event);

        assertFalse(event.isCancelled());
    }

    @Test
    public void shouldCancelIfMetadataValueForSpreadingFireIsFalse() {
        MetadataValue metadataValue = mock(MetadataValue.class);
        when(metadataValue.asBoolean()).thenReturn(false);

        when(block.getMetadata("battlegrounds-burn-blocks")).thenReturn(List.of(metadataValue));

        BlockBurnEvent event = new BlockBurnEvent(block, ignitingBlock);

        BlockBurnEventHandler eventHandler = new BlockBurnEventHandler();
        eventHandler.handle(event);

        assertTrue(event.isCancelled());
    }
}
