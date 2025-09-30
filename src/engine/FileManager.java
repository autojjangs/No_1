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
	 * Max number of high scores.
	 */
	private static final int MAX_SCORES = 7;

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
	 * @param spriteMap Mapping of sprite type and empty boolean matrix that will
	 *                  contain the image.
	 * @throws IOException In case of loading problems.
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
	 * @param size Point size of the font.
	 * @return New font.
	 * @throws IOException         In case of loading problems.
	 * @throws FontFormatException In case of incorrect font format.
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
	 *
	 * @return Default high scores.
	 * @throws IOException In case of loading problems.
	 */
	private List<Score> loadDefaultHighScores() throws IOException {
		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
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
	 *
	 * @return Sorted list of scores - players.
	 * @throws IOException In case of loading problems.
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
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user high scores.");

			Score highScore = null;
			String name = bufferedReader.readLine();
			String score = bufferedReader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score));
				highScores.add(highScore);
				name = bufferedReader.readLine();
				score = bufferedReader.readLine();
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
	 * @param highScores High scores to save.
	 * @throws IOException In case of loading problems.
	 */
	public void saveHighScores(final List<Score> highScores)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);

			if (!scoresFile.exists())
				scoresFile.createNewFile();

			outputStream = new FileOutputStream(scoresFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user high scores.");

			// Saves 7 or less scores.
			int savedCount = 0;
			for (Score score : highScores) {
				if (savedCount >= MAX_SCORES)
					break;
				bufferedWriter.write(score.getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getScore()));
				bufferedWriter.newLine();
				savedCount++;
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
	public List<Boolean> searchAchievementsByName(String userName)
			throws IOException {
		List<Boolean> achievementList = new ArrayList<Boolean>();

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String achievementPath = String.valueOf(new File(jarPath));
			achievementPath += File.separator;
			achievementPath += "achievement.csv";

			// 디버그 출력
			System.out.println(">>> achievementPath = " + achievementPath);
			File testFile = new File(achievementPath);
			System.out.println(">>> File exists? " + testFile.exists());

			InputStream iStream = new FileInputStream(achievementPath);
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(iStream, Charset.forName("UTF-8")));

			bReader.readLine(); // Dump header
			String line;
			boolean flag = false;
			while ((line = bReader.readLine()) != null) {
				String[] playRecord = line.split(",");
				if (playRecord[0].equals(userName)) {
					flag = true;
					logger.info("Loading user achievements.");
					for (int i = 1; i < playRecord.length; i++) {
						achievementList.add(playRecord[i].equals("1") ? true : false);
					}
					break;
				}
			}
			if (!flag)
				for (int i = 0; i < 5; i++) {
					logger.info("Loading default achievement.");
					achievementList.add(false);
				}
		} catch (FileNotFoundException e) {
			logger.info("Loading default achievement.");
			for (int i = 0; i < 5; i++) {
				achievementList.add(false);
			}
		}
		return achievementList;
	}

	/**
	 * Unlocks an achievement for the given user.
	 *
	 * @param userName            user's name to search.
	 * @param unlockedAchievement A list of booleans representing which achievements
	 * @throws IOException In case of loading problems.
	 */
	public void unlockAchievement(String userName, List<Boolean> unlockedAchievement)
			throws IOException {
		List<String[]> records = new ArrayList<>();
		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String achievementPath = String.valueOf(new File(jarPath));
			achievementPath += File.separator;
			achievementPath += "achievement.csv";

			InputStream iStream = new FileInputStream(achievementPath);
			BufferedReader bReader = new BufferedReader(
					new InputStreamReader(iStream, Charset.forName("UTF-8")));

			String line;
			boolean flag = false;
			List<String[]> recorder = new ArrayList<>();
			while ((line = bReader.readLine()) != null) {
				String[] playRecord = line.split(",");
				if (playRecord[0].equals(userName)) {
					flag = true;
					logger.info("Achievement has been updated");
					for (int i = 1; i < playRecord.length; i++) {
						if (playRecord[i].equals("0") && unlockedAchievement.get(i))
							playRecord[i] = "1";
					}
				}
				recorder.add(playRecord);
			}
			if (!flag){
				logger.info("User not found, creating new record.");
				String[] newRecord = new String[unlockedAchievement.size() + 1];
				newRecord[0] = userName;
				for (int i = 0; i < unlockedAchievement.size(); i++)
					newRecord[i+1] = unlockedAchievement.get(i) ? "1" : "0";
				recorder.add(newRecord);
			}


			OutputStream outStream = new FileOutputStream(achievementPath);
			BufferedWriter bWriter = new BufferedWriter(
					new OutputStreamWriter(outStream, Charset.forName("UTF-8")));

			for (String[] record : recorder) {
				bWriter.write(String.join(",", record));
				bWriter.newLine();
			}

			bWriter.close();

		} catch (FileNotFoundException e) {
			logger.info("No achievements to save");
		}
	}
}
