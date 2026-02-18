package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Represents the sky background in the game.
 * Provides a static method to create a sky GameObject that fills the entire window.
 */
public class Sky {

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Creates a new GameObject representing the sky background.
     * The sky fills the entire window area and is rendered relative to the camera.
     *
     * @param windowDims The dimensions of the game window (width and height).
     * @return A GameObject representing the sky background.
     */
    public static GameObject create(Vector2 windowDims){
        GameObject sky = new GameObject(Vector2.ZERO, windowDims, new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag("sky");
        return sky;
    }
}
