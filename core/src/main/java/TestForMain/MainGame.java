package TestForMain;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.monzombie.game.assets.Assets;
import com.monzombie.game.screens.MenuScreen;

public class MainGame extends Game {

    public SpriteBatch batch;
    public Assets assets;
    public String selectedHero = "Hugo";

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) getScreen().dispose();
        batch.dispose();
        assets.dispose();
    }
}
