
public class State {

	public static final int NumX = 4;
	public static final int NumY = 3;
	public static final int NumEnemyX = 4;
	public static final int NumEnemyY = 3;
	public static final int NumStates;
	public static final int Mapping[][][][];

	static {
		Mapping = new int[NumX][NumY][NumEnemyX][NumEnemyY];
		int count = 0;
		for (int a = 0; a < NumX; a++)
			for (int b = 0; b < NumY; b++)
				for (int c = 0; c < NumEnemyX; c++)
					for (int d = 0; d < NumEnemyY; d++)
						Mapping[a][b][c][d] = count++;
		NumStates = count;
	}

	public static int getPositionX(double value) {
		int positionX = (int) (value / 200.0);
		if (positionX > NumX - 1)
			positionX = NumX - 1;
		return positionX;
	}

	public static int getPositionY(double value) {
		int positionY = (int) (value / 200.0);
		if (positionY > NumY - 1)
			positionY = NumY - 1;
		return positionY;
	}
}
