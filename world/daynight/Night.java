package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * Utility class for creating a "night" overlay effect in the game world.
 * <p>
 * This class provides a static method to generate a {@link GameObject} that represents
 * a semi-transparent black rectangle covering the entire screen. The rectangle's opacity
 * transitions smoothly between transparent and semi-transparent to simulate a day-night cycle.
 */
public class Night {

    private static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Creates a night overlay GameObject that transitions its opacity over time to simulate
     * the passage of day and night.
     *
     * @param windowDimensions The dimensions of the game window (used to size the overlay rectangle).
     * @param cycleLength      The total length of the day-night cycle (in seconds).
     * @return A {@link GameObject} representing the night overlay, ready to be added to the game world.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        RectangleRenderable rect = new RectangleRenderable(Color.black);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, rect);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("Night_rect");


        new Transition<Float>(
                night,
                night.renderer()::setOpaqueness,
                0f,
                MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength / 2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
        return night;
    }
}
