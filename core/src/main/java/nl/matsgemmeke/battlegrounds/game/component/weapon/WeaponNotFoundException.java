package nl.matsgemmeke.battlegrounds.game.component.weapon;

public class WeaponNotFoundException extends RuntimeException {

    public WeaponNotFoundException(String message) {
        super(message);
    }
}
