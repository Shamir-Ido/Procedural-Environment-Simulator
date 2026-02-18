package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

/**
 * A controllable player avatar in the game world.
 * <p>
 * The avatar can move left/right, jump, and collect fruit. The avatar has an energy level
 * that affects movement and regenerates when idle. Animations for idle, running, and jumping
 * are provided.
 */
public class Avatar extends GameObject {

    private static UserInputListener inputListener;
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static float energy = 100;
    private static boolean didJump = false;

    private static AnimationRenderable idleAnimation;
    private static AnimationRenderable runAnimation;
    private static AnimationRenderable jumpAnimation;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param inputListener
     * @param imageReader
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {

        super(topLeftCorner, Vector2.ONES.mult(50), imageReader.readImage("./assets/idle_0.png",
                false));
        Avatar.inputListener = inputListener;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        initAnimations(imageReader);
        this.setTag("avatar");
    }

    /**
     * Initializes the avatar's idle, run, and jump animations.
     *
     * @param imageReader The image reader used to load animation frames.
     */
    private void initAnimations(ImageReader imageReader) {
        idleAnimation = new AnimationRenderable(new String[]
                        {"./assets/idle_0.png",
                        "./assets/idle_1.png",
                        "./assets/idle_2.png",
                        "./assets/idle_3.png"},
                imageReader, false, 0.3);

        runAnimation = new AnimationRenderable(new String[]
                        {"./assets/run_0.png",
                        "./assets/run_1.png",
                        "./assets/run_2.png",
                        "./assets/run_3.png",
                        "./assets/run_4.png"},
                imageReader, false, 0.1);

        jumpAnimation = new AnimationRenderable(new String[]
                        {"./assets/jump_0.png",
                        "./assets/jump_1.png",
                        "./assets/jump_2.png",
                        "./assets/jump_3.png"},
                imageReader, false, 0.1);
    }

    /**
     * Returns the current energy level of the avatar.
     *
     * @return The avatar's energy level (0-100).
     */
    public static int getEnergy(){
        return (int) Avatar.energy;
    }

    /**
     * Returns whether the avatar performed a jump this frame.
     * <p>
     * The flag is reset to false after this method returns true.
     *
     * @return true if the avatar jumped this frame, false otherwise.
     */
    public boolean didAvatarJump(){
        if(didJump){
            didJump = false;
            return true;
        }
        return false;
    }


    /**
     * Changes the avatar's energy by the given delta, clamping the value between 0 and 100.
     *
     * @param delta The amount to change the energy (can be positive or negative).
     */
    private static void changeEnergy(float delta) {
        float newEnergy = Math.max(0, Math.min(100, energy + delta));
        if (newEnergy != energy) {
            energy = newEnergy;
        }
    }

    /**
     * Updates the avatar's behavior each frame based on user input and energy level.
     * <p>
     * The avatar can move left/right and jump if energy allows. Energy regenerates when idle.
     *
     * @param deltaTime Time elapsed since the last frame (in seconds).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean keyPressed = false;

        float xVel = 0;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy >= 0.5f ){
            xVel -= VELOCITY_X;
            keyPressed = true;
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= 0.5f){
            xVel += VELOCITY_X;
            keyPressed = true;
        }
        if(xVel != 0){
            changeEnergy(-0.5f); // Run mode decrement of energy
            if(xVel < 0){
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(true);
            }
            else{
                renderer().setRenderable(runAnimation);
                renderer().setIsFlippedHorizontally(false);
            }

        }
        transform().setVelocityX(xVel);

        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0 && energy >= 10){
            transform().setVelocityY(VELOCITY_Y);
            changeEnergy(-10); // Jump mode decrement of energy
            renderer().setRenderable(jumpAnimation); // Jump animation
            didJump = true;
            return;
        }

        // Idle mode increment of energy
        if(!keyPressed && getVelocity().y() == 0){
            changeEnergy(1);
            renderer().setRenderable(idleAnimation);
        }
    }

    /**
     * Handles collision events with other game objects.
     * <p>
     * - Colliding with "ground" stops vertical movement.
     * - Colliding with "fruit" increases energy and disables the fruit.
     *
     * @param other     The other GameObject involved in the collision.
     * @param collision The Collision object containing collision details.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);

        if (other.getTag().equals("ground")){
            this.transform().setVelocityY(0);
        }

        if (other.getTag().equals("fruit")){
            other.setTag("disabledFruit");
            changeEnergy(10);
        }
    }
}
