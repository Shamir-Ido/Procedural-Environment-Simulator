package pepse.world.trees;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import java.awt.*;

/**
 * A class representing a collectible fruit in the game world.
 * <p>
 * Fruits can be collected by the player (avatar). Once collected, the fruit disappears
 * and is scheduled to reappear after a full night cycle.
 */
public class Fruit extends GameObject {

    private static final String FRUIT_TAG = "fruit";
    private static final String AVATAR_TAG = "avatar";
    private final Color fruitColor;
    private boolean refreshFruit = false;
    private static final int FRUIT_SIZE = 25;

    /**
     * Constructs a Fruit instance at the given position and with the specified color.
     *
     * @param position   The initial position of the fruit in the game world.
     * @param fruitColor The color to render the fruit with.
     */
    public Fruit(Vector2 position, Color fruitColor) {
        super(position, new Vector2(FRUIT_SIZE, FRUIT_SIZE), new OvalRenderable(fruitColor));
        this.fruitColor = fruitColor;
        this.setTag(FRUIT_TAG);
    }


    /**
     * Called when this fruit collides with another GameObject.
     * <p>
     * If the other object is the player ("avatar"), the fruit becomes invisible
     * and is scheduled to reappear after a night cycle.
     *
     * @param other     The other GameObject involved in the collision.
     * @param collision The Collision object containing collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(AVATAR_TAG)) {
            renderer().setRenderable(null);
            new ScheduledTask(this, PepseGameManager.NIGHT_CYCLE_LENGTH, false,
                    () -> refreshFruit = true);
        }
    }

    /**
     * Updates the fruit each frame.
     * <p>
     * If the fruit is flagged for refresh, it becomes visible again with its original color.
     *
     * @param deltaTime The time elapsed since the last frame (in seconds).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(refreshFruit){
            refreshFruit = false;
            this.setTag(FRUIT_TAG);
            renderer().setRenderable(new OvalRenderable(fruitColor));
        }

    }
}
