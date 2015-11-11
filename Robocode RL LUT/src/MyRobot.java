import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import robocode.*;

public class MyRobot extends AdvancedRobot {

	public static final double PI = Math.PI;
	private EnemyTarget enemyTarget;
	private RLQTable table;
	private RLLearner learner;
	private double reward = 0.0;

	public static int NumTest = 150;
	private static int timer = 0;
	static int total = 0;
	private static double rewardsum = 0.0;
	private static double rewardArray[] = new double[NumTest];
	private static int flag = 0;

	public void run() {
		table = new RLQTable();
		loadData();
		learner = new RLLearner(table);
		enemyTarget = new EnemyTarget();
		enemyTarget.distance = 100000;

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		turnRadarRightRadians(2 * PI);
		while (true) {
			robotMovement();
			radarMovement();
			execute();
		}
	}

	private void robotMovement() {

		if (timer < 2000 && flag == 0) {
			timer++;
			rewardsum = rewardsum + reward;
		} else if (flag == 0) {
			rewardArray[total] = rewardsum;
			rewardsum = 0;
			timer = 0;
			total++;
		}

		if (total >= NumTest && flag == 0) {
			saveRewardData();
			flag = 1;
		}

		int state = getState();
		int action = learner.selectAction(state);
		out.println("Action selected: " + action);
//		learner.learnSarsa(state, action, reward);
		learner.learn(state, action, reward);
		reward = 0.0;

		switch (action) {
		case Action.RobotStop:
			
			break;
		case Action.RobotGravity:
			setupAntiGravityMove(+1000.0);
			break;
		case Action.RobotAntiGravity:
			setupAntiGravityMove(-1000.0);
			break;
		}

	}

	/**
	 * Implements an anti-gravity movement strategy with three components.
	 * <ol>
	 * <li>Each enemy repulses with a force inversely proportional to distance
	 * from us.
	 * <li>A variable midpoint force is intended to improve overall movement.
	 * <li>Walls are repulsive when we are close to them.
	 * </ol>
	 * Calls AdvancedRobot.setTurnLeft() and AdvancedRobot.setAhead() to
	 * implement.
	 */
	void setupAntiGravityMove(double pForce) {
		double xforce = 0;
		double yforce = 0;
		double force;
		double ang;
		GravPoint p;

		p = new GravPoint(enemyTarget.x, enemyTarget.y, pForce);
		force = p.power / Math.pow(getRange(getX(), getY(), p.x, p.y), 2);
		// Find the bearing from the point to us
		ang = NormaliseBearing(Math.PI / 2 - Math.atan2(getY() - p.y, getX() - p.x));
		// Add the components of this force to the total force in their
		// respective directions
		xforce += Math.sin(ang) * force;
		yforce += Math.cos(ang) * force;

		/*
		 * The following four lines add wall avoidance. They will only affect us
		 * if the bot is close to the walls due to the force from the walls
		 * decreasing at a power 3.
		 */
		xforce += 5000 / Math.pow(getRange(getX(), getY(), getBattleFieldWidth(), getY()), 3);
		xforce -= 5000 / Math.pow(getRange(getX(), getY(), 0, getY()), 3);
		yforce += 5000 / Math.pow(getRange(getX(), getY(), getX(), getBattleFieldHeight()), 3);
		yforce -= 5000 / Math.pow(getRange(getX(), getY(), getX(), 0), 3);

		// Move in the direction of our resolved force.
		goTo(getX() - xforce, getY() - yforce);
	}

	/**
	 * Move toward (x, y) on next execute().
	 * 
	 * @param x
	 *            The X coordinate.
	 * @param y
	 *            The Y coordinate.
	 */
	void goTo(double x, double y) {
		double dist = 40;
		double angle = Math.toDegrees(absbearing(getX(), getY(), x, y));
		double r = turnTo(angle);
		out.println("r is: " + r);
		setAhead(dist*r);
	}

	/**
	 * Turns the shortest angle possible to come to a heading, then returns the
	 * direction (1 or -1) that the bot needs to move in.
	 * 
	 * @param angle
	 *            The desired new heading.
	 * @return Our new direction, represented as a 1 or -1.
	 **/
	int turnTo(double angle) {
		double ang;
		int dir;
		ang = NormaliseBearing(getHeading() - angle);
		if (ang > 90) {
			ang -= 180;
			dir = -1;
		} else if (ang < -90) {
			ang += 180;
			dir = -1;
		} else {
			dir = 1;
		}
		setTurnLeft(ang);
		return dir;
	}

	private int getState() {
		int positionX = State.getPositionX(getX());
		int positionY = State.getPositionY(getY());
		int enemypositionX = State.getPositionX(enemyTarget.x);
		int enemypositionY = State.getPositionY(enemyTarget.y);
		int state = State.Mapping[positionX][positionY][enemypositionX][enemypositionY];
		return state;
	}

	private void radarMovement() {
		double radarOffset;
		if (getTime() - enemyTarget.ctime > 4) {
			radarOffset = 4 * PI;
		} else {
			radarOffset = getRadarHeadingRadians() - (Math.PI / 2 - Math.atan2(enemyTarget.y - getY(), enemyTarget.x - getX()));
			radarOffset = NormaliseBearing(radarOffset);
			if (radarOffset < 0)
				radarOffset -= PI / 10;
			else
				radarOffset += PI / 10;
		}
		setTurnRadarLeftRadians(radarOffset);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		double absbearing_rad = (getHeadingRadians() + e.getBearingRadians()) % (2 * PI);
		double h = NormaliseBearing(e.getHeadingRadians() - enemyTarget.head);
		h = h / (getTime() - enemyTarget.ctime);
		enemyTarget.changehead = h;
		enemyTarget.x = getX() + Math.sin(absbearing_rad) * e.getDistance(); 
		enemyTarget.y = getY() + Math.cos(absbearing_rad) * e.getDistance(); 
		enemyTarget.bearing = e.getBearingRadians();
		enemyTarget.head = e.getHeadingRadians();
		enemyTarget.ctime = getTime();
		enemyTarget.speed = e.getVelocity();
		enemyTarget.distance = e.getDistance();
		enemyTarget.energy = e.getEnergy();
	}

	// bearing is within the -pi to pi range
	double NormaliseBearing(double ang) {
		if (ang > PI)
			ang -= 2 * PI;
		if (ang < -PI)
			ang += 2 * PI;
		return ang;
	}

	// heading within the 0 to 2pi range
	double NormaliseHeading(double ang) {
		if (ang > 2 * PI)
			ang -= 2 * PI;
		if (ang < 0)
			ang += 2 * PI;
		return ang;
	}

	/**
	 * Gets the absolute bearing between to x,y coordinates.
	 * 
	 * @param x1
	 *            First x.
	 * @param y1
	 *            First y.
	 * @param x2
	 *            Second x.
	 * @param y2
	 *            Second y.
	 * @return The absolute bearing from (x1, y1) to (x2, y2).
	 */
	public double absbearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		double h = getRange(x1, y1, x2, y2);
		if (xo > 0 && yo > 0) {
			return Math.asin(xo / h);
		}
		if (xo > 0 && yo < 0) {
			return Math.PI - Math.asin(xo / h);
		}
		if (xo < 0 && yo < 0) {
			return Math.PI + Math.asin(-xo / h);
		}
		if (xo < 0 && yo > 0) {
			return 2.0 * Math.PI - Math.asin(-xo / h);
		}
		return 0;
	}

	// returns the distance between two x,y coordinates
	public double getRange(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;
		return Math.sqrt(xo * xo + yo * yo);
	}

	public void onHitByBullet(HitByBulletEvent e) {
		double power = e.getBullet().getPower();
		double change = -(4 * power + 2 * (power - 1));
		reward += change;
	}

	/**
	 * Holds the x, y, and strength info of a gravity point.
	 */
	private static class GravPoint {
		public double x, y, power;

		/**
		 * Creates a new GravPoint class indicating a source of repulsive force
		 * for our robot.
		 * 
		 * @param pX
		 *            The x coordinate.
		 * @param pY
		 *            The y coordinate.
		 * @param pPower
		 *            The repulsive force to be associated with this object.
		 */
		public GravPoint(double pX, double pY, double pPower) {
			x = pX;
			y = pY;
			power = pPower;
		}
	}

	public void onWin(WinEvent event) {
//		learner.learn(getState(), learner.selectAction(getState()), 1.0);
		saveData();
	}

	public void onDeath(DeathEvent event) {
		saveData();
	}

	public void loadData() {
		try {
			table.loadData(getDataFile("movement.dat"));
		} catch (Exception e) {
		}
	}

	public void saveData() {
		try {
			table.saveData(getDataFile("movement.dat"));
		} catch (Exception e) {
			out.println("Exception trying to write: " + e);
		}
	}

	public void saveRewardData() {
		try {
			saveRewardDataFunction(getDataFile("reward.dat"));
		} catch (Exception e) {
			out.println("Exception trying to write: " + e);
		}
	}

	public void saveRewardDataFunction(File file) {
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(file));
			for (int i = 0; i < NumTest; i++)
				w.println(new Double(rewardArray[i]));

			if (w.checkError())
				System.out.println("Could not save the data!");
			w.close();
		} catch (IOException e) {
			System.out.println("IOException trying to write: " + e);
		} finally {
			try {
				if (w != null)
					w.close();
			} catch (Exception e) {
				System.out.println("Exception trying to close witer: " + e);
			}
		}
	}
}
