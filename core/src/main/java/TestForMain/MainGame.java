package TestForMain;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.monzombie.game.assets.Assets;
import com.monzombie.game.screens.MenuScreen;
import com.monzombie.game.util.ScoreManager;
import com.monzombie.game.util.SettingsManager;

public class MainGame extends Game {

    public SpriteBatch batch;
    public Assets assets;
    public String selectedHero = "Hugo";
    public ScoreManager scoreManager;
    public SettingsManager settings;
    private final boolean[] levelCompleted = new boolean[4]; 
    private int highestLevelUnlocked = 1;

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

    public boolean isLevelUnlocked(int levelNumber) {
        return levelNumber <= highestLevelUnlocked;
    }

    public boolean isLevelCompleted(int levelNumber) {
        if (levelNumber < 1 || levelNumber >= levelCompleted.length) return false;
        return levelCompleted[levelNumber];
    }

    public void markLevelFinished(int levelNumber) {
        if (levelNumber < 1 || levelNumber >= levelCompleted.length) return;
        levelCompleted[levelNumber] = true;

        int nextLevel = Math.min(levelCompleted.length - 1, levelNumber + 1);
        if (nextLevel > highestLevelUnlocked) {
            highestLevelUnlocked = nextLevel;
        }
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        assets.dispose();
    }
}
