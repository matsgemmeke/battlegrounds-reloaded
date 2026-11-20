package nl.matsgemmeke.battlegrounds.command;

import org.jspecify.annotations.Nullable;

/**
 * The information of a subcommand.
 *
 * @param description the subcommand description, possibly null
 * @param usage       the subcommand usage syntax
 * @param suggestion  the command suggestion to use when a player clicks the command
 * @param permissions the permissions that allow usage of the subcommand
 */
public record CommandInfo(
        @Nullable
        String description,
        String usage,
        String suggestion,
        String[] permissions
) {
}
