package nl.matsgemmeke.battlegrounds.command;

public abstract class CommandSource {

    private final String description;
    private final String name;
    private final String usage;

    public CommandSource(String name, String description, String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Gets the description of the command.
     *
     * @return the command description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the command.
     *
     * @return the command name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the usage of the command.
     *
     * @return the command name
     */
    public String getUsage() {
        return usage;
    }
}
