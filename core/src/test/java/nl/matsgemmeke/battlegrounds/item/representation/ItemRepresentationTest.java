package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRepresentationTest {

    private ItemTemplate itemTemplate;

    @Captor
    private ArgumentCaptor<Map<String, Object>> placeholderValuesCaptor;

    @BeforeEach
    public void setUp() {
        itemTemplate = mock(ItemTemplate.class);
    }

    @Test
    public void updateReturnsNewItemStackInstanceFromItemTemplateWithPlaceholderValues() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        when(itemTemplate.createItemStack(anyMap())).thenReturn(itemStack);

        ItemRepresentation itemRepresentation = new ItemRepresentation(itemTemplate);
        itemRepresentation.setPlaceholder(Placeholder.ITEM_NAME, "Test");
        ItemStack result = itemRepresentation.update();

        verify(itemTemplate).createItemStack(placeholderValuesCaptor.capture());
        Map<String, Object> placeholderValues = placeholderValuesCaptor.getValue();

        assertThat(result).isEqualTo(itemStack);
        assertThat(placeholderValues).containsExactly(entry("name", "Test"));
    }
}
