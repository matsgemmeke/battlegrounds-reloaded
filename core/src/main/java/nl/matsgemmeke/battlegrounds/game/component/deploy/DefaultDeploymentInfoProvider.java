package nl.matsgemmeke.battlegrounds.game.component.deploy;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.component.item.EquipmentRegistry;
import nl.matsgemmeke.battlegrounds.item.deploy.DeployableItem;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DefaultDeploymentInfoProvider implements DeploymentInfoProvider {

    @NotNull
    private EquipmentRegistry equipmentRegistry;

    @Inject
    public DefaultDeploymentInfoProvider(@NotNull EquipmentRegistry equipmentRegistry) {
        this.equipmentRegistry = equipmentRegistry;
    }

    @NotNull
    public List<DeploymentObject> getAllDeploymentObjects() {
        List<DeploymentObject> deploymentObjects = new ArrayList<>();

        for (Equipment equipment : equipmentRegistry.findAll()) {
            DeploymentObject deploymentObject = equipment.getDeploymentObject();

            if (deploymentObject != null) {
                deploymentObjects.add(deploymentObject);
            }
        }

        return deploymentObjects;
    }

    @Nullable
    public DeployableItem getDeployableItem(@NotNull DeploymentObject deploymentObject) {
        for (Equipment equipment : equipmentRegistry.findAll()) {
            if (equipment.getDeploymentObject() == deploymentObject) {
                return equipment;
            }
        }

        return null;
    }
}
