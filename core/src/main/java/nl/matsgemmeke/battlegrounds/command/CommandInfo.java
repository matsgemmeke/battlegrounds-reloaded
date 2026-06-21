package nl.matsgemmeke.battlegrounds.command;

/**
 * The information of a subcommand.
 *
 * @param name        the subcommand name
 * @param description the subcommand description
 * @param usage       the subcommand usage syntax
 * @param suggestion  the command suggestion to use when a player clicks the command
 * @param permissions the permissions that allow usage of the subcommand
 */
public record CommandInfo(String name, String description, String usage, String suggestion, String[] permissions) {
}
