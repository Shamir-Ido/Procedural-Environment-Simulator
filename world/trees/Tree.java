package pepse.world.trees;

import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * A class representing a procedurally generated tree in the game world.
 * <p>
 * A tree consists of a trunk, a cluster of animated leaves, and a collection of collectible fruits.
 * The tree's trunk height and leaf/fruit placement are randomly generated to provide variety.
 */
public class Tree {

    //Fruit color options:
    private static final Color[] fruitColors = {Color.RED, Color.YELLOW, Color.PINK, Color.ORANGE};



    private final int trunkHeight;
    private final Vector2 treePos;
    private final Random rand = new Random();

    private final List<Block> trunkBlocks = new ArrayList<>();
    private final List<Block> leafBlocks = new ArrayList<>();
    private final List<Fruit> fruits = new ArrayList<>();

    private static final String LEAF_TAG = "leaf";
    private static final String FRUIT_TAG = "fruit";
    private static final String TRUNK_TAG = "treeTrunk";
    private static final float INIITAL_LEAF_TRANSITION_VALUE = -5f;
    private static final float FINAL_LEAF_TRANSITION_VALUE = 5f;
    private static final float TRANSITION_TIME = 2f;
    private static final Color TRUNK_COLOR = new Color(100, 50, 20);
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final int RANDOMNESS_COEF = 20;
    private static final int LEAF_SPARSITY_THRESHOLD = 5;
    private static final int LOW_TREE_HEIGHT = 3;
    private static final int HIGH_TREE_HEIGHT = 5;
    private static final int LOW_TREE_CANOPY_WIDTH = 2;
    private static final int HIGH_TREE_CANOPY_WIDTH = 3;


    /**
     * Constructs a new Tree instance at the specified position.
     * <p>
     * The tree's trunk height, leaf layout, and fruit placement are randomized.
     *
     * @param position The base position of the tree (bottom-left corner of the trunk).
     */
    public Tree(Vector2 position){
        this.treePos = position;
        this.trunkHeight = rand.nextBoolean() ? LOW_TREE_HEIGHT : HIGH_TREE_HEIGHT;
        generateTrunk();
        generateLeavesAndFruit();
    }

    /**
     * Generates and places the tree's leaves and fruit objects.
     * <p>
     * Leaves are animated using transitions to sway and stretch.
     * Fruits are placed randomly within the leaf cluster.
     */
    private void generateLeavesAndFruit() {
        Vector2 startPos = new Vector2(treePos.x() -
            (trunkHeight == LOW_TREE_HEIGHT ? LOW_TREE_CANOPY_WIDTH : HIGH_TREE_CANOPY_WIDTH) * Block.SIZE,
            treePos.y() - (2 * trunkHeight + 1) * Block.SIZE);
        for (int i = 0; i < trunkHeight + 2; i++) {
            for (int j = 0; j < trunkHeight + 2; j++) {

                if (rand.nextInt(RANDOMNESS_COEF) == 0){
                    generateFruit(
                            new Vector2(startPos.x() + i * Block.SIZE, startPos.y() + (j * Block.SIZE)),
                            fruitColors[rand.nextInt(fruitColors.length)]);
                }

                if(rand.nextInt(RANDOMNESS_COEF) > LEAF_SPARSITY_THRESHOLD || j == trunkHeight + 1){
                    RectangleRenderable leafRenderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR));

                    Block leaf = new Block(new Vector2(startPos.x() + i * Block.SIZE,
                            startPos.y() + (j * Block.SIZE)),
                            new Vector2(Block.SIZE, Block.SIZE), leafRenderable);
                    leaf.setTag(LEAF_TAG);
                    leaf.physics().preventIntersectionsFromDirection(null);

                    new ScheduledTask(
                            leaf,//Obj
                            rand.nextInt(RANDOMNESS_COEF) / (float) RANDOMNESS_COEF,

                            false,
                            () -> initiateLeafTransitions(leaf)
                    );
                    this.leafBlocks.add(leaf);
                }
            }
        }

    }

    /**
     * Generates a fruit object at the specified position with the given color.
     *
     * @param position The position to place the fruit.
     * @param color    The color of the fruit.
     */
    private void generateFruit(Vector2 position, Color color) {
        Fruit fruit = new Fruit(position, color);
        fruit.setTag(FRUIT_TAG);
        this.fruits.add(fruit);
    }

    /**
     * Adds simple animation transitions to the specified leaf block.
     * <p>
     * The leaf will sway left and right and slightly stretch vertically.
     *
     * @param leaf The leaf block to animate.
     */
    private void initiateLeafTransitions(Block leaf) {
        new Transition<Float>(
                leaf,
                (Float angle) ->
                        leaf.renderer().setRenderableAngle(angle),
                INIITAL_LEAF_TRANSITION_VALUE,
                FINAL_LEAF_TRANSITION_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        new Transition<Float>(
                leaf,
                (Float angle) ->
                        leaf.setDimensions(new Vector2(Block.SIZE, Block.SIZE + angle)),
                0f,
                FINAL_LEAF_TRANSITION_VALUE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /**
     * Generates and places the blocks that make up the tree's trunk.
     */
    private void generateTrunk() {
        RectangleRenderable trunk = new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR));

        for (int i = 0; i < trunkHeight; i++) {
            Block block = new Block(new Vector2(treePos.x(),
                    treePos.y() - (i * Block.SIZE)),
                    new Vector2(Block.SIZE, Block.SIZE), trunk);
            block.setTag(TRUNK_TAG);
            this.trunkBlocks.add(block);
        }
    }

    /**
     * Returns the list of blocks composing the tree's trunk.
     *
     * @return List of trunk {@link Block} objects.
     */
    public List<Block> getTrunkBlocks(){
        return trunkBlocks;
    }

    /**
     * Returns the list of leaf blocks attached to the tree.
     *
     * @return List of leaf {@link Block} objects.
     */
    public List<Block> getLeafBlocks(){
        return leafBlocks;
    }

    /**
     * Returns the list of fruit objects attached to the tree.
     *
     * @return List of {@link Fruit} objects.
     */
    public List<Fruit> getFruits(){
        return fruits;
    }

}
