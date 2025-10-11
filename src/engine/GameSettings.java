package engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Implements an object that stores a single game's difficulty settings.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameSettings {

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

    private List<ChangeData> changeDataList;
    public final List<ChangeData> getChangeDataList() {
        return changeDataList;
    }

    public static List<GameSettings> getGameSettings(){
        List<GameSettings> result = new ArrayList<>();

        GameSettings setting;

            //Level 1
            setting = new GameSettings(4, 3, 80, 2000);
            result.add(setting);

            //Level 2
            setting = new GameSettings(5, 4, 70, 1500);
            setting.changeDataList = Arrays.asList(
                    new ChangeData(0, 0, 2, 2, Color.RED),
                    new ChangeData(1, 0, 2, 2, Color.RED),
                    new ChangeData(2, 0, 2, 2, Color.RED),
                    new ChangeData(3, 0, 2, 2, Color.RED),
                    new ChangeData(4, 0, 2, 2, Color.RED)
            );
            result.add(setting);

        //Bonus Stage
        setting = new GameSettings(5, 4, 1, 1000000);
        setting.changeDataList = Arrays.asList(
                new ChangeData(0, 0, 0, 0),
                new ChangeData(1, 0, 0, 0),
                new ChangeData(2, 0, 1, 2, Color.yellow),
                new ChangeData(3, 0, 0, 0),
                new ChangeData(4, 0, 0, 0),

                new ChangeData(0, 1, 1, 2, Color.yellow),
                new ChangeData(1, 1, 1, 2, Color.yellow),
                new ChangeData(2, 1, 1, 2, Color.yellow),
                new ChangeData(3, 1, 1, 2, Color.yellow),
                new ChangeData(4, 1, 1, 2, Color.yellow),

                new ChangeData(0, 2, 0, 0),
                new ChangeData(1, 2, 1, 2, Color.yellow),
                new ChangeData(2, 2, 1, 2, Color.yellow),
                new ChangeData(3, 2, 1, 2, Color.yellow),
                new ChangeData(4, 2, 0, 0),

                new ChangeData(0, 3, 0, 0),
                new ChangeData(1, 3, 1, 2, Color.yellow),
                new ChangeData(2, 3, 0, 0),
                new ChangeData(3, 3, 1, 2, Color.yellow),
                new ChangeData(4, 3, 0, 0)

        );
        result.add(setting);

        //Level 4
        setting = new GameSettings(8, 2, 40, 1000);
        setting.changeDataList = Arrays.asList(
                new ChangeData(0, 0, 2, 2, Color.RED),
                new ChangeData(1, 1, 2, 2, Color.RED),
                new ChangeData(2, 0, 2, 2, Color.RED),
                new ChangeData(3, 1, 2, 2, Color.RED),
                new ChangeData(4, 0, 2, 2, Color.RED),
                new ChangeData(5, 1, 2, 2, Color.RED),
                new ChangeData(6, 0, 2, 2, Color.RED),
                new ChangeData(7, 1, 2, 2, Color.RED)
        );
        result.add(setting);

        //Level 5
        setting = new GameSettings(3, 3, 40, 1000);
        setting.changeDataList = Arrays.asList(
                new ChangeData(0, 0, 3, 3, Color.BLUE),
                new ChangeData(1, 0, 3, 3, Color.BLUE),
                new ChangeData(2, 0, 3, 3, Color.BLUE),

                new ChangeData(0, 1, 3, 3, Color.BLUE),
                new ChangeData(1, 1, 10, 10, Color.MAGENTA),
                new ChangeData(2, 1, 3, 3, Color.BLUE),

                new ChangeData(0, 2, 3, 3, Color.BLUE),
                new ChangeData(1, 2, 3, 3, Color.BLUE),
                new ChangeData(2, 2, 3, 3, Color.BLUE)

        );
        result.add(setting);

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