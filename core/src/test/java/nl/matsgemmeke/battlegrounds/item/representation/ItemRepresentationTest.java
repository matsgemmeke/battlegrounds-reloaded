package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRepresentationTest {

    private static final int AMOUNT = 2;

    @Mock
    private ItemTemplate itemTemplate;
    @Captor
    private ArgumentCaptor<Map<String, Object>> placeholderValuesCaptor;
    @InjectMocks
    private ItemRepresentation itemRepresentation;

    @Test
    @DisplayName("update returns new ItemStack instance from item template with placeholder values")
    void updateReturnsNewItemStackInstanceFromItemTemplateWithPlaceholderValues() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        when(itemTemplate.createItemStack(anyMap())).thenReturn(itemStack);

        itemRepresentation.setAmount(AMOUNT);
        itemRepresentation.setPlaceholder(Placeholder.ITEM_NAME, "Test");
        ItemStack result = itemRepresentation.update();

        verify(itemTemplate).createItemStack(placeholderValuesCaptor.capture());
        Map<String, Object> placeholderValues = placeholderValuesCaptor.getValue();

        assertThat(result.getAmount()).isEqualTo(AMOUNT);
        assertThat(result.getType()).isEqualTo(Material.IRON_HOE);
        assertThat(placeholderValues).containsExactly(entry("name", "Test"));
    }
}
