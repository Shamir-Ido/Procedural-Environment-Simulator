package pepse;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import pepse.world.Block;
import pepse.world.trees.Fruit;
import pepse.world.trees.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a chunk of the game world, grouping ground blocks and flora (trees and their parts).
 * Manages loading and unloading of its contained game objects into the game's object collection.
 */
public class Chunk {
    private static final int LEAF_LAYER = -101;
    private final int chunkId;
    private final GameObjectCollection gameObjects;
    List <GameObject> chunkObjects = new ArrayList<>();

    /**
     * Constructs a Chunk instance with specified game objects.
     *
     * @param gameObjects  The global game object collection managing all game objects.
     * @param groundBlocks The ground blocks that belong to this chunk.
     * @param flora        The trees (and their components) present in this chunk.
     * @param chunkId      The unique identifier of this chunk.
     */
    public Chunk(GameObjectCollection gameObjects, List<Block> groundBlocks, List<Tree> flora, int chunkId) {

        this.chunkId = chunkId;
        this.gameObjects = gameObjects;
        for (Tree tree : flora) {
            List<Block> trunks = tree.getTrunkBlocks();
            List<Block> leafs = tree.getLeafBlocks();
            List<Fruit> fruits = tree.getFruits();

            chunkObjects.addAll(trunks);
            chunkObjects.addAll(leafs);
            chunkObjects.addAll(fruits);
        }

        chunkObjects.addAll(groundBlocks);
    }

    /**
     * Adds all the game objects contained in this chunk to the global game object collection.
     * Fruits are added to the default layer, while other objects are added to the static objects layer.
     */
    public void loadChunk() {
        for (GameObject gameObject : chunkObjects) {
            if (gameObject.getTag().equals("fruit"))
                gameObjects.addGameObject(gameObject, Layer.DEFAULT);
            if (gameObject.getTag().equals("leaf"))
                gameObjects.addGameObject(gameObject, LEAF_LAYER);
            else
                gameObjects.addGameObject(gameObject, Layer.STATIC_OBJECTS);
        }
    }


    /**
     * Returns the unique identifier of this chunk.
     *
     * @return The chunk's ID.
     */
    public int getChunkId() {
        return chunkId;
    }

    /**
     * Removes all the game objects of this chunk from the global game object collection.
     * Fruits are removed from the default layer,
     * while other objects are removed from the static objects layer.
     */
    public void unloadChunk() {
        for (GameObject gameObject : chunkObjects) {
            if (gameObject.getTag().equals("fruit"))
                gameObjects.removeGameObject(gameObject, Layer.DEFAULT);
            if (gameObject.getTag().equals("leaf"))
                gameObjects.removeGameObject(gameObject, LEAF_LAYER);
            else
                gameObjects.removeGameObject(gameObject, Layer.STATIC_OBJECTS);
        }
    }
}
