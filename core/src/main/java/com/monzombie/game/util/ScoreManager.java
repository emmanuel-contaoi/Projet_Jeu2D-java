package com.monzombie.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Stores best level completion times in a JSON file and exposes query methods.
 */
public class ScoreManager {

    private static final String SAVE_FILE = "scores.json";

    private final Array<ScoreEntry> scores = new Array<>();
    private final Json json;

    /**
     * Loads existing scores from disk or starts with an empty list.
     */
    public ScoreManager() {
        json = new Json();
        json.setIgnoreUnknownFields(true);
        json.setOutputType(JsonWriter.OutputType.json);
        load();
    }

    /**
     * Records a new score entry with the provided level and completion time.
     *
     * @param level level number associated with the score
     * @param timeSeconds completion time in seconds
     */
    public void addScore(int level, float timeSeconds) {
        ScoreEntry entry = new ScoreEntry();
        entry.level = level;
        entry.timeSeconds = Math.max(0f, timeSeconds);
        entry.timestamp = TimeUtils.millis();
        scores.add(entry);
        save();
    }

    /**
     * Returns the fastest completion times sorted in ascending order.
     *
     * @param max maximum number of entries to return
     * @return copy of the best scores up to the requested amount
     */
    public Array<ScoreEntry> getTopScores(int max) {
        Array<ScoreEntry> copy = new Array<>(scores);
        copy.sort((a, b) -> Float.compare(a.timeSeconds, b.timeSeconds));
        if (copy.size > max) {
            Array<ScoreEntry> trimmed = new Array<>();
            for (int i = 0; i < Math.min(max, copy.size); i++) {
                trimmed.add(copy.get(i));
            }
            return trimmed;
        }
        return copy;
    }

    private void load() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (!file.exists()) return;
            ScoreEntry[] loaded = json.fromJson(ScoreEntry[].class, file.readString("UTF-8"));
            if (loaded != null) {
                scores.clear();
                for (ScoreEntry entry : loaded) {
                    if (entry != null) scores.add(entry);
                }
            }
        } catch (Exception ex) {
            Gdx.app.error("ScoreManager", "Impossible de charger " + SAVE_FILE, ex);
        }
    }

    private void save() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            ScoreEntry[] arr = new ScoreEntry[scores.size];
            for (int i = 0; i < scores.size; i++) {
                arr[i] = scores.get(i);
            }
            String data = json.prettyPrint(arr);
            file.writeString(data, false, "UTF-8");
        } catch (Exception ex) {
            Gdx.app.error("ScoreManager", "Impossible d'enregistrer " + SAVE_FILE, ex);
        }
    }

    /**
     * Simple data container persisted as JSON on disk.
     */
    public static class ScoreEntry {
        public int level;
        public float timeSeconds;
        public long timestamp;
    }
}
