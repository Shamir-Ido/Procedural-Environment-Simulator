package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;


/**
 * Utility class for creating a "sun" GameObject that simulates a sun's motion across the sky.
 * <p>
 * The sun is rendered as a yellow oval and follows a circular trajectory over time,
 * giving the illusion of rising and setting as part of a day-night cycle.
 */
public class Sun {

    private static final float SUN_SIZE = 100;


    /**
     * Creates a sun GameObject that moves in a circular path to simulate a sun arc.
     *
     * @param windowDimensions The dimensions of the game window (used to center the arc).
     * @param cycleLength      The duration of the full sun movement cycle (in seconds).
     * @return A {@link GameObject} representing the animated sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        OvalRenderable oval = new OvalRenderable(Color.YELLOW);
        Vector2 initialSunCenter = new Vector2((windowDimensions.x() / 2) - (SUN_SIZE / 2), SUN_SIZE);

        GameObject sun = new GameObject(initialSunCenter, new Vector2(SUN_SIZE, SUN_SIZE), oval);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");

        Vector2 cycleCenter = new Vector2((windowDimensions.x() / 2), windowDimensions.y() * (2f/3f));
        new Transition<Float>(
                sun,
                (Float angle) ->
                sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                0f,
                360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );
        return sun;
    }



}
