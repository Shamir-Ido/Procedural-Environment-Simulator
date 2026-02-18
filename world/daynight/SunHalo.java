package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Utility class for creating a "sun halo" visual effect.
 * <p>
 * The sun halo is a large, semi-transparent oval that overlays the sun to provide
 * a glowing aura effect, enhancing the visual realism of the sun.
 */
public class SunHalo {

    private static final float HALO_SIZE = 200 ;

    /**
     * Creates a halo GameObject that visually surrounds the given sun object.
     * <p>
     * The halo is rendered as a large, semi-transparent yellow oval. It is intended
     * to be positioned and updated externally to follow the sun's position.
     *
     * @param sun The {@link GameObject} representing the sun.
     *           (Currently unused in this method, but can be used to position the halo.)
     * @return A {@link GameObject} representing the sun halo.
     */
    public static GameObject create(GameObject sun) {
        OvalRenderable oval = new OvalRenderable(new Color(255, 255, 0, 20));
        GameObject halo = new GameObject(Vector2.ZERO, new Vector2(HALO_SIZE, HALO_SIZE), oval);
        halo.setTag("halo");
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return halo;
    }
}
