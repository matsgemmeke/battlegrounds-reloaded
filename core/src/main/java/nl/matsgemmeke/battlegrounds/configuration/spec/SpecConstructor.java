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
            Class<?> polymorphicType = this.getRegisteredPolymorphicType(mappingNode);

            if (polymorphicType != null) {
                node.setType(polymorphicType);
            }
        }

        return super.constructObject(node);
    }

    @Nullable
    private Class<?> getRegisteredPolymorphicType(MappingNode node) {
        for (NodeTuple tuple : node.getValue()) {
            if (tuple.getKeyNode() instanceof ScalarNode keyNode && tuple.getValueNode() instanceof ScalarNode valueNode) {
                String keyName = keyNode.getValue();
                String valueName = valueNode.getValue();

                Class<?> type = PolymorphicTypeRegistry.resolve(keyName, valueName).orElse(null);

                if (type != null) {
                    return type;
                }
            }
        }

        return null;
    }
}
