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

    public Optional<SoundSequenceSpec> getSoundSequence(String sequenceId) {
        Section rootSection = configurationFile.getRootSection();

        if (!rootSection.isList(sequenceId)) {
            return Optional.empty();
        }

        try {
            return Optional.of(this.loadSequence(sequenceId));
        } catch (InvalidSoundSequenceException ex) {
            logger.warning("Sound sequence '%s' is invalid and will be ignored".formatted(sequenceId));
            ex.getErrors().forEach(error -> logger.warning(" - " + error));
            return Optional.empty();
        }
    }

    private SoundSequenceSpec loadSequence(String sequenceId) throws InvalidSoundSequenceException {
        List<String> entryValues = configurationFile.getRootSection().getStringList(sequenceId);
        List<SoundSequenceEntrySpec> entries = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < entryValues.size(); i++) {
            try {
                entries.add(this.parseEntry(entryValues.get(i)));
            } catch (InvalidSoundSequenceEntryException ex) {
                errors.add("entry %s: %s".formatted(i + 1, ex.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidSoundSequenceException("Sound sequence has parsing errors", errors);
        }

        SoundSequenceSpec sequence = new SoundSequenceSpec(sequenceId, entries);

        try {
            objectValidator.validate(sequence);
        } catch (ValidationException ex) {
            List<String> violationMessages = ex.getViolations().stream().map(Violation::message).toList();
            throw new InvalidSoundSequenceException("Sound sequence has validation errors", violationMessages);
        }

        return sequence;
    }

    private SoundSequenceEntrySpec parseEntry(String entryValue) throws InvalidSoundSequenceEntryException {
        String[] parts = entryValue.split("\\s+");

        if (parts.length != 4) {
            throw new InvalidSoundSequenceEntryException("has %s parts, expected 4 parts (sound volume pitch delay)".formatted(parts.length));
        }

        String sound = parts[0];
        float volume = this.parseFloat("volume", parts[1]);
        float pitch = this.parseFloat("pitch", parts[2]);
        int delay = this.parseInt("delay", parts[3]);

        return new SoundSequenceEntrySpec(sound, volume, pitch, delay);
    }

    private float parseFloat(String field, String floatValue) throws InvalidSoundSequenceEntryException {
        try {
            return Float.parseFloat(floatValue);
        } catch (NumberFormatException ex) {
            throw new InvalidSoundSequenceEntryException("%s '%s' is not a decimal number".formatted(field, floatValue));
        }
    }

    private int parseInt(String field, String intValue) throws InvalidSoundSequenceEntryException {
        try {
            return Integer.parseInt(intValue);
        } catch (NumberFormatException ex) {
            throw new InvalidSoundSequenceEntryException("%s '%s' is not a whole number".formatted(field, intValue));
        }
    }
}
