package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Represents a cloud composed of multiple blocks that move horizontally across the screen,
 * occasionally generating rain drops that fall downwards.
 */
public class Cloud extends GameObject {

    private static final float GRAVITY = 600;
    private static final int CLOUD_VELOCITY = 100;
    private static final int RAIN_DROP_SIZE = 10;
    private static final String RAIN_TAG = "rain";
    private static Random random = new Random();
    private final Vector2 windowDimensions;
    private final List<Block> cloudBlocks = new ArrayList<>();

    List<List<Integer>> cloud = List.of(
            List.of(0, 1, 1, 0, 0, 0),
            List.of(1, 1, 1, 0, 1, 0),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(0, 1, 1, 1, 0, 0),
            List.of(0, 0, 0, 0, 0, 0)
    );

    private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
    private static final Color BASE_RAIN_DROP_COLOR = Color.CYAN;

    /**
     * Constructs a Cloud instance that moves horizontally and can produce rain drops.
     *
     * @param windowDimensions The dimensions of the game window, used for boundary checks.
     */
    public Cloud(Vector2 windowDimensions) {
        super(Vector2.ZERO, Vector2.ONES, null);
        this.windowDimensions = windowDimensions;
    }

    /**
     * Creates the cloud blocks according to the predefined cloud shape pattern.
     * Each cloud block is set with velocity and coordinate space.
     *
     * @return A list of the cloud blocks created.
     */
    public List<Block> create(){
        Vector2 startPos = Vector2.ZERO.add(new Vector2(-(Block.SIZE * cloud.get(0).size()),
                                        Block.SIZE * cloud.size()));
        for (int i = 0; i < cloud.size(); i++) {
            for (int j = 0; j < cloud.get(0).size(); j++) {
                if (cloud.get(i).get(j) == 1) {
                    RectangleRenderable rect =
                            new RectangleRenderable(ColorSupplier.approximateColor(BASE_CLOUD_COLOR));
                    Block block = new Block(new Vector2(startPos.x() +
                            (j * Block.SIZE), startPos.y() + (i * Block.SIZE)),
                            new Vector2(Block.SIZE, Block.SIZE), rect);
                    block.setTag("cloud");
                    block.physics().preventIntersectionsFromDirection(null);
                    block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    block.setVelocity(new Vector2(CLOUD_VELOCITY, 0));
                    cloudBlocks.add(block);
                }
            }
        }
        return cloudBlocks;
    }

    /**
     * Checks if the cloud has moved beyond the right boundary of the window.
     *
     * @return True if the cloud is out of the right boundary, false otherwise.
     */
    private boolean cloudOutOfRightBound() {
        return cloudBlocks.get(0).getTopLeftCorner().x() >= windowDimensions.x() + cloud.size() * Block.SIZE;
    }

    /**
     * Checks if the cloud has moved beyond the left boundary of the window.
     *
     * @return True if the cloud is out of the left boundary, false otherwise.
     */
    private boolean cloudOutOfLeftBound() {
        return cloudBlocks.get(20).getTopLeftCorner().x() <= -cloud.size() * Block.SIZE;
    }


    /**
     * Changes the horizontal movement direction of the cloud blocks.
     *
     * @param bound A string indicating the bound hit; "RIGHT" to move left, anything else to move right.
     */
    private void changeCloudDir(String bound){

        if (bound.equals("RIGHT")){
            for (Block b : cloudBlocks) {
                b.setVelocity(new Vector2(CLOUD_VELOCITY, 0).multX(-1));
            }
            return;
        }
        for (Block b : cloudBlocks) {
            b.setVelocity(new Vector2(CLOUD_VELOCITY, 0));
        }
    }

    /**
     * Creates rain drops falling from the cloud blocks with some randomness.
     * Rain drops fall downward with gravity applied and fade out before removal.
     *
     * @return A list of rain drop blocks created during this update.
     */
    public List<Block> rain(){
    List<Block> rainBlocks = new ArrayList<>();
        for (Block block : cloudBlocks) {

            if(random.nextInt(10) > 2)
                continue;

            RectangleRenderable rect =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_RAIN_DROP_COLOR));
            Block rainDrop = new Block(block.getTopLeftCorner(),
                    new Vector2(RAIN_DROP_SIZE, RAIN_DROP_SIZE),
                    rect);
            rainDrop.transform().setAccelerationY(GRAVITY);
            rainDrop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
            rainDrop.setTag(RAIN_TAG);

            new Transition<Float>(
                    rainDrop,
                    (Float f) -> rainDrop.renderer().setOpaqueness(f),
                    1f,
                    0f,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    2,
                    Transition.TransitionType.TRANSITION_ONCE,
                    () -> rainDrop.setTag(PepseGameManager.OBJECT_REMOVE_TAG)
            );
            rainBlocks.add(rainDrop);
        }
        return rainBlocks;
    }


    /**
     * Updates the cloud every frame.
     * Moves the cloud and checks boundaries to reverse direction if needed.
     *
     * @param deltaTime Time elapsed since the last update call (in seconds).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (cloudOutOfRightBound())
            changeCloudDir("RIGHT");
        if (cloudOutOfLeftBound())
            changeCloudDir("LEFT");
    }
}
