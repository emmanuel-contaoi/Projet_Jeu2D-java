package TestForMain;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.monzombie.game.assets.Assets;
import com.monzombie.game.screens.MenuScreen;
import com.monzombie.game.util.ScoreManager;
import com.monzombie.game.util.SettingsManager;

/**
 * Core game object that owns global resources, tracks progression,
 * and delegates to the active LibGDX screen.
 */
public class MainGame extends Game {

    public SpriteBatch batch;
    public Assets assets;
    public String selectedHero = "Hugo";
    public ScoreManager scoreManager;
    public SettingsManager settings;
    private final boolean[] levelCompleted = new boolean[4]; 
    private int highestLevelUnlocked = 1;

    /**
     * Initializes shared resources and opens the main menu.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();
        settings = new SettingsManager();
        settings.applyResolution();
        scoreManager = new ScoreManager();

        setScreen(new MenuScreen(this));
    }

    /**
     * Indicates if the player may enter the requested level.
     *
     * @param levelNumber level id starting at 1
     * @return true when the level index is no higher than the highest unlocked one
     */
    public boolean isLevelUnlocked(int levelNumber) {
        return levelNumber <= highestLevelUnlocked;
    }

    /**
     * Checks if a level has been cleared at least once.
     *
     * @param levelNumber level id starting at 1
     * @return true when the requested level number is within bounds and marked finished
     */
    public boolean isLevelCompleted(int levelNumber) {
        if (levelNumber < 1 || levelNumber >= levelCompleted.length) return false;
        return levelCompleted[levelNumber];
    }

    /**
     * Marks a level as completed and unlocks the following level when possible.
     *
     * @param levelNumber completed level id starting at 1
     */
    public void markLevelFinished(int levelNumber) {
        if (levelNumber < 1 || levelNumber >= levelCompleted.length) return;
        levelCompleted[levelNumber] = true;

        int nextLevel = Math.min(levelCompleted.length - 1, levelNumber + 1);
        if (nextLevel > highestLevelUnlocked) {
            highestLevelUnlocked = nextLevel;
        }
    }

    /**
     * Disposes every shared resource when the application shuts down.
     */
    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        assets.dispose();
    }
}
