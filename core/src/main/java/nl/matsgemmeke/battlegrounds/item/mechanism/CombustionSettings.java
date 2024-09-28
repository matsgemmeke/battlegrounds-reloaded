package nl.matsgemmeke.battlegrounds.item.mechanism;

public record CombustionSettings(int radius, long ticksBetweenFireSpread, boolean burnBlocks, boolean spreadFire) {
}
