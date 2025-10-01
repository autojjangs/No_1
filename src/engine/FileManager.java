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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import engine.DrawManager.SpriteType;

/**
 * Manages files used in the application.
 * * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * */
public final class FileManager {

    /** Singleton instance of the class. */
    private static FileManager instance;
    /** Application logger. */
    private static Logger logger;
    /** Max number of high scores. */
    private static final int MAX_SCORES = 7;
    /** The filename used for high scores and coin count. */
    private static final String SCORE_FILENAME = "score.csv"; // CSV 파일명으로 변경

    /**
     * private constructor.
     */
    private FileManager() {
        logger = Core.getLogger();
    }

    /**
     * Returns shared instance of FileManager.
     * * @return Shared instance of FileManager.
     */
    protected static FileManager getInstance() {
        if (instance == null)
            instance = new FileManager();
        return instance;
    }

    /**
     * Loads sprites from disk.
     * * @param spriteMap
     * Mapping of sprite type and empty boolean matrix that will
     * contain the image.
     * @throws IOException
     * In case of loading problems.
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
     * * @param size
     * Point size of the font.
     * @return New font.
     * @throws IOException
     * In case of loading problems.
     * @throws FontFormatException
     * In case of incorrect font format.
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
     * Returns the application default scores if there is no user high scores
     * file.
     * * @return Default high scores.
     * @throws IOException
     * In case of loading problems.
     */
    private List<Score> loadDefaultHighScores() throws IOException {
        List<Score> highScores = new ArrayList<Score>();
        InputStream inputStream = null;
        BufferedReader reader = null;

        try {
            // 기본 점수 파일은 res/scores에서 읽어오므로 기존 줄바꿈 방식 유지
            inputStream = FileManager.class.getClassLoader()
                    .getResourceAsStream("scores");
            reader = new BufferedReader(new InputStreamReader(inputStream));

            Score highScore = null;
            String name = reader.readLine();
            String score = reader.readLine();

            while ((name != null) && (score != null)) {
                highScore = new Score(name, Integer.parseInt(score));
                highScores.add(highScore);
                name = reader.readLine();
                score = reader.readLine();
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
     * * @return Sorted list of scores - players.
     * @throws IOException
     * In case of loading problems.
     */
    public List<Score> loadHighScores() throws IOException {

        List<Score> highScores = new ArrayList<Score>();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += SCORE_FILENAME; // score.csv 사용

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream, Charset.forName("UTF-8")));

            logger.info("Loading user high scores from " + SCORE_FILENAME + ".");

            // 코인 수가 저장된 첫 줄을 건너뛰기
            bufferedReader.readLine();

            Score highScore = null;
            // CSV 파일은 한 줄에 "이름,점수"가 있으므로 한 줄씩 읽음
            String line = bufferedReader.readLine();

            while (line != null) {
                // 쉼표(,)를 기준으로 이름과 점수를 분리
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String score = parts[1].trim();
                    highScore = new Score(name, Integer.parseInt(score));
                    highScores.add(highScore);
                }
                line = bufferedReader.readLine();
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
     * * @param highScores
     * High scores to save.
     * @throws IOException
     * In case of loading problems.
     */
    public void saveHighScores(final List<Score> highScores)
            throws IOException {
        // 코인 수를 보존하기 위해 현재 코인 수를 먼저 로드
        int currentCoins = this.loadCoins();

        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += SCORE_FILENAME; // score.csv 사용

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists())
                scoresFile.createNewFile();

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    outputStream, Charset.forName("UTF-8")));

            logger.info("Saving user high scores to " + SCORE_FILENAME + ".");

            // 파일 맨 첫 줄에 코인 수를 먼저 저장
            bufferedWriter.write(Integer.toString(currentCoins));
            bufferedWriter.newLine();

            // Saves 7 or less scores.
            int savedCount = 0;
            for (Score score : highScores) {
                if (savedCount >= MAX_SCORES)
                    break;

                // CSV 형식: 이름,점수 (줄바꿈은 한 번만)
                bufferedWriter.write(score.getName());
                bufferedWriter.write(","); // 쉼표로 구분
                bufferedWriter.write(Integer.toString(score.getScore()));
                bufferedWriter.newLine();
                savedCount++;
            }

        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }
    }

    // ----------------------------------------------------
    // [추가된 함수] 코인 수를 파일에서 불러옵니다.
    // ----------------------------------------------------
    /**
     * Loads the coin count from the first line of the scores file.
     *
     * @return The saved coin count, or 0 if the file is not found or empty.
     * @throws IOException
     * In case of loading problems.
     */
    public int loadCoins() throws IOException {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += SCORE_FILENAME;

            File scoresFile = new File(scoresPath);
            inputStream = new FileInputStream(scoresFile);
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream, Charset.forName("UTF-8")));

            logger.info("Loading coin count from " + SCORE_FILENAME + ".");

            // 맨 첫 줄을 읽어 코인 수를 가져옴
            String line = bufferedReader.readLine();

            if (line != null && !line.trim().isEmpty()) {
                try {
                    // 숫자로 변환하여 반환
                    return Integer.parseInt(line.trim());
                } catch (NumberFormatException e) {
                    logger.warning("Coin count line is not a valid number. Returning 0.");
                    return 0;
                }
            }

        } catch (FileNotFoundException e) {
            logger.info(SCORE_FILENAME + " not found. Returning 0 coins.");
            return 0;
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }

        return 0;
    }

    // ----------------------------------------------------
    // [추가된 함수] 코인 수를 파일에 저장합니다.
    // ----------------------------------------------------
    /**
     * Saves the current coin count to the first line of the scores file,
     * while preserving the existing high scores.
     *
     * @param coins
     * The total number of coins acquired.
     * @throws IOException
     * In case of saving problems.
     */
    public void saveCoins(final int coins) throws IOException {
        // 1. 현재 점수 목록을 불러와서 메모리에 임시 저장
        List<Score> currentHighScores = this.loadHighScores();

        OutputStream outputStream = null;
        BufferedWriter bufferedWriter = null;

        try {
            String jarPath = FileManager.class.getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");

            String scoresPath = new File(jarPath).getParent();
            scoresPath += File.separator;
            scoresPath += SCORE_FILENAME;

            File scoresFile = new File(scoresPath);

            if (!scoresFile.exists())
                scoresFile.createNewFile();

            outputStream = new FileOutputStream(scoresFile);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    outputStream, Charset.forName("UTF-8")));

            logger.info("Saving new coin count (" + coins + ") to " + SCORE_FILENAME + ".");

            // 2. 맨 첫 줄에 새로운 코인 수 저장
            bufferedWriter.write(Integer.toString(coins));
            bufferedWriter.newLine();

            // 3. 그 아래에 기존 점수 목록 다시 저장 (saveHighScores 로직 재현)
            int savedCount = 0;
            for (Score score : currentHighScores) {
                if (savedCount >= MAX_SCORES)
                    break;

                // CSV 형식: 이름,점수
                bufferedWriter.write(score.getName());
                bufferedWriter.write(",");
                bufferedWriter.write(Integer.toString(score.getScore()));
                bufferedWriter.newLine();
                savedCount++;
            }

        } finally {
            if (bufferedWriter != null)
                bufferedWriter.close();
        }
    }
}