package nl.matsgemmeke.battlegrounds.arena.configuration.setup.element;

import jakarta.validation.constraints.Min;

public class SpawnPointData extends ElementData {

    @Min(value = 1, message = "team id must be greater than zero")
    private int teamId;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
