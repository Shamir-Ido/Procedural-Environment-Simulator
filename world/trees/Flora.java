package pepse.world.trees;


import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Utility class responsible for generating trees (flora) within a given range of the world.
 * <p>
 * This class uses random placement to create natural-looking tree distributions
 * on top of the terrain.
 */
public class Flora {

    private final Terrain terrain;
    private static final int RANDOMNESS_COEF = 20;

    /**
     * Constructs a Flora instance.
     *
     * @param terrain The {@link Terrain} object used to calculate the ground height for tree placement.
     */
    public Flora(Terrain terrain) {
        this.terrain = terrain;
    }

    /**
     * Generates a list of trees within the specified horizontal range.
     * <p>
     * The range is aligned to the block grid, and trees are placed randomly along this range.
     * Approximately 1 out of every 15 positions will contain a tree.
     *
     * @param minX The minimum x-coordinate of the range (inclusive).
     * @param maxX The maximum x-coordinate of the range (inclusive).
     * @return A list of {@link Tree} objects positioned on top of the terrain within the given range.
     */
    public List<Tree> createInRange(int minX, int maxX){

        minX = minX - (minX % Block.SIZE);
        maxX = (maxX + Block.SIZE) - (maxX % Block.SIZE);
        float blockAmount = (float) (maxX - minX) / Block.SIZE;

        List<Tree> trees = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i <= blockAmount; i++){
            if(rand.nextInt(RANDOMNESS_COEF) == 0){
                float x = minX + (i * Block.SIZE);
                Tree test = new Tree(new Vector2(x, terrain.groundHeightAt(x) - Block.SIZE));
                trees.add(test);
            }
        }
        return trees;
    }
}
