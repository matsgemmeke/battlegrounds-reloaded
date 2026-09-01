package nl.matsgemmeke.battlegrounds.arena.configuration.setup;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementDataFactory;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.element.ElementType;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.map.ArenaMapData;
import nl.matsgemmeke.battlegrounds.arena.configuration.setup.spawn.CreateSpawnPointData;
import nl.matsgemmeke.battlegrounds.configuration.ConfigurationFile;
import nl.matsgemmeke.battlegrounds.configuration.Section;
import nl.matsgemmeke.battlegrounds.configuration.serialization.LocationDataSerializer;
import nl.matsgemmeke.battlegrounds.util.TextUtil;
import nl.matsgemmeke.battlegrounds.validation.ObjectValidator;
import nl.matsgemmeke.battlegrounds.validation.ValidationException;
import nl.matsgemmeke.battlegrounds.validation.Violation;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ArenaSetupConfiguration {

    private static final String CREATED_AT_PATH = "created-at";
    private static final String CREATED_BY_PATH = "created-by";
    private static final String MAPS_PATH = "maps";
    private static final String MAP_NAME_PATH = "name";
    private static final String MAP_CREATED_AT_PATH = "created-at";
    private static final String MAP_CREATED_BY_PATH = "created-by";

    private static final String ELEMENTS_PATH = "elements";
    private static final String ELEMENT_TYPE_PATH = "type";
    private static final String SPAWN_POINT_LOCATION_PATH = "location";
    private static final String SPAWN_POINT_TEAM_ID_PATH = "team-id";

    private final ConfigurationFile configurationFile;
    private final ElementDataFactory elementDataFactory;
    private final LocationDataSerializer locationDataSerializer;
    private final Logger logger;
    private final ObjectValidator objectValidator;

    @Inject
    public ArenaSetupConfiguration(
            ElementDataFactory elementDataFactory,
            LocationDataSerializer locationDataSerializer,
            @Named("Battlegrounds") Logger logger,
            ObjectValidator objectValidator,
            @Assisted ConfigurationFile configurationFile
    ) {
        this.elementDataFactory = elementDataFactory;
        this.locationDataSerializer = locationDataSerializer;
        this.logger = logger;
        this.objectValidator = objectValidator;
        this.configurationFile = configurationFile;
    }

    public Optional<Instant> getCreatedAt() {
        return configurationFile.getRootSection().getString(CREATED_AT_PATH).map(Instant::parse);
    }

    public void setCreatedAt(Instant instant) {
        configurationFile.getRootSection().set(CREATED_AT_PATH, instant.toString());
        configurationFile.save();
    }

    public Optional<UUID> getCreatedBy() {
        return configurationFile.getRootSection().getString(CREATED_BY_PATH).map(UUID::fromString);
    }

    public void setCreatedBy(UUID uuid) {
        configurationFile.getRootSection().set(CREATED_BY_PATH, uuid.toString());
        configurationFile.save();
    }

    public void createMap(MapCreationInfo mapCreationInfo) {
        String mapPathName = TextUtil.toKebabCase(mapCreationInfo.mapName());

        Section rootSection = configurationFile.getRootSection();
        rootSection.set(MAPS_PATH + "." + mapPathName + "." + MAP_NAME_PATH, mapCreationInfo.mapName());
        rootSection.set(MAPS_PATH + "." + mapPathName + "." + MAP_CREATED_AT_PATH, mapCreationInfo.createdAt().toString());
        rootSection.set(MAPS_PATH + "." + mapPathName + "." + MAP_CREATED_BY_PATH, mapCreationInfo.createdBy().toString());

        configurationFile.save();
    }

    public void removeMap(String mapName) {
        String mapPathName = TextUtil.toKebabCase(mapName);

        configurationFile.getRootSection().removeSection(MAPS_PATH + "." + mapPathName);
        configurationFile.save();
    }

    public Collection<ArenaMapData> getMaps() {
        Section mapsSection = configurationFile.getRootSection().getSection(MAPS_PATH).orElse(null);

        if (mapsSection == null) {
            return Collections.emptySet();
        }

        return mapsSection.getKeys().stream()
                .map(mapsKey -> this.readArenaMapData(mapsSection, mapsKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Nullable
    private ArenaMapData readArenaMapData(Section mapsSection, String mapKey) {
        String name = mapsSection.getString(mapKey + "." + MAP_NAME_PATH).orElse(null);
        Instant createdAt = mapsSection.getString(mapKey + "." + MAP_CREATED_AT_PATH).map(this::parseInstant).orElse(null);
        UUID createdBy = mapsSection.getString(mapKey + "." + MAP_CREATED_BY_PATH).map(this::parseUUID).orElse(null);
        List<ElementData> elements = mapsSection.getSection(mapKey + "." + ELEMENTS_PATH).map(this::readElements).orElse(Collections.emptyList());

        ArenaMapData mapData = new ArenaMapData(name, createdAt, createdBy, elements);

        try {
            objectValidator.validate(mapData);
            return mapData;
        } catch (ValidationException ex) {
            this.logMapViolations(mapKey, ex.getMessage(), ex.getViolations());
            return null;
        }
    }

    @Nullable
    private Instant parseInstant(String value) {
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    @Nullable
    private UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private List<ElementData> readElements(Section elementsSection) {
        List<Section> elementSections = elementsSection.getKeys().stream()
                .map(elementsSection::getSection)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return elementSections.stream()
                .map(elementSection -> elementSection.getString("element-type")
                        .map(this::parseElementType)
                        .map(elementType -> elementDataFactory.create(elementType, elementSection))
                        .map(elementData -> this.validateElementData(elementData, elementSection)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Nullable
    private ElementType parseElementType(String value) {
        try {
            return ElementType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Nullable
    private ElementData validateElementData(ElementData elementData, Section elementSection) {
        try {
            objectValidator.validate(elementData);
            return elementData;
        } catch (ValidationException ex) {
            this.logElementViolations(elementSection.getAbsolutePath(), ex.getMessage(), ex.getViolations());
            return null;
        }
    }

    private void logElementViolations(String elementPath, String exceptionMessage, List<Violation> violations) {
        String violationsMessage = violations.stream()
                .map(violation -> " - " + violation.propertyPath() + ": " + violation.message())
                .collect(Collectors.joining("\n"));

        logger.severe("Failed to load element located at '%s': %s\n%s".formatted(elementPath, exceptionMessage, violationsMessage));
    }

    private void logMapViolations(String mapKey, String exceptionMessage, List<Violation> violations) {
        String violationsMessage = violations.stream()
                .map(violation -> " - " + violation.propertyPath() + ": " + violation.message())
                .collect(Collectors.joining("\n"));

        logger.severe("Failed to load map %s: %s\n%s".formatted(mapKey, exceptionMessage, violationsMessage));
    }

    public void createSpawnPoint(CreateSpawnPointData data) {
        try {
            objectValidator.validate(data);
        } catch (ValidationException ex) {
            throw new IllegalArgumentException("Cannot create spawn point for invalid data", ex);
        }

        String mapPathName = TextUtil.toKebabCase(data.mapName());
        String spawnPointPath = MAPS_PATH + "." + mapPathName + "." + ELEMENTS_PATH + "." + data.elementId();
        String spawnPointLocationPath = spawnPointPath + "." + SPAWN_POINT_LOCATION_PATH;
        Section locationSection = configurationFile.getRootSection().createSection(spawnPointLocationPath);

        locationDataSerializer.serialize(data.locationData(), locationSection);
        configurationFile.getRootSection().set(spawnPointPath + "." + ELEMENT_TYPE_PATH, ElementType.SPAWN_POINT.toString());
        configurationFile.getRootSection().set(spawnPointPath + "." + SPAWN_POINT_TEAM_ID_PATH, data.teamId());
        configurationFile.save();
    }
}
