package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.NoiseGenerator;
import pepse.util.ColorSupplier;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the terrain in the game world.
 * Generates a procedural ground shape using noise, and constructs blocks to form the terrain.
 */
public class Terrain {

    private final float groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final int NOISE_SMOOTHNESS = 7;
    private static final String GROUND_TAG = "ground";
    private final NoiseGenerator noiseGenerator;

    /**
     * Constructs a Terrain instance based on the window dimensions and a noise seed.
     *
     * @param windowDims The dimensions of the game window.
     * @param seed       The seed to initialize the noise generator for terrain variation.
     */
    public Terrain(Vector2 windowDims, int seed) {
        this.groundHeightAtX0 = windowDims.y() * (2f/3f);
        this.noiseGenerator = new NoiseGenerator(seed, (int) groundHeightAtX0);
    }

    /**
     * Returns the ground height at a specific x-coordinate.
     * The height is calculated by adding noise-based variation to the base ground height.
     *
     * @param x The x-coordinate to query.
     * @return The y-coordinate of the ground surface at x.
     */
    public float groundHeightAt(float x) {
        return (float) noiseGenerator.noise(x, Block.SIZE * NOISE_SMOOTHNESS) + this.groundHeightAtX0;
    }

    /**
     * Creates terrain blocks within a horizontal range from minX to maxX.
     * For each horizontal block position, stacks blocks vertically downward to form terrain depth.
     *
     * @param minX The minimum x-coordinate (inclusive) to start creating terrain blocks.
     * @param maxX The maximum x-coordinate (inclusive) to end terrain creation.
     * @return A list of Blocks representing the terrain in the specified range.
     */
    public List<Block> createInRange(int minX, int maxX){

        minX = minX - (minX % Block.SIZE);
        maxX = (maxX + Block.SIZE) - (maxX % Block.SIZE);

        float blockAmount = (float) (maxX - minX) / Block.SIZE;
        List<Block> blocks = new ArrayList<>();

        for (int i = 0; i < blockAmount; i++) {
            float initialHeight = (float) (Math.floor(groundHeightAt((float)minX +
                    i * Block.SIZE)  / Block.SIZE) * Block.SIZE);
            for (int j = 0; j < TERRAIN_DEPTH; j++){
                RectangleRenderable blockRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(new Vector2((float)minX + (i * Block.SIZE),
                                    initialHeight + (j * Block.SIZE)),
                                       new Vector2(Block.SIZE, Block.SIZE), blockRenderable);
                block.setTag(GROUND_TAG);
                blocks.add(block);
            }
        }
        return blocks;
    }

}
