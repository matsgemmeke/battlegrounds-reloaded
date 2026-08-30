package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.spawn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;

public class SpawnPointData extends ElementData {

    @NotNull
    @Valid
    private LocationData locationData;

    @NotNull
    @Min(value = 1, message = "team id must be greater than zero")
    private Integer teamId;

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }
}
