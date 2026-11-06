package nl.matsgemmeke.battlegrounds.i18n.configuration;

import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.util.TextUtil;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.validation.Violation;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LanguageConfiguration {

    private final ConfigurationFile configurationFile;
    private final Logger logger;
    private final MessageSpecFactory messageSpecFactory;
    private final ObjectValidator objectValidator;

    public LanguageConfiguration(
            ConfigurationFile configurationFile,
            @Named("Battlegounds") Logger logger,
            MessageSpecFactory messageSpecFactory,
            ObjectValidator objectValidator
    ) {
        this.configurationFile = configurationFile;
        this.logger = logger;
        this.messageSpecFactory = messageSpecFactory;
        this.objectValidator = objectValidator;
    }

    public Optional<MessageSpec> getMessageSpec(String path) {
        return configurationFile.getRootSection().getSection(path)
                .flatMap(section -> section.getString("type")
                        .flatMap(this::parseMessageType)
                        .map(messageType -> messageSpecFactory.create(messageType, section))
                        .flatMap(messageSpec -> this.validateMessageSpec(path, messageSpec)));
    }

    private Optional<MessageType> parseMessageType(String type) {
        try {
            return Optional.of(MessageType.valueOf(type));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Optional<MessageSpec> validateMessageSpec(String path, MessageSpec messageSpec) {
        try {
            objectValidator.validate(messageSpec);
            return Optional.of(messageSpec);
        } catch (ValidationException ex) {
            this.logMessageSpecViolations(path, ex.getViolations());
            return Optional.empty();
        }
    }

    private void logMessageSpecViolations(String path, List<Violation> violations) {
        String violationsMessage = violations.stream()
                .map(violation -> " - " + violation.propertyPath() + ": " + violation.message())
                .collect(Collectors.joining("\n"));
        String violationCountIndication = TextUtil.pluralize(violations.size(), "violation", "violations");

        logger.severe("Attempted to load message at '%s' but was invalid (%d constraint %s):\n%s".formatted(path, violations.size(), violationCountIndication, violationsMessage));
    }

    public Optional<String> getTextValue(String path) {
        Section rootSection = configurationFile.getRootSection();

        if (!rootSection.exists(path)) {
            return Optional.empty();
        }

        if (rootSection.isList(path)) {
            List<String> lines = rootSection.getStringList(path);

            if (lines.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(String.join("\n", lines));
        } else {
            return rootSection.getString(path);
        }
    }
}
