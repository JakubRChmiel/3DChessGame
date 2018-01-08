package chessPieces;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.Alpha;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Box;

public class King extends ChessPieces {

	private PositionInterpolator kingUp, kingRight, kingLeft, kingDown, kingUpLeft, kingUpRight, kingDownLeft,
			kingDownRight, kingCastleKingSide, kingCastleQueenSide;
	private boolean inCheck;

	// Constructor Method

	public King(int row, int col, boolean isWhite) {

		super(row, col, isWhite);
		isKing = true;
		rookQueenMoved = false;
		rookKingMoved = false;
		kingMoved = false;
		inCheck = false;

		try {
		loadPiece();
		}
		catch(Exception e) {
			loadLocal();
		}
		// Create the tgmPiece which will be moveable
		tgmPiece = new TransformGroup();
		tgmPiece.addChild(tgPiece);

		// Create all the alphas you'll need for the king
		numberOfInterpolators = 10;

		alphas = new Alpha[numberOfInterpolators];
		for (int a = 0; a < numberOfInterpolators; a++) {
			alphas[a] = new Alpha(1, 500);
			alphas[a].setStartTime(Long.MAX_VALUE);
		}

		allPositionInterpolators = new PositionInterpolator[numberOfInterpolators];
		kingUp = new PositionInterpolator(alphas[0], tgmPiece, yaxis, 0.0f, 1.0f);
		kingDown = new PositionInterpolator(alphas[1], tgmPiece, yaxis, 0.0f, -1.0f);
		kingLeft = new PositionInterpolator(alphas[2], tgmPiece, xaxis, 0.0f, -1.0f);
		kingRight = new PositionInterpolator(alphas[3], tgmPiece, xaxis, 0.0f, 1.0f);
		kingUpRight = new PositionInterpolator(alphas[4], tgmPiece, upRightAxis, 0.0f, 1.41421f);
		kingDownLeft = new PositionInterpolator(alphas[5], tgmPiece, upRightAxis, 0.0f, -1.41421f);
		kingUpLeft = new PositionInterpolator(alphas[6], tgmPiece, upLeftAxis, 0.0f, 1.41421f);
		kingDownRight = new PositionInterpolator(alphas[7], tgmPiece, upLeftAxis, 0.0f, -1.41421f);
		kingCastleKingSide = new PositionInterpolator(alphas[8], tgmPiece, xaxis, 0.0f, 2.0f);
		kingCastleQueenSide = new PositionInterpolator(alphas[9], tgmPiece, xaxis, 0.0f, -2.0f);

		// add all position interpolators to the array
		allPositionInterpolators[0] = kingUp;
		allPositionInterpolators[1] = kingDown;
		allPositionInterpolators[2] = kingLeft;
		allPositionInterpolators[3] = kingRight;
		allPositionInterpolators[4] = kingUpRight;
		allPositionInterpolators[5] = kingDownLeft;
		allPositionInterpolators[6] = kingUpLeft;
		allPositionInterpolators[7] = kingDownRight;
		allPositionInterpolators[8] = kingCastleKingSide;
		allPositionInterpolators[9] = kingCastleQueenSide;

		for (int z = 0; z < numberOfInterpolators; z++) {
			allPositionInterpolators[z].setSchedulingBounds(bs);
			tgmPiece.addChild(allPositionInterpolators[z]);
		}

		// Add position interpolator for capture event
		captureAlpha = new Alpha(1, Alpha.INCREASING_ENABLE, 0, 200, 300, 0, 0, 0, 0, 0);
		captureAlpha.setStartTime(Long.MAX_VALUE);
		captureUnderBoard = new PositionInterpolator(captureAlpha, tgmPiece, zaxis, 0.0f, -5.0f);
		captureUnderBoard.setSchedulingBounds(bs);

		tgmPiece.addChild(captureUnderBoard);
		tgmPiece.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}

	// Constructor Method

	public void setMoveLogic(int[][] boardForLogic, int[][] boardForLogicEnemy, int inPassCol, boolean inPassEnabled) {
		// set board as 0
		// Get board for your own pieces

		int[][] boardForLogicPlayer = new int[8][8];

		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++) {
				boardForLogicPlayer[a][b] = boardForLogic[a][b];
			}
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++) {

				if (boardForLogicEnemy[a][b] != 0)
					boardForLogicPlayer[a][b] = 0;
			}

		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++) {
				moveLogic[a][b] = 0;
			}

		// Check for enemy piece or vacancy up
		if (row - 1 > -1) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row - 1][column] != 0)
				moveLogic[row - 1][column] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row - 1][column] == 0)
				moveLogic[row - 1][column] = 1;
		}
		// Check for enemy piece or vacancy down
		if (row + 1 < 8) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row + 1][column] != 0)
				moveLogic[row + 1][column] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row + 1][column] == 0)
				moveLogic[row + 1][column] = 1;
		}
		// Check for enemy piece or vacancy left
		if (column - 1 > -1) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row][column - 1] != 0)
				moveLogic[row][column - 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row][column - 1] == 0)
				moveLogic[row][column - 1] = 1;
		}
		// Check for enemy piece or vacancy right
		if (column + 1 < 8) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row][column + 1] != 0)
				moveLogic[row][column + 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row][column + 1] == 0)
				moveLogic[row][column + 1] = 1;
		}
		// Check for diagonals next

		// Check for enemy piece or vacancy up right
		if (row - 1 > -1 && column + 1 < 8) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row - 1][column + 1] != 0)
				moveLogic[row - 1][column + 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row - 1][column + 1] == 0)
				moveLogic[row - 1][column + 1] = 1;

		}
		// Check for enemy piece or vacancy down right
		if (row + 1 < 8 && column + 1 < 8) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row + 1][column + 1] != 0)
				moveLogic[row + 1][column + 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row + 1][column + 1] == 0)
				moveLogic[row + 1][column + 1] = 1;
		}

		// Check for enemy piece or vacancy up left
		if (row - 1 > -1 && column - 1 > -1) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row - 1][column - 1] != 0)
				moveLogic[row - 1][column - 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row - 1][column - 1] == 0)
				moveLogic[row - 1][column - 1] = 1;
		}
		// Check for enemy piece or vacancy down left
		if (row + 1 < 8 && column - 1 > -1) {
			// if there's an enemy ahead can take
			if (boardForLogicEnemy[row + 1][column - 1] != 0)
				moveLogic[row + 1][column - 1] = 1;
			// if there's an empty space can move to
			if (boardForLogicPlayer[row + 1][column - 1] == 0)
				moveLogic[row + 1][column - 1] = 1;
		}

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				if (moveLogic[x][y] == 1) {
					// Call method to return whether moveLogic should be
					// reverted or not
					if (!getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy, x, y))
						moveLogic[x][y] = 0;
				}
			}
		}

		/*
		 * System.out.
		 * println("THE UPDATED MOVE LOGIC BOARD BEFORE CASTLING IS THE FOLLOWING: "
		 * );
		 * 
		 * for (int x = 0; x < 8; x++) { for (int y = 0; y < 8; y++) {
		 * System.out.print(moveLogic[x][y] + " "); } System.out.println(); }
		 */

		// Find out if we are in check currently
		// using boards for logic and stuff

		// Here we can check if you can castle
		// first check king side ********************************
		// Neither piece may have moved prior
		if (!rookKingMoved && !kingMoved) {
			// if king has not moved we will bother to check if he can castle
			inCheck = !getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy, row, column);

			// if he's not in check
			if (!inCheck) {

				// You may not pass through check
				if (moveLogic[row][column + 1] == 1) {

					// Must not be any piece on destination spot
					if (boardForLogic[row][6] == 0) {

						// We've reached a point where we can add castling as
						// move for the king

						moveLogic[row][6] = 1;
					}
				}
			}
		}

		// Check castle queen side
		if (!rookQueenMoved && !kingMoved) {
			// if king has not moved we will bother to check if he can castle
			inCheck = !getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy, row, column);

			// if he's not in check
			if (!inCheck) {

				// You may not pass through check
				if (moveLogic[row][column - 1] == 1) {

					// Must not be any piece on destination spot
					if (boardForLogic[row][2] == 0 && boardForLogic[row][1] == 0) {

						// We've reached a point where we can add castling as
						// move for the king

						moveLogic[row][2] = 1;
					}
				}
			}
		}
		/*
		 * System.out.
		 * println("THE UPDATED MOVE LOGIC BOARD AFTER CASTLING IS THE FOLLOWING: "
		 * );
		 * 
		 * for (int x = 0; x < 8; x++) { for (int y = 0; y < 8; y++) {
		 * System.out.print(moveLogic[x][y] + " "); } System.out.println(); }
		 */

	}

	/*
	 * ************************************************* Accessor Methods
	 * *************************************************
	 */

	public boolean isKingInCheck(boolean[][] influenceArray) {

		// Check if king's row and column coincide with a 1 on the
		// influenceArray

		// ********* for right now put false, eventually must be implemented
		return false;

	}

	public int[] selectPositionInterpolator(int rowInput, int colInput) {

		int rowDiff = row - rowInput; // so that positive means move up
		int colDiff = colInput - column; // so that positive means right
		int[] returnInt = new int[1];

		if (rowDiff == 1 && colDiff == 0)
			returnInt[0] = 0;
		else if (rowDiff == -1 && colDiff == 0)
			returnInt[0] = 1;
		else if (rowDiff == 0 && colDiff == -1)
			returnInt[0] = 2;
		else if (rowDiff == 0 && colDiff == 1)
			returnInt[0] = 3;
		else if (rowDiff == 1 && colDiff == 1)
			returnInt[0] = 4;
		else if (rowDiff == -1 && colDiff == -1)
			returnInt[0] = 5;
		else if (rowDiff == 1 && colDiff == -1)
			returnInt[0] = 6;
		else if (rowDiff == -1 && colDiff == 1)
			returnInt[0] = 7;
		else if (colDiff == 2)
			returnInt[0] = 8;
		else if (colDiff == -2)
			returnInt[0] = 9;

		return returnInt;

	}

	public PositionInterpolator getPositionInterpolator(int selection) {

		return allPositionInterpolators[selection];

	}

	public String toString() {
		String name = "King";
		return name;
	}

	public void setMoveYet(int moveCode) {

		if (moveCode == 1)
			rookQueenMoved = true;
		if (moveCode == 2)
			kingMoved = true;
		if (moveCode == 3)
			rookKingMoved = true;

	}

	public void loadLocal() {
		// Create transform for making tgPiece a correctly oriented thing on
		// origin
		fixOrientation = new Transform3D();
		fixOrientation.setTranslation(new Vector3f(0.0f, 0.0f, 0.5f));

		// Create a box that will be the king
		Box kingBox = new Box(0.2f, 0.2f, 0.5f, getColor(isWhite));

		tgPiece = new TransformGroup(fixOrientation);
		tgPiece.addChild(kingBox);
	}

	public void loadPiece() {

		// Correct orrientation of default piece in the origin
		fixOrientation = new Transform3D();
		fixOrientation.rotX(Math.PI / 2);

		Transform3D scaleDown = new Transform3D();
		scaleDown.setScale(0.85);

		Transform3D putOnBoard = new Transform3D();
		putOnBoard.setTranslation(new Vector3f(0.0f, 0.0f, 0.9f));

		fixOrientation.mul(fixOrientation, scaleDown);

		fixOrientation.mul(putOnBoard, fixOrientation);

		// tgPiece will be correctly placed in origin (square a1)
		tgPiece = new TransformGroup(fixOrientation);

		ObjectFile f = new ObjectFile(ObjectFile.RESIZE);
		Scene s = null;
		try {
			s = f.load("Assets/King.obj");

			tgPiece.addChild(s.getSceneGroup());
		} catch (Exception e) {
			System.out.println("File failed to print" + e);
		}

		// In the following way, the names of the parts of the object can be
		// obtained. The names are printed.
		Hashtable namedObjects = s.getNamedObjects();
		Enumeration enumer = namedObjects.keys();
		String name;
		Shape3D parts = new Shape3D();

		name = (String) enumer.nextElement();
		parts = (Shape3D) namedObjects.get(name);

		parts.setAppearance(getColor(isWhite));

	}

}
