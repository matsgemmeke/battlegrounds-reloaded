package nl.matsgemmeke.battlegrounds.game.training;

import com.google.inject.Inject;
import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.GameContextProvider;
import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.jetbrains.annotations.NotNull;

public class TrainingModeContextProvider implements Provider<GameContext> {

    private GameContextProvider contextProvider;
    private InternalsProvider internals;
    private TrainingModeFactory trainingModeFactory;

    @Inject
    public TrainingModeContextProvider(
            @NotNull GameContextProvider contextProvider,
            @NotNull InternalsProvider internals,
            @NotNull TrainingModeFactory trainingModeFactory
    ) {
        this.contextProvider = contextProvider;
        this.internals = internals;
        this.trainingModeFactory = trainingModeFactory;
    }

    public GameContext get() {
        TrainingMode trainingMode = trainingModeFactory.make();
        TrainingModeContext trainingModeContext = new TrainingModeContext(trainingMode, internals);

        contextProvider.assignTrainingModeContext(trainingModeContext);

        return trainingModeContext;
    }
}
