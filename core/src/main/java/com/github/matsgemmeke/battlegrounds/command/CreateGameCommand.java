package com.github.matsgemmeke.battlegrounds.command;

import com.github.matsgemmeke.battlegrounds.api.BattleContextProvider;
import com.github.matsgemmeke.battlegrounds.api.game.GameConfiguration;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.game.DefaultGameConfiguration;
import com.github.matsgemmeke.battlegrounds.locale.PlaceholderEntry;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import com.github.matsgemmeke.battlegrounds.game.GameContextFactory;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CreateGameCommand extends CommandSource {

    @NotNull
    private BattleContextProvider contextProvider;
    @NotNull
    private GameContextFactory contextFactory;
    @NotNull
    private Translator translator;

    public CreateGameCommand(
            @NotNull BattleContextProvider contextProvider,
            @NotNull GameContextFactory contextFactory,
            @NotNull Translator translator
    ) {
        super("creategame", translator.translate(TranslationKey.DESCRIPTION_CREATEGAME.getPath()), "bg creategame <id>");
        this.contextProvider = contextProvider;
        this.contextFactory = contextFactory;
        this.translator = translator;
    }

    public void execute(@NotNull CommandSender sender, int id) {
        GameConfiguration configuration = DefaultGameConfiguration.getNewConfiguration();
        GameContext context = contextFactory.make(id, configuration);

        PlaceholderEntry placeholder = new PlaceholderEntry("bg_game", String.valueOf(id));

        if (!contextProvider.addGameContext(context)) {
            sender.sendMessage(translator.translate(TranslationKey.GAME_CREATION_FAILED.getPath(), placeholder));
            return;
        }

        sender.sendMessage(translator.translate(TranslationKey.GAME_CREATED.getPath(), placeholder));
    }
}
