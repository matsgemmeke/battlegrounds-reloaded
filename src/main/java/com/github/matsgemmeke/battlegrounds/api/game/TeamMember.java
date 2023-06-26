package com.github.matsgemmeke.battlegrounds.api.game;

import org.jetbrains.annotations.Nullable;

public interface TeamMember {

    @Nullable
    Team getTeam();

    void setTeam(@Nullable Team team);
}
