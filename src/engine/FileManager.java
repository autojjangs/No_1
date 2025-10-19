package engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import engine.DrawManager.SpriteType;

/**
 * Manages files used in the application.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class FileManager {
    /**
     * Singleton instance of the class.
     */
    private static FileManager instance;
    /**
     * Application logger.
     */
    private static Logger logger;

    /**
     * private constructor.
     */
    private FileManager() {
        logger = Core.getLogger();
    }

    /**
     * Returns shared instance of FileManager.
     *
     * @return Shared instance of FileManager.
     */
    protected static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    /**
     * Loads sprites from disk.
     *
     * @param spriteMap
     *            Mapping of sprite type and empty boolean matrix that will
     *            contain the image.
     * @throws IOException
     *             In case of loading problems.
     */
    public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap)
            throws IOException {
        InputStream inputStream = null;

        try {
            inputStream = DrawManager.class.getClassLoader()
                    .getResourceAsStream("graphics");
            char c;

            // Sprite loading.
            for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap
                    .entrySet()) {
                for (int i = 0; i < sprite.getValue().length; i++)
                    for (int j = 0; j < sprite.getValue()[i].length; j++) {
                        do
                            c = (char) inputStream.read();
                        while (c != '0' && c != '1');

                        if (c == '1')
                            sprite.getValue()[i][j] = true;
                        else
                            sprite.getValue()[i][j] = false;
                    }
                logger.fine("Sprite " + sprite.getKey() + " loaded.");
            }
            if (inputStream != null)
                inputStream.close();
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Loads a font of a given size.
     *
     * @param size
     *            Point size of the font.
     * @return New font.
     * @throws IOException
     *             In case of loading problems.
     * @throws FontFormatException
     *             In case of incorrect font format.
     */
    public Font loadFont(final float size) throws IOException,
            FontFormatException {
        InputStream inputStream = null;
        Font font;

        try {
            // Font loading.
            inputStream = FileManager.class.getClassLoader()
                    .getResourceAsStream("font.ttf");
            font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
                    size);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return font;
    }

    /**
     * Returns the filepath
     *
     * @param fileName
     *      file to get path
     * @return full file path
     * @throws IOException
     *      In case of loading problems
     * */
    private static String getFilePath(String fileName) throws IOException {
        String filePath = System.getProperty("user.dir");
        filePath += File.separator + "res" + File.separator + fileName;
        return filePath;
    }

    /**
     * Returns the application default scores if there is no user high scores
     * file.
     *
     * @return Default high scores.
     * @throws IOException
     *             In case of loading problems.
     */
    private List<Score> loadDefaultHighScores() throws IOException {
        List<Score> highScores = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader reader;

        try {
            inputStream = FileManager.class.getClassLoader().getResourceAsStream("1Pscores.csv");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // except first line
            reader.readLine();
            String input;
            while ((input = reader.readLine()) != null) {
                String[] pair = input.split(",");
                String name = pair[0], score = pair[1];
                String mode = pair[2];
                Score highScore = new Score(name, Integer.parseInt(score), mode);
                highScores.add(highScore);
            }
        } finally {
            if (inputStream != null)
                inputStream.close();
        }

        return highScores;
    }

    /**
     * Loads high scores from file, and returns a sorted list of pairs score -
     * value.
     * @param mode
     *      get game mode 1P/2P.
     * @return Sorted list of scores - players.
     * @throws IOException
     *             In case of loading problems.
     */
    public List<Score> loadHighScores(String mode) throws IOException {
        List<Score> highScores = new ArrayList<>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String scoresPath = getFilePath(mode+"scores.csv");

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            logger.info("Loading user high scores.");
            // except first line
            bufferedReader.readLine();
            String input;
            while ((input = bufferedReader.readLine()) != null) {
                String[] pair = input.split(",");
                String name = pair[0], score = pair[1];
                Score highScore = new Score(name, Integer.parseInt(score), mode);
                highScores.add(highScore);
            }
        } catch (FileNotFoundException e) {
            // loads default if there's no user scores.
            logger.info("Loading default high scores.");
            highScores = loadDefaultHighScores();
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }

        Collections.sort(highScores);
        return highScores;
    }

    /**
     * Saves user high scores to disk.
     *
     * @param highScores
     *            High scores to save.
     * @param mode
     *            get game mode 1P/2P.
     *
     * @throws IOException
     *             In case of loading problems.
     */
    public void saveHighScores(final List<Score> highScores, String mode) throws IOException {
        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String scoresPath = getFilePath(mode+"scores.csv");

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists())
                scoresFile.createNewFile();

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            logger.info("Saving user high scores.");
            bufferedWriter.write("player,score");
            bufferedWriter.newLine();

            for(Score score : highScores) {
                bufferedWriter.write(score.getName() + "," + score.getScore());
                bufferedWriter.newLine();
            }

        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }
    }

    /**
     * Search Achievement list of user
     *
     * @param userName user's name to search.
     * @throws IOException In case of loading problems.
     */
    public List<Boolean> searchAchievementsByName(String userName) throws IOException {
        List<Boolean> achievementList = new ArrayList<>();

        try {
            String achievementPath = getFilePath("achievement.csv");

            try (BufferedReader bReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(achievementPath), StandardCharsets.UTF_8))) {

                bReader.readLine(); // Skip header
                String line;
                boolean found = false;

                while ((line = bReader.readLine()) != null) {
                    String[] playRecord = line.split(",");
                    if (playRecord.length < 3) continue; // Minimum fields: mode, userName, at least 1 achievement

                    String mode = playRecord[0].trim(); // Mode: "1" or "2"
                    String name = playRecord[1].trim();

                    if (name.equals(userName)) {
                        found = true;
                        logger.info("Loading user achievements.");
                        // Achievements start from index 2
                        for (int i = 2; i < playRecord.length; i++) {
                            achievementList.add(playRecord[i].equals("1"));
                        }
                        break;
                    }
                }

                if (!found) {
                    logger.info("Loading default achievements.");
                    for (int i = 0; i < 5; i++) { // Default to 5 achievements, all set to false
                        achievementList.add(false);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            logger.info("Achievement file not found, loading default achievements.");
            for (int i = 0; i < 5; i++) {
                achievementList.add(false);
            }
        }

        return achievementList;
    }


    /**
     * Unlocks achievements for a specific user.
     *
     * @param userName             The name of the user.
     * @param unlockedAchievement  A list of booleans representing which achievements have been unlocked.
     */
    public void unlockAchievement(String userName, List<Boolean> unlockedAchievement, String mode) {
        List<String[]> records = new ArrayList<>();

        // Extract only numeric part from mode string (e.g., "1P" → "1", "2P" → "2")
        String numericMode = mode.replaceAll("[^0-9]", "");

        try {
            String achievementPath = getFilePath("achievement.csv");

            try (BufferedReader bReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(achievementPath), StandardCharsets.UTF_8))) {

                String line;
                boolean found = false;

                while ((line = bReader.readLine()) != null) {
                    String[] playRecord = line.split(",");

                    // Skip invalid or incomplete lines
                    if (playRecord.length < 3) {
                        records.add(playRecord);
                        continue;
                    }

                    String currentMode = playRecord[0].trim();
                    String name = playRecord[1].trim();

                    // ✅ Match both user name and mode to consider it the same record
                    if (name.equals(userName) && currentMode.equals(numericMode)) {
                        found = true;
                        Logger.getLogger(getClass().getName()).info("Achievement has been updated.");
                        for (int i = 2; i < playRecord.length; i++) {
                            if (playRecord[i].equals("0") && unlockedAchievement.get(i - 2)) {
                                playRecord[i] = "1";
                            }
                        }
                    }

                    records.add(playRecord);
                }

                // If no existing record found, create a new one
                if (!found) {
                    Logger.getLogger(getClass().getName()).info("User not found, creating new record.");
                    String[] newRecord = new String[unlockedAchievement.size() + 2];
                    newRecord[0] = numericMode; // Store numeric mode only
                    newRecord[1] = userName;
                    for (int i = 0; i < unlockedAchievement.size(); i++) {
                        newRecord[i + 2] = unlockedAchievement.get(i) ? "1" : "0";
                    }
                    records.add(newRecord);
                }
            }

            // Write the updated records back to the CSV file
            try (BufferedWriter bWriter = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(achievementPath), StandardCharsets.UTF_8))) {
                for (String[] record : records) {
                    bWriter.write(String.join(",", record));
                    bWriter.newLine();
                }
            }

        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).info("No achievements to save or error occurred.");
        }
    }




    /**
     * Returns a list of users who have completed a specific achievement.
     *
     * @param achievement The achievement to check.
     * @return A list of strings in the format "mode:username" for those who have completed the achievement.
     *
     * [2025-10-09] Added in commit: feat: add method to retrieve achievement completer
     */
    public List<String> getAchievementCompleter(Achievement achievement) {
        List<String> completer = new ArrayList<>();
        try {
            String achievementPath = getFilePath("achievement.csv");

            try (BufferedReader bReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(achievementPath), StandardCharsets.UTF_8))) {

                String line;
                String[] header = bReader.readLine().split(",");
                int idx = -1;

                // Find the column index of the given achievement name
                for (int i = 2; i < header.length; i++) { // Achievements start from column index 2
                    if (header[i].trim().equalsIgnoreCase(achievement.getName().trim())) {
                        idx = i;
                        break;
                    }
                }

                if (idx == -1) {
                    logger.warning("Achievement not found: " + achievement.getName());
                    return completer;
                }

                // Parse each line in the file
                while ((line = bReader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length <= idx) continue;

                    String mode = tokens[0].trim();
                    String playerName = tokens[1].trim();
                    String value = tokens[idx].trim();

                    if (value.equals("1")) {
                        completer.add(mode + ":" + playerName);
                    }
                }

            }

        } catch (IOException e) {
            logger.warning("Error reading achievement file. Returning default users...");
            completer.add("1:ABC");
            completer.add("2:DEF");
        }

        return completer;
    }
}