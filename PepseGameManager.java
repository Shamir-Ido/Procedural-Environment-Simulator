package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The main game manager class for the Pepse game.
 * Responsible for initializing game elements such as terrain, flora, avatar, weather,
 * UI, and managing chunks loading/unloading as the avatar moves.
 */
public class PepseGameManager extends GameManager {

    /** length of the game's cycle */
    public static final float NIGHT_CYCLE_LENGTH = 30f;
    private static final int RAIN_LAYER = -197;
    private static final int CLOUD_LAYER = -196;
    private static final int HALO_LAYER = -199;
    private static final int INITIAL_CHUNK_RADIUS = 1;

    /**A tag for GameObjects that need to be removed in the next update iteration. */
    public static final String OBJECT_REMOVE_TAG = "toRemove";

    private  WindowController windowController;
    private  Terrain terrain;
    private  Flora flora;

    private Cloud cloud;
    private final Map<Integer, Chunk> chunkMap = new HashMap<>();
    private int chunkLength;
    private Avatar avatar;
    private int currentChunkId;

    /**
     * Initializes the game, including terrain, flora, avatar, weather, UI, and initial chunks.
     *
     * @param imageReader      Used to read image assets.
     * @param soundReader      Used to read sound assets.
     * @param inputListener    Used to listen to user input.
     * @param windowController Used to interact with the game window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowController = windowController;
        this.chunkLength = (int) windowController.getWindowDimensions().x();
        this.currentChunkId = 0;
        terrain = new Terrain(windowController.getWindowDimensions(), 0);
        flora = new Flora(terrain);

        initializeAvatar(inputListener, imageReader);
        initializeWeather(windowController, avatar);
        initializeUI();

        initializeStarterChunks();
    }


    /**
     * Loads the initial chunks surrounding the starting chunk.
     */
    private void initializeStarterChunks(){
        for (int i=-1; i <= INITIAL_CHUNK_RADIUS; i++){
            Chunk chunk = initializeChunk(i * chunkLength, i);
            chunkMap.put(chunk.getChunkId(), chunk);
        }
    }

    /**
     * Creates a new chunk with terrain blocks and trees, and loads it.
     *
     * @param startX  The starting x-coordinate of the chunk.
     * @param chunkId The id number of the chunk.
     * @return The newly created and loaded chunk.
     */
    private Chunk initializeChunk(int startX, int chunkId){
        List<Block> blocks = terrain.createInRange(startX, startX + chunkLength);
        List<Tree> trees = flora.createInRange(startX, startX + chunkLength);
        Chunk chunk = new Chunk(gameObjects(), blocks, trees, chunkId);
        chunk.loadChunk();
        return chunk;
    }

    /**
     * Initializes the player's avatar at the center top of the ground and sets the camera to follow it.
     *
     * @param inputListener The user input listener to control the avatar.
     * @param imageReader   Used to read avatar images.
     */
    private void initializeAvatar(UserInputListener inputListener, ImageReader imageReader) {

        Vector2 initialAvatarPosition = new Vector2(windowController.getWindowDimensions().x() / 2,
                terrain.groundHeightAt(windowController.getWindowDimensions().x() / 2) - Block.SIZE);

        avatar = new Avatar(initialAvatarPosition, inputListener, imageReader);
        setCamera(new Camera(avatar,
                Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        gameObjects().addGameObject(avatar, Layer.DEFAULT);

    }

    /**
     * Calculates the current chunk ID in which the avatar is located.
     *
     * @return The chunk ID based on avatar's x-coordinate.
     */
    private int currentAvatarChunk() {
        return (int) Math.floor(avatar.getCenter().x() / chunkLength);
    }

    /**
     * Initializes the UI elements such as the energy bar display.
     */
    private void initializeUI(){
        EnergyBarDisplay energyBar = createEnergyDisplay();
        gameObjects().addGameObject(energyBar, Layer.UI);
    }

    /**
     * Creates the energy display UI element showing the avatar's current energy.
     *
     * @return An EnergyBarDisplay instance.
     */
    private EnergyBarDisplay createEnergyDisplay(){
        TextRenderable textRenderable =
                new TextRenderable(String.format(EnergyBarDisplay.ENERGY_TEXT, Avatar.getEnergy()));
        EnergyBarDisplay energyBarDisplay = new EnergyBarDisplay(
                        new Vector2(10, 10),
                        new Vector2(10, 20),
                        textRenderable,
                        Avatar::getEnergy);
        energyBarDisplay.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        return energyBarDisplay;
    }

    /**
     * Initializes weather-related game objects such as night, sky, sun, sun halo, and clouds.
     *
     * @param windowController The window controller for window dimensions.
     * @param avatar           The player's avatar.
     */
    private void initializeWeather(WindowController windowController, Avatar avatar) {
        GameObject night = Night.create(windowController.getWindowDimensions(), NIGHT_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        GameObject sun = Sun.create(windowController.getWindowDimensions(), NIGHT_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        GameObject sunHalo = SunHalo.create(sun);
        sunHalo.addComponent((float f) -> sunHalo.setCenter(sun.getCenter()));
        gameObjects().addGameObject(sunHalo, HALO_LAYER);

        cloud = new Cloud(windowController.getWindowDimensions());
        List<Block> cloudBlocks = cloud.create();
        gameObjects().addGameObject(cloud, CLOUD_LAYER);

        for(Block block : cloudBlocks) {
            gameObjects().addGameObject(block, CLOUD_LAYER);
        }
    }

    /**
     * Loads and unloads chunks based on the avatar's current chunk position.
     * Loads the next chunk ahead and unloads chunks far behind or ahead accordingly.
     *
     * @param newChunkId The new chunk ID where the avatar currently is.
     */
    private void updateChunks(int newChunkId){
        if(newChunkId > currentChunkId){
            chunkMap.get(currentChunkId - 1).unloadChunk();
            currentChunkId = newChunkId;
            int nextChunkId = newChunkId + 1;
            if (chunkMap.containsKey(nextChunkId))
                chunkMap.get(nextChunkId).loadChunk();
            else
                chunkMap.put(nextChunkId, initializeChunk(nextChunkId * chunkLength, nextChunkId ));
        }

        if(newChunkId < currentChunkId){
            chunkMap.get(currentChunkId + 1).unloadChunk();
            currentChunkId = newChunkId;
            int nextChunkId = newChunkId - 1;

            if (chunkMap.containsKey(nextChunkId))
                chunkMap.get(nextChunkId).loadChunk();
            else
                chunkMap.put(nextChunkId, initializeChunk(nextChunkId * chunkLength, nextChunkId ));
        }
    }

    /**
     * Called every frame to update the game state.
     * Updates chunks based on avatar position,
     * triggers rain if avatar jumped,
     * and removes game objects tagged for removal.
     *
     * @param deltaTime Time elapsed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateChunks(currentAvatarChunk());

        if (avatar.didAvatarJump()){
            List<Block> rainDrops = cloud.rain();
            for (Block block : rainDrops) {
                gameObjects().addGameObject(block, RAIN_LAYER);
            }
        }

        for (GameObject go : gameObjects()){
            if (go.getTag().equals(OBJECT_REMOVE_TAG))
                gameObjects().removeGameObject(go);
        }
    }

    /**
     * Main entry point to run the Pepse game.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
