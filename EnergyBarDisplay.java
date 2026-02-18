package pepse;

import danogl.GameObject;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.util.function.Supplier;

/**
 * A display GameObject that shows the player's energy as a textual energy bar.
 * The displayed energy percentage updates automatically whenever the energy value changes.
 */
public class EnergyBarDisplay extends GameObject {

    /** Text for the energy meter */
    public static final String ENERGY_TEXT = "Energy: %d%% ";
    private final Supplier<Integer> energySupplier;
    private final TextRenderable textRenderable;
    private int energy;

    /**
     * Constructs a new EnergyBarDisplay object.
     *
     * @param topLeftCorner Position of the object in window coordinates (pixels).
     *                      (0,0) corresponds to the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The TextRenderable that displays the energy string.
     * @param Supplier      A Supplier function providing the current energy value as an Integer.
     */
    public EnergyBarDisplay(Vector2 topLeftCorner,
                            Vector2 dimensions,
                            TextRenderable renderable,
                            Supplier<Integer> Supplier) {
        super(topLeftCorner, dimensions, renderable);
        textRenderable = renderable;
        energySupplier = Supplier;
        energy = energySupplier.get();
    }

    /**
     * Called internally when the energy value changes to update the displayed text.
     */
    private void onEnergyChange() {
        textRenderable.setString(String.format(ENERGY_TEXT, energySupplier.get()));
    }

    /**
     * Called once per frame to update the energy display if the energy value has changed.
     *
     * @param deltaTime Time elapsed since last frame in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        int newEnergy = energySupplier.get();
        if (newEnergy != energy) {
            energy = newEnergy;
            onEnergyChange();
        }
    }
}
