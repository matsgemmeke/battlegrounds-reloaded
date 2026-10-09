package nl.matsgemmeke.battlegrounds.sound.configuration;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.validation.Violation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SoundsConfiguration {

    private final ConfigurationFile configurationFile;
    private final Logger logger;
    private final ObjectValidator objectValidator;

    @Inject
    public SoundsConfiguration(@Named("sounds") ConfigurationFile configurationFile, @Named("Battlegrounds") Logger logger, ObjectValidator objectValidator) {
        this.configurationFile = configurationFile;
        this.logger = logger;
        this.objectValidator = objectValidator;
    }

    public Optional<SoundTemplateSpec> getSoundTemplate(String templateId) {
        Section rootSection = configurationFile.getRootSection();

        if (!rootSection.isList(templateId)) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.loadTemplate(templateId));
        } catch (InvalidSoundTemplateException ex) {
            logger.warning("Sound template '%s' is invalid and will be ignored".formatted(templateId));
            ex.getErrors().forEach(error -> logger.warning(" - " + error));
            return Optional.empty();
        }
    }

    private SoundTemplateSpec loadTemplate(String templateId) throws InvalidSoundTemplateException {
        List<String> cueValues = configurationFile.getRootSection().getStringList(templateId);
        List<SoundCueSpec> cues = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < cueValues.size(); i++) {
            try {
                cues.add(this.parseCue(cueValues.get(i)));
            } catch (InvalidSoundCueException ex) {
                errors.add("entry %s: %s".formatted(i + 1, ex.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidSoundTemplateException("Sound template has parsing errors", errors);
        }

        SoundTemplateSpec template = new SoundTemplateSpec(templateId, cues);

        try {
            objectValidator.validate(template);
        } catch (ValidationException ex) {
            List<String> violationMessages = ex.getViolations().stream().map(Violation::message).toList();
            throw new InvalidSoundTemplateException("Sound template has validation errors", violationMessages);
        }

        return template;
    }

    private SoundCueSpec parseCue(String cueValue) throws InvalidSoundCueException {
        String[] parts = cueValue.split("\\s+");

        if (parts.length != 4) {
            throw new InvalidSoundCueException("has %s parts, expected 4 parts (sound volume pitch delay)".formatted(parts.length));
        }

        String sound = parts[0];
        float volume = this.parseFloat("volume", parts[1]);
        float pitch = this.parseFloat("pitch", parts[2]);
        int delay = this.parseInt("delay", parts[3]);

        return new SoundCueSpec(sound, volume, pitch, delay);
    }

    private float parseFloat(String field, String floatValue) throws InvalidSoundCueException {
        try {
            return Float.parseFloat(floatValue);
        } catch (NumberFormatException ex) {
            throw new InvalidSoundCueException("%s '%s' is not a decimal number".formatted(field, floatValue));
        }
    }

    private int parseInt(String field, String intValue) throws InvalidSoundCueException {
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException ex) {
            throw new InvalidSoundCueException("%s '%s' is not a whole number".formatted(field, intValue));
        }
    }
}
