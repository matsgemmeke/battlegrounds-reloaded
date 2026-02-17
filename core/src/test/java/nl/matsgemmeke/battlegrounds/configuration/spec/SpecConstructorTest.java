package nl.matsgemmeke.battlegrounds.configuration.spec;

import nl.matsgemmeke.battlegrounds.configuration.item.effect.DamageEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.effect.ItemEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.nodes.*;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SpecConstructorTest {

    private static final Class<?> TYPE = GunSpec.class;
    private static final LoaderOptions LOADING_CONFIG = new LoaderOptions();

    private SpecConstructor specConstructor;

    @BeforeEach
    void setUp() {
        SpecPropertyUtils propertyUtils = new SpecPropertyUtils();
        propertyUtils.setSkipMissingProperties(true);

        specConstructor = new SpecConstructor(TYPE, LOADING_CONFIG);
        specConstructor.setPropertyUtils(new SpecPropertyUtils());
    }

    @Test
    @DisplayName("constructObject does not change type when node is not an instance of MappingNode")
    void constructObject_noMappingNode() {
        Node node = new ScalarNode(Tag.STR, "example", null, null, ScalarStyle.PLAIN);

        Object object = specConstructor.constructObject(node);

        assertThat(object).isEqualTo("example");
    }

    @Test
    @DisplayName("constructObject does not change type when node type is not registered as polymorphic type")
    void constructObject_nodeNotRegisteredAsPolymorphic() {
        NodeTuple effectTypeTuple = new NodeTuple(
                new ScalarNode(Tag.STR, "type", null, null, ScalarStyle.PLAIN),
                new ScalarNode(Tag.STR, "DAMAGE", null, null, ScalarStyle.PLAIN)
        );

        Node node = new MappingNode(Tag.MAP, List.of(effectTypeTuple), FlowStyle.FLOW);
        node.setType(Object.class);

        Object object = specConstructor.constructObject(node);

        assertThat(object).isInstanceOf(LinkedHashMap.class);
    }

    @Test
    @DisplayName("constructObject sets type to polymorphic implementation type when node type is registered as polymorphic type")
    void constructObject_nodeRegisteredAsPolymorphic() {
        NodeTuple effectTypeTuple = new NodeTuple(
                new ScalarNode(Tag.STR, "type", null, null, ScalarStyle.PLAIN),
                new ScalarNode(Tag.STR, "DAMAGE", null, null, ScalarStyle.PLAIN)
        );
        NodeTuple damageTypeTuple = new NodeTuple(
                new ScalarNode(Tag.STR, "damage-type", null, null, ScalarStyle.PLAIN),
                new ScalarNode(Tag.STR, "BULLET_DAMAGE", null, null, ScalarStyle.PLAIN)
        );

        Node node = new MappingNode(Tag.MAP, List.of(effectTypeTuple, damageTypeTuple), FlowStyle.FLOW);
        node.setType(ItemEffectSpec.class);

        Object object = specConstructor.constructObject(node);

        assertThat(object).isInstanceOf(DamageEffectSpec.class);
    }
}
