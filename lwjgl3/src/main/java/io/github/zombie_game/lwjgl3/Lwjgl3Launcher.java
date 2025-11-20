package io.github.zombie_game.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import TestForMain.MainGame;


/**
 * Desktop entry point that configures LWJGL3 and launches the game.
 */
public class Lwjgl3Launcher {
    /**
     * Starts the application, relaunching on macOS when needed.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return; 
        createApplication();
    }

    /**
     * Creates the LibGDX application with the default configuration.
     */
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new MainGame(), getDefaultConfiguration());
    }

    /**
     * Builds the render configuration tailored for this project.
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("zombie_game");
        
        
        configuration.useVsync(true);
        
        
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        
        
        

        configuration.setWindowedMode(640, 480);
        
        
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");

        
        
        
        
        
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20, 0, 0);

        return configuration;
    }
}
