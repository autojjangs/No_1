package engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.nio.file.*;
import java.util.logging.Logger;

/**
 * Implements an object that stores a single game's difficulty settings.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameSettings {

	private static final Logger LOGGER = Logger.getLogger(Core.class.getSimpleName());
	/** Width of the level's enemy formation. */
	private int formationWidth;
	/** Height of the level's enemy formation. */
	private int formationHeight;
	/** Speed of the enemies, function of the remaining number. */
	private int baseSpeed;
	/** Frequency of enemy shootings, +/- 30%. */
	private int shootingFrecuency;

	//추가 사항
	public static class ChangeData {
		//바꿀 적 위치
		public int x, y;

		//적 체력
		public int hp;
		//적 색상
		public Color color = null;

		//보상 배율
		public int multiplier;

		public ChangeData(int x, int y, int hp, int multiplier) {
			this.x = x; this.y = y;
			this.hp = hp;
			this.multiplier = multiplier;
		}

		public ChangeData(int x, int y, int hp, int multiplier, Color color) {
			this.x = x; this.y = y;
			this.hp = hp;
			this.multiplier = multiplier;
			this.color = color;
		}
	}

	// StageData: Structure of GameSettings + List<ChangeData> at each stage
	public static class StageData {
		public GameSettings settings;
		public List<ChangeData> changeList;
		public StageData(GameSettings settings, List<ChangeData> changeList) {
			this.settings = settings;
			this.changeList = changeList;
		}
	}

	public static Color hexToColor(String hex) {
		if(hex.startsWith("#")) hex = hex.substring(1);
		if(hex.length() > 6) {
			int alpha = Integer.parseInt(hex.substring(7), 16);
			hex = hex.substring(0, 6);
			int red = Integer.parseInt(hex.substring(0, 2), 16);
			int green = Integer.parseInt(hex.substring(3, 5), 16);
			int blue = Integer.parseInt(hex.substring(5), 16);
			// if it is hex code with 8 digits(like #AAFFAABB)
			return new Color(red, green, blue, alpha);
		}
		int rgb = Integer.parseInt(hex, 16);
		// if it is hex code with 6 digits(like #AAFFAA)
		return new Color(rgb);
	}

	public static List<StageData> parseStages(Path path) throws Exception {
		String raw = Files.readString(path);
		raw = raw.replace("\uFEFF", "");
		raw = raw.replaceAll("(?m)^\\s*//.*$", "");

		String[] stageBlocks = raw.split("&");
		List<StageData> result = new ArrayList<>();
		for(String block: stageBlocks) {
			List<String> lines = Arrays.stream(block.split("\n"))
					.map(String::trim)
					.filter(s -> !s.isEmpty() && !s.startsWith("//")) // Deleting comments and empty lines
					.toList();
			if(lines.isEmpty()) continue;

			String[] ints = lines.get(0).split(",");
			GameSettings settings = new GameSettings(
					Integer.parseInt(ints[0].trim()),
					Integer.parseInt(ints[1].trim()),
					Integer.parseInt(ints[2].trim()),
					Integer.parseInt(ints[3].trim())
			);

			List<ChangeData> changeList = new ArrayList<>();
			for(int i = 1; i < lines.size(); i++) {
				String[] parts = lines.get(i).split(",");
				int x = Integer.parseInt(parts[0].trim());
				int y = Integer.parseInt(parts[1].trim());
				int z = Integer.parseInt(parts[2].trim());
				int w = Integer.parseInt(parts[3].trim());
				Color color = null;
				if(parts.length >= 5) {
					String hex = parts[4].trim();
					if(hex.startsWith("#")) hex = hex.substring(1);
					color = hexToColor(hex);
				}
				changeList.add(new ChangeData(x, y, z, w, color));
			}
			result.add(new StageData(settings, changeList));
		}

		return result;
	}

	private List<ChangeData> changeDataList;
	public final List<ChangeData> getChangeDataList() {
		return changeDataList;
	}

	public static List<GameSettings> getGameSettings(){
		List<GameSettings> result = new ArrayList<>();
		List<StageData> stageDataList;

		GameSettings setting;

		try {
			stageDataList = parseStages(Paths.get("res", "level.csv"));
			for(StageData s : stageDataList) {
				setting = s.settings;
				setting.changeDataList = new ArrayList<>();

				setting.changeDataList.addAll(s.changeList);
				result.add(setting);
			}
		} catch (Exception e) {
			LOGGER.info("Failed Loading Data: There is no such file named " + e.getMessage());
			LOGGER.info("By the error, game is closing.");
			System.exit(1);
			return Collections.emptyList();
		}

		if(stageDataList.isEmpty()) {
			LOGGER.info("Failed Loading Data: There is no data in level.csv file.");
			LOGGER.info("By the error, game is closing.");
			System.exit(1);
			return Collections.emptyList();
		}

		return result;
	}


	/**
	 * Constructor.
	 *
	 * @param formationWidth
	 *                          Width of the level's enemy formation.
	 * @param formationHeight
	 *                          Height of the level's enemy formation.
	 * @param baseSpeed
	 *                          Speed of the enemies.
	 * @param shootingFrecuency
	 *                          Frecuency of enemy shootings, +/- 30%.
	 */
	public GameSettings(final int formationWidth, final int formationHeight,
						final int baseSpeed, final int shootingFrecuency) {
		this.formationWidth = formationWidth;
		this.formationHeight = formationHeight;
		this.baseSpeed = baseSpeed;
		this.shootingFrecuency = shootingFrecuency;
		this.changeDataList = new ArrayList<>();
	}

	/**
	 * @return the formationWidth
	 */
	public final int getFormationWidth() {
		return formationWidth;
	}

	/**
	 * @return the formationHeight
	 */
	public final int getFormationHeight() {
		return formationHeight;
	}

	/**
	 * @return the baseSpeed
	 */
	public final int getBaseSpeed() {
		return baseSpeed;
	}

	/**
	 * @return the shootingFrecuency
	 */
	public final int getShootingFrecuency() {
		return shootingFrecuency;
	}

}