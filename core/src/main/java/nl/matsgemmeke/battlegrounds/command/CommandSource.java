package nl.matsgemmeke.battlegrounds.command;

import org.jetbrains.annotations.NotNull;

public abstract class CommandSource {

    @NotNull
    private String description;
    @NotNull
    private String name;
    @NotNull
    private String usage;

    public CommandSource(@NotNull String name, @NotNull String description, @NotNull String usage) {
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    /**
     * Gets the description of the command.
     *
     * @return the command description
     */
    @NotNull
    public String getDescription() {
        return description;
    }

    /**
     * Gets the name of the command.
     *
     * @return the command name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the usage of the command.
     *
     * @return the command name
     */
    @NotNull
    public String getUsage() {
        return usage;
    }
}
