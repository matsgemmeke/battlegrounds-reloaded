package nl.matsgemmeke.battlegrounds.command;

import co.aikar.commands.PaperCommandManager;

/**
 * Registers a feature's commands, command completions, and command conditions with the command framework.
 * <p>
 * Each feature that exposes commands should provide its own implementation of this interface, keeping all registration
 * logic for that feature self-contained. This allows the plugin to configure every feature's commands without
 * depending on any feature-specific classes, preserving the one-way dependency from features to common infrastructure.
 */
public interface CommandExtension {

    /**
     * Registers this feature's commands, command completions, and/or command conditions on the given command manager.
     *
     * @param commandManager the command manager to register commands, completions, and conditions with
     */
    void configure(PaperCommandManager commandManager);
}
