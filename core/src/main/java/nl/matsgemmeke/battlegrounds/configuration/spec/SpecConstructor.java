package nl.matsgemmeke.battlegrounds.configuration.spec;

import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.*;

public class SpecConstructor extends Constructor {

    public SpecConstructor(Class<?> rootType, LoaderOptions loadingConfig) {
        super(rootType, loadingConfig);
    }

    @Override
    public Object constructObject(Node node) {
        if (node instanceof MappingNode mappingNode) {
            Class<?> baseType = mappingNode.getType();
            PolymorphicDefinition definition = PolymorphicTypeRegistry.get(baseType).orElse(null);

            // Check if the node type is registered as a polymorphic type
            if (definition != null) {
                String discriminatorValue = this.readDiscriminatorValue(mappingNode, definition.discriminator());

                if (discriminatorValue != null) {
                    // Find the corresponding polymorphic type by looking up the discriminator
                    Class<?> resolved = definition.mappings().get(discriminatorValue);

                    node.setType(resolved);
                }
            }
        }

        return super.constructObject(node);
    }

    @Nullable
    private String readDiscriminatorValue(MappingNode node, String discriminatorKey) {
        for (NodeTuple tuple : node.getValue()) {
            Node keyNode = tuple.getKeyNode();

            if (keyNode instanceof ScalarNode scalarKey) {
                if (discriminatorKey.equals(scalarKey.getValue())) {
                    Node valueNode = tuple.getValueNode();

                    if (valueNode instanceof ScalarNode scalarValue) {
                        return scalarValue.getValue();
                    }
                }
            }
        }

        return null;
    }
}
