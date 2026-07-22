package nl.matsgemmeke.battlegrounds.command.condition;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions;

public interface ParameterCondition<T> extends CommandConditions.ParameterCondition<T, BukkitCommandExecutionContext, BukkitCommandIssuer> {
}
