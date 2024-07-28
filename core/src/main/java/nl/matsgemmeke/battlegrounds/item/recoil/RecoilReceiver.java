package nl.matsgemmeke.battlegrounds.item.recoil;

/**
 * An entity capable of receiving recoil from an item.
 */
public interface RecoilReceiver {

    /**
     * Gets whether the entity is currently in the condition of being able to receive recoil.
     *
     * @return whether the entity can receive recoil
     */
    boolean canReceiveRecoil();

    /**
     * Gets the relative accuracy of the entity, which influences how much recoil will be produced. This value returns
     * a relative number based on the state of the entity. For example, it should return 1.0 if the accuracy is
     * unaffected, 0.5 if the accuracy is worsened, 2.0 if the accuracy is improved etc.
     *
     * @return the relative accuracy
     */
    float getRelativeAccuracy();

    /**
     * Adjusts the camera rotation of the entity based on the given values.
     *
     * @param yaw the relative yaw change
     * @param pitch the relative pitch change
     */
    void modifyCameraRotation(float yaw, float pitch);
}
