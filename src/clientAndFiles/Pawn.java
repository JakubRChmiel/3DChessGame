package clientAndFiles;

import java.io.FileNotFoundException;
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
import com.sun.j3d.utils.geometry.Cone;

public class Pawn extends ChessPieces {

	private boolean canJump;
	// private boolean canEnPassante; // Not sure that this will be necessary
	private PositionInterpolator pawn2Jump;
	private PositionInterpolator pawnJump;
	private PositionInterpolator pawnTakeRight;
	private PositionInterpolator pawnTakeLeft;
	private Shape3D loadedPawn;

	public Pawn(int row, int column, boolean isWhite) {

		// Set the starting position/ conditions of the piece

		// Create chess piece, later add pawn related things
		super(row, column, isWhite);

		isKing = false;
		canJump = true;
		// canEnPassante = false;

		// ***************************************************************
		// All transform3D required
		// ***************************************************************

		// Create shape of pawn to be repositioned
		Cone pawnShape = new Cone(0.4f, 1.0f, getColor(isWhite));

		// For now try to import a wavefront object for the pawn
		// Will be replaced with blender pawn later

		// tgPiece.addChild(pawnShape);
		try {
			loadObjectPawn();
		} catch (Exception e) {
			loadLocalPiece();
		}
		// tgmPiece contains the correctly placed piece on a1
		tgmPiece = new TransformGroup();
		tgmPiece.addChild(tgPiece);

		// tgmPiece is moved to correct position on board in constructor
		tgmPiece.setTransform(moveToSquare);

		// Equals to 4 for pawns (4 possible move types)
		numberOfInterpolators = 4;

		// Alphas contain the same value but are necessary because we want start
		// times to change
		// individually based on moves
		alphas = new Alpha[numberOfInterpolators];

		for (int a = 0; a < numberOfInterpolators; a++) {
			alphas[a] = new Alpha(1, 500);
			alphas[a].setStartTime(Long.MAX_VALUE);
		}

		// Define the logic for the position interpolators

		if (isWhite) {
			pawn2Jump = new PositionInterpolator(alphas[0], tgmPiece, yaxis, 0.0f, 2.0f);
			pawnJump = new PositionInterpolator(alphas[1], tgmPiece, yaxis, 0.0f, 1.0f);
			pawnTakeRight = new PositionInterpolator(alphas[2], tgmPiece, upRightAxis, 0.0f, 1.41421f);
			pawnTakeLeft = new PositionInterpolator(alphas[3], tgmPiece, upLeftAxis, 0.0f, 1.41421f);

		} else {
			pawn2Jump = new PositionInterpolator(alphas[0], tgmPiece, yaxis, 0.0f, -2.0f);
			pawnJump = new PositionInterpolator(alphas[1], tgmPiece, yaxis, 0.0f, -1.0f);
			pawnTakeRight = new PositionInterpolator(alphas[2], tgmPiece, upLeftAxis, 0.0f, -1.41421f);
			pawnTakeLeft = new PositionInterpolator(alphas[3], tgmPiece, upRightAxis, 0.0f, -1.41421f);

		}

		// set the scheduling bounds for interpolators so they do the thing

		pawn2Jump.setSchedulingBounds(bs);
		pawnJump.setSchedulingBounds(bs);
		pawnTakeRight.setSchedulingBounds(bs);
		pawnTakeLeft.setSchedulingBounds(bs);

		allPositionInterpolators = new PositionInterpolator[4];
		allPositionInterpolators[0] = pawn2Jump;
		allPositionInterpolators[1] = pawnJump;
		allPositionInterpolators[2] = pawnTakeRight;
		allPositionInterpolators[3] = pawnTakeLeft;

		// Add all of these interpolators to tgmPiece
		for (int z = 0; z < numberOfInterpolators; z++)
			tgmPiece.addChild(allPositionInterpolators[z]);

		// Add position interpolator for capture event
		captureAlpha = new Alpha(1, Alpha.INCREASING_ENABLE, 0, 200, 300, 0, 0, 0, 0, 0);
		captureAlpha.setStartTime(Long.MAX_VALUE);
		captureUnderBoard = new PositionInterpolator(captureAlpha, tgmPiece, zaxis, 0.0f, -5.0f);
		captureUnderBoard.setSchedulingBounds(bs);

		tgmPiece.addChild(captureUnderBoard);

		tgmPiece.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}

	/*
	 * ***************************************************** Mutator methods
	 * *****************************************************
	 */

	public void setMoveLogic(int[][] boardForLogic, int[][] boardForLogicEnemy, int inPassCol, boolean inPassEnabled) {

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
			for (int b = 0; b < 8; b++)
				moveLogic[a][b] = 1;

		// Can definitely speed up algorithm by starting with 0 and only adding
		// legal moves oh well
		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {

				if (isWhite) {

					// cancel the same row/ below pawn
					if (a >= row)
						moveLogic[a][b] = 0;

					// Cancel the rows more than 1 above
					if (a < row - 1)
						moveLogic[a][b] = 0;

					// Now cancel the unreachable columns
					if (Math.abs(b - column) > 0)
						moveLogic[a][b] = 0;

				}
				if (!isWhite) {
					if (a <= row)
						moveLogic[a][b] = 0;

					// Cancel the rows more than 1 below
					if (a > row + 1)
						moveLogic[a][b] = 0;
					// Now cancel the unreachable columns
					if (Math.abs(b - column) > 0)
						moveLogic[a][b] = 0;

				}
			}
		}
		// Stuff done outside of loop

		if (isWhite) {

			// Add double jump ability to be evaluated next
			if (row == 6)
				moveLogic[row - 2][column] = 1;
			// Add attacking ability

			if (column - 1 >= 0)
				if (boardForLogicEnemy[row - 1][column - 1] != 0)
					moveLogic[row - 1][column - 1] = 1;

			if (column + 1 < 8)
				if (boardForLogicEnemy[row - 1][column + 1] != 0)
					moveLogic[row - 1][column + 1] = 1;

			// Cut down ability to jump ahead if there's any piece in the way
			if (boardForLogic[row - 1][column] != 0)
				moveLogic[row - 1][column] = 0;
			if (row == 6 && boardForLogic[row - 2][column] != 0)
				moveLogic[row - 2][column] = 0;

		}

		if (!isWhite) {

			// Add double jump ability to be evaluated next
			if (row == 1)
				moveLogic[row + 2][column] = 1;
			// Add attacking ability
			if (column - 1 >= 0)
				if (boardForLogicEnemy[row + 1][column - 1] != 0)
					moveLogic[row + 1][column - 1] = 1;
			if (column + 1 < 8)
				if (boardForLogicEnemy[row + 1][column + 1] != 0)
					moveLogic[row + 1][column + 1] = 1;
			// Cut down ability to jump ahead if there's any piece in the way
			if (boardForLogic[row + 1][column] != 0)
				moveLogic[row + 1][column] = 0;
			if (row == 1 && boardForLogic[row + 2][column] != 0)
				moveLogic[row + 2][column] = 0;
		}

		// NOW WE HAVE ENPASS INFORMATION SENT IN WOOT
		if (inPassEnabled) {
			// Case for if you're white w/ in pass ability
			if (isWhite) {
				// Pawn to your left
				if (row == 3 && column - inPassCol == 1) {
					moveLogic[row - 1][column - 1] = 1;
				}
				// Pawn to your right
				if (row == 3 && column - inPassCol == -1) {
					moveLogic[row - 1][column + 1] = 1;
				}
			}
			if (!isWhite) {
				// Pawn to your left
				if (row == 4 && column - inPassCol == 1)
					moveLogic[row + 1][column - 1] = 1;
				if (row == 4 && column - inPassCol == -1)
					moveLogic[row + 1][column + 1] = 1;
			}
		}

		// Now we have a moveLogic board with potential moves that need to be
		// tested for checks
		// We have a boardForLogic, boardForLogicEnemy and boardForLogicPlayer
		// From boardForLogic player, we have 6 = king which is ours, first get
		// the position of king not changing

		// N0w that we have the king's location which will not move ever for all
		// pawn possible moves
		// Create temporary enemy board array and temporary total board to get
		// back an array that says check or not

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {

				if (moveLogic[a][b] == 1) {
					// Call method to return whether moveLogic should be
					// reverted or not
					if (!getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy, a, b))
						moveLogic[a][b] = 0;
				}
			}
		}

		/*
		 * System.out.println("THE UPDATED MOVE LOGIC BOARD IS THE FOLLOWING: "
		 * );
		 * 
		 * for (int x = 0; x < 8; x++) { for (int y = 0; y < 8; y++) {
		 * System.out.print(moveLogic[x][y] + " "); } System.out.println(); }
		 */

	}

	/*
	 * ******************************************************* Accessor Methods
	 * **************************************************
	 */

	public boolean getCanJump() {
		return canJump;
	}

	public int[] selectPositionInterpolator(int rowInput, int colInput) {

		int[] returnInt = new int[1];
		if (Math.abs(row - rowInput) == 2)
			returnInt[0] = 0;
		else if (column == colInput)
			returnInt[0] = 1;
		else if (column - colInput == -1)
			returnInt[0] = 2;
		else
			returnInt[0] = 3;

		return returnInt;
	}

	public TransformGroup getTGM() {
		return tgmPiece;
	}

	public PositionInterpolator getPositionInterpolator(int a) {
		return allPositionInterpolators[a];
	}

	public String toString() {
		String name = "Pawn";
		return name;
	}

	public void loadObjectPawn() {

		// Correct orrientation of default piece in the origin
				fixOrientation = new Transform3D();
				fixOrientation.rotX(Math.PI / 2);

				Transform3D scaleDown = new Transform3D();
				scaleDown.setScale(0.55);

				Transform3D putOnBoard = new Transform3D();
				putOnBoard.setTranslation(new Vector3f(0.0f, 0.0f, 0.55f));

				fixOrientation.mul(fixOrientation, scaleDown);

				fixOrientation.mul(putOnBoard, fixOrientation);
		
		// tgPiece will be correctly placed in origin (square a1)
		tgPiece = new TransformGroup(fixOrientation);

		

		ObjectFile f = new ObjectFile(ObjectFile.RESIZE);
		Scene s = null;
		try {
			s = f.load("pawnFirstAttempt.obj");

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

	public void loadLocalPiece() {
		// add the old piece to tgPiece

		fixOrientation = new Transform3D();
		fixOrientation.rotX(Math.PI / 2);

		Transform3D putOnBoard = new Transform3D();
		putOnBoard.setTranslation(new Vector3f(0.0f, 0.0f, 0.1f));

		// tgPiece will be correctly placed in origin (square a1)
		tgPiece = new TransformGroup(fixOrientation);

		fixOrientation.mul(putOnBoard, fixOrientation);
		// Create shape of pawn to be repositioned
		Cone pawnShape = new Cone(0.4f, 1.0f, getColor(isWhite));
		tgPiece.addChild(pawnShape);

	}

}
