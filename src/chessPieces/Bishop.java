package chessPieces;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.*;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.*;
import javax.vecmath.*;

public class Bishop extends ChessPieces {

	public Bishop(int row, int col, boolean isWhite) {

		super(row, col, isWhite);

		// Create a transformation group tgPiece which is a correctly positioned
		// piece on the origin

		try {
			loadObject();
		} catch (Exception e) {
			loadLocal();
		}

		TransformGroup tgmPieceHop = new TransformGroup();
		tgmPieceHop.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgmPieceHop.addChild(tgPiece);

		TransformGroup tgmPieceLateral = new TransformGroup();
		tgmPieceLateral.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgmPieceLateral.addChild(tgmPieceHop);

		TransformGroup tgmPieceHorizontal = new TransformGroup();
		tgmPieceHorizontal.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgmPieceHorizontal.addChild(tgmPieceLateral);

		tgmPiece = new TransformGroup();
		tgmPiece.addChild(tgmPieceHorizontal);
		tgmPiece.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// Create all the alphas and also the PositionInterpolators
		numberOfInterpolators = 29;

		// Create all the alphas for this
		alphas = new Alpha[numberOfInterpolators];

		alphas[28] = new Alpha(1, Alpha.INCREASING_ENABLE + Alpha.DECREASING_ENABLE, 0, 0, 250, 0, 0, 250, 0, 0);
		alphas[28].setStartTime(Long.MAX_VALUE);

		for (int a = 0; a < numberOfInterpolators - 1; a++) {
			alphas[a] = new Alpha(1, 500);
			alphas[a].setStartTime(Long.MAX_VALUE);
		}

		// Create the position Interpolators
		allPositionInterpolators = new PositionInterpolator[numberOfInterpolators];

		PositionInterpolator up1 = new PositionInterpolator(alphas[0], tgmPieceLateral, yaxis, 0.0f, 1.0f);
		PositionInterpolator up2 = new PositionInterpolator(alphas[1], tgmPieceLateral, yaxis, 0.0f, 2.0f);
		PositionInterpolator up3 = new PositionInterpolator(alphas[2], tgmPieceLateral, yaxis, 0.0f, 3.0f);
		PositionInterpolator up4 = new PositionInterpolator(alphas[3], tgmPieceLateral, yaxis, 0.0f, 4.0f);
		PositionInterpolator up5 = new PositionInterpolator(alphas[4], tgmPieceLateral, yaxis, 0.0f, 5.0f);
		PositionInterpolator up6 = new PositionInterpolator(alphas[5], tgmPieceLateral, yaxis, 0.0f, 6.0f);
		PositionInterpolator up7 = new PositionInterpolator(alphas[6], tgmPieceLateral, yaxis, 0.0f, 7.0f);

		PositionInterpolator Left1 = new PositionInterpolator(alphas[7], tgmPieceHorizontal, xaxis, 0.0f, -1.0f);
		PositionInterpolator Left2 = new PositionInterpolator(alphas[8], tgmPieceHorizontal, xaxis, 0.0f, -2.0f);
		PositionInterpolator Left3 = new PositionInterpolator(alphas[9], tgmPieceHorizontal, xaxis, 0.0f, -3.0f);
		PositionInterpolator Left4 = new PositionInterpolator(alphas[10], tgmPieceHorizontal, xaxis, 0.0f, -4.0f);
		PositionInterpolator Left5 = new PositionInterpolator(alphas[11], tgmPieceHorizontal, xaxis, 0.0f, -5.0f);
		PositionInterpolator Left6 = new PositionInterpolator(alphas[12], tgmPieceHorizontal, xaxis, 0.0f, -6.0f);
		PositionInterpolator Left7 = new PositionInterpolator(alphas[13], tgmPieceHorizontal, xaxis, 0.0f, -7.0f);

		PositionInterpolator Down1 = new PositionInterpolator(alphas[14], tgmPieceLateral, yaxis, 0.0f, -1.0f);
		PositionInterpolator Down2 = new PositionInterpolator(alphas[15], tgmPieceLateral, yaxis, 0.0f, -2.0f);
		PositionInterpolator Down3 = new PositionInterpolator(alphas[16], tgmPieceLateral, yaxis, 0.0f, -3.0f);
		PositionInterpolator Down4 = new PositionInterpolator(alphas[17], tgmPieceLateral, yaxis, 0.0f, -4.0f);
		PositionInterpolator Down5 = new PositionInterpolator(alphas[18], tgmPieceLateral, yaxis, 0.0f, -5.0f);
		PositionInterpolator Down6 = new PositionInterpolator(alphas[19], tgmPieceLateral, yaxis, 0.0f, -6.0f);
		PositionInterpolator Down7 = new PositionInterpolator(alphas[20], tgmPieceLateral, yaxis, 0.0f, -7.0f);

		PositionInterpolator Right1 = new PositionInterpolator(alphas[21], tgmPieceHorizontal, xaxis, 0.0f, 1.0f);
		PositionInterpolator Right2 = new PositionInterpolator(alphas[22], tgmPieceHorizontal, xaxis, 0.0f, 2.0f);
		PositionInterpolator Right3 = new PositionInterpolator(alphas[23], tgmPieceHorizontal, xaxis, 0.0f, 3.0f);
		PositionInterpolator Right4 = new PositionInterpolator(alphas[24], tgmPieceHorizontal, xaxis, 0.0f, 4.0f);
		PositionInterpolator Right5 = new PositionInterpolator(alphas[25], tgmPieceHorizontal, xaxis, 0.0f, 5.0f);
		PositionInterpolator Right6 = new PositionInterpolator(alphas[26], tgmPieceHorizontal, xaxis, 0.0f, 6.0f);
		PositionInterpolator Right7 = new PositionInterpolator(alphas[27], tgmPieceHorizontal, xaxis, 0.0f, 7.0f);

		PositionInterpolator Hop = new PositionInterpolator(alphas[28], tgmPieceHop, zaxis, 0.0f, 1.0f);

		allPositionInterpolators[0] = up1;
		allPositionInterpolators[1] = up2;
		allPositionInterpolators[2] = up3;
		allPositionInterpolators[3] = up4;
		allPositionInterpolators[4] = up5;
		allPositionInterpolators[5] = up6;
		allPositionInterpolators[6] = up7;
		allPositionInterpolators[7] = Left1;
		allPositionInterpolators[8] = Left2;
		allPositionInterpolators[9] = Left3;
		allPositionInterpolators[10] = Left4;
		allPositionInterpolators[11] = Left5;
		allPositionInterpolators[12] = Left6;
		allPositionInterpolators[13] = Left7;
		allPositionInterpolators[14] = Down1;
		allPositionInterpolators[15] = Down2;
		allPositionInterpolators[16] = Down3;
		allPositionInterpolators[17] = Down4;
		allPositionInterpolators[18] = Down5;
		allPositionInterpolators[19] = Down6;
		allPositionInterpolators[20] = Down7;
		allPositionInterpolators[21] = Right1;
		allPositionInterpolators[22] = Right2;
		allPositionInterpolators[23] = Right3;
		allPositionInterpolators[24] = Right4;
		allPositionInterpolators[25] = Right5;
		allPositionInterpolators[26] = Right6;
		allPositionInterpolators[27] = Right7;
		allPositionInterpolators[28] = Hop;

		for (int a = 0; a < numberOfInterpolators; a++)
			allPositionInterpolators[a].setSchedulingBounds(bs);

		for (int b = 0; b <= 6; b++)
			tgmPieceLateral.addChild(allPositionInterpolators[b]);
		for (int b = 14; b <= 20; b++)
			tgmPieceLateral.addChild(allPositionInterpolators[b]);
		for (int b = 7; b <= 13; b++)
			tgmPieceHorizontal.addChild(allPositionInterpolators[b]);
		for (int b = 21; b <= 27; b++)
			tgmPieceHorizontal.addChild(allPositionInterpolators[b]);

		tgmPieceHop.addChild(Hop);

		// Add position interpolator for capture event
		captureAlpha = new Alpha(1, Alpha.INCREASING_ENABLE, 0, 200, 300, 0, 0, 0, 0, 0);
		captureAlpha.setStartTime(Long.MAX_VALUE);
		captureUnderBoard = new PositionInterpolator(captureAlpha, tgmPiece, zaxis, 0.0f, -5.0f);
		captureUnderBoard.setSchedulingBounds(bs);

		tgmPiece.addChild(captureUnderBoard);

		tgmPiece.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	}

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
			for (int b = 0; b < 8; b++) {
				moveLogic[a][b] = 0;
			}

		int a, b;
		boolean flagEnd = false;

		// Test in the down right direction
		a = row + 1;
		b = column + 1;
		flagEnd = false;
		while (a < 8 && a > -1 && b < 8 && b > -1 && !flagEnd) {
			if (boardForLogicPlayer[a][b] != 0) {
				flagEnd = true;
			}
			if (boardForLogicPlayer[a][b] == 0) {
				moveLogic[a][b] = 1;
			}

			if (boardForLogicEnemy[a][b] != 0) {
				flagEnd = true;
			}

			a++;
			b++;
		}

		// Test in the up right direction
		a = row - 1;
		b = column + 1;
		flagEnd = false;
		while (a < 8 && a > -1 && b < 8 && b > -1 && !flagEnd) {
			if (boardForLogicPlayer[a][b] != 0) {
				flagEnd = true;
			}
			if (boardForLogicPlayer[a][b] == 0) {
				moveLogic[a][b] = 1;
			}

			if (boardForLogicEnemy[a][b] != 0) {
				flagEnd = true;
			}
			a--;
			b++;
		}

		// Test in the down left direction
		a = row + 1;
		b = column - 1;
		flagEnd = false;
		while (a < 8 && a > -1 && b < 8 && b > -1 && !flagEnd) {
			if (boardForLogicPlayer[a][b] != 0) {
				flagEnd = true;
			}
			if (boardForLogicPlayer[a][b] == 0) {
				moveLogic[a][b] = 1;
			}

			if (boardForLogicEnemy[a][b] != 0) {
				flagEnd = true;
			}
			a++;
			b--;
		}

		// Test in the up left direction
		a = row - 1;
		b = column - 1;
		flagEnd = false;
		while (a < 8 && a > -1 && b < 8 && b > -1 && !flagEnd) {
			if (boardForLogicPlayer[a][b] != 0) {
				flagEnd = true;
			}
			if (boardForLogicPlayer[a][b] == 0) {
				moveLogic[a][b] = 1;
			}

			if (boardForLogicEnemy[a][b] != 0) {
				flagEnd = true;
			}
			a--;
			b--;
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
		 * s.println("THE UPDATED MOVE LOGIC BOARD IS THE FOLLOWING: " );
		 * 
		 * for (int x = 0; x < 8; x++) { for (int y = 0; y < 8; y++) {
		 * System.out.print(moveLogic[x][y] + " "); } System.out.println(); }
		 */

	}

	public int[] selectPositionInterpolator(int rowInput, int colInput) {

		int[] returnInt = new int[3];
		returnInt[0] = 28;

		int rowDiff = row - rowInput;
		int colDiff = colInput - column;

		// First figure out how much up or down to go

		if (rowDiff > 0)
			returnInt[1] = rowDiff - 1;
		if (rowDiff < 0)
			returnInt[1] = Math.abs(rowDiff) + 13;

		if (colDiff > 0)
			returnInt[2] = colDiff + 20;
		if (colDiff < 0)
			returnInt[2] = Math.abs(colDiff) + 6;

		return returnInt;
	}

	public PositionInterpolator getPositionInterpolator(int a) {

		return allPositionInterpolators[a];
	}

	public String toString() {
		return "Bishop";
	}

	public void loadObject() {

		// Correct orrientation of default piece in the origin
		fixOrientation = new Transform3D();
		fixOrientation.rotX(Math.PI / 2);

		Transform3D scaleDown = new Transform3D();
		scaleDown.setScale(0.8);

		Transform3D putOnBoard = new Transform3D();
		putOnBoard.setTranslation(new Vector3f(0.0f, 0.0f, 0.8f));

		fixOrientation.mul(fixOrientation, scaleDown);

		fixOrientation.mul(putOnBoard, fixOrientation);

		tgPiece = new TransformGroup(fixOrientation);

		ObjectFile f = new ObjectFile(ObjectFile.RESIZE);
		Scene s = null;
		try {
			s = f.load("Assets/Bishop.obj");

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

		if (isWhite)
			parts.setAppearance(ChessPieces.whiteApp);
		else
			parts.setAppearance(ChessPieces.blackApp);

	}

	public void loadLocal() {
		Box bottom = new Box(0.3f, 0.3f, 0.3f, getColor(isWhite));
		Box top = new Box(0.1f, 0.1f, 0.1f, getColor(isWhite));

		// put the top box on top of the bottom box
		Transform3D tftop = new Transform3D();
		tftop.setTranslation(new Vector3f(0.0f, 0.0f, 0.3f));

		TransformGroup tgTop = new TransformGroup(tftop);
		tgTop.addChild(top);

		tgPiece.addChild(tgTop);
		tgPiece.addChild(bottom);
	}

}
