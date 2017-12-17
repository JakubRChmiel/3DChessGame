package clientAndFiles;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.*;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.media.j3d.*;

import javax.vecmath.*;

public class Knight extends ChessPieces {

	private PositionInterpolator up1;
	private PositionInterpolator up2;
	private PositionInterpolator down1;
	private PositionInterpolator down2;
	private PositionInterpolator right1;
	private PositionInterpolator right2;
	private PositionInterpolator left1;
	private PositionInterpolator left2;
	private PositionInterpolator hopAction;

	public Knight(int row, int col, boolean isWhite) {

		super(row, col, isWhite);

		try {
			loadPiece();
		}
		catch(Exception e) {
		loadLocal();
		}
		TransformGroup tgmPieceJump = new TransformGroup();
		tgmPieceJump.addChild(tgPiece);
		tgmPieceJump.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// Create all the 9 position interpolators a knight uses

		numberOfInterpolators = 9;

		alphas = new Alpha[numberOfInterpolators];

		alphas[0] = new Alpha(1, Alpha.INCREASING_ENABLE + Alpha.DECREASING_ENABLE, 0, 0, 250, 0, 0, 250, 0, 0);
		alphas[0].setStartTime(Long.MAX_VALUE);

		for (int z = 1; z < numberOfInterpolators; z++) {
			alphas[z] = new Alpha(1, 500);
			alphas[z].setStartTime(Long.MAX_VALUE);
		}

		hopAction = new PositionInterpolator(alphas[0], tgmPieceJump, zaxis, 0.0f, 2.0f);
		hopAction.setSchedulingBounds(bs);

		tgmPieceJump.addChild(hopAction);

		TransformGroup tgmPieceLateral = new TransformGroup();
		tgmPieceLateral.addChild(tgmPieceJump);
		tgmPieceLateral.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		up1 = new PositionInterpolator(alphas[1], tgmPieceLateral, yaxis, 0.0f, 1.0f);
		up2 = new PositionInterpolator(alphas[2], tgmPieceLateral, yaxis, 0.0f, 2.0f);
		down1 = new PositionInterpolator(alphas[3], tgmPieceLateral, yaxis, 0.0f, -1.0f);
		down2 = new PositionInterpolator(alphas[4], tgmPieceLateral, yaxis, 0.0f, -2.0f);

		TransformGroup tgmPieceHorizontal = new TransformGroup();
		tgmPieceHorizontal.addChild(tgmPieceLateral);
		tgmPieceHorizontal.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		right1 = new PositionInterpolator(alphas[5], tgmPieceHorizontal, xaxis, 0.0f, 1.0f);
		right2 = new PositionInterpolator(alphas[6], tgmPieceHorizontal, xaxis, 0.0f, 2.0f);
		left1 = new PositionInterpolator(alphas[7], tgmPieceHorizontal, xaxis, 0.0f, -1.0f);
		left2 = new PositionInterpolator(alphas[8], tgmPieceHorizontal, xaxis, 0.0f, -2.0f);

		up1.setSchedulingBounds(bs);
		up2.setSchedulingBounds(bs);
		down1.setSchedulingBounds(bs);
		down2.setSchedulingBounds(bs);
		right1.setSchedulingBounds(bs);
		right2.setSchedulingBounds(bs);
		left1.setSchedulingBounds(bs);
		left2.setSchedulingBounds(bs);

		allPositionInterpolators = new PositionInterpolator[numberOfInterpolators];
		allPositionInterpolators[0] = hopAction;
		allPositionInterpolators[1] = up1;
		allPositionInterpolators[2] = up2;
		allPositionInterpolators[3] = down1;
		allPositionInterpolators[4] = down2;
		allPositionInterpolators[5] = right1;
		allPositionInterpolators[6] = right2;
		allPositionInterpolators[7] = left1;
		allPositionInterpolators[8] = left2;

		for (int a = 1; a < 5; a++) {
			tgmPieceLateral.addChild(allPositionInterpolators[a]);
		}
		for (int a = 5; a < 9; a++) {
			tgmPieceHorizontal.addChild(allPositionInterpolators[a]);
		}
		tgmPiece = new TransformGroup();

		tgmPiece.addChild(tgmPieceHorizontal);

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

		// Set your move logic

		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++) {
				moveLogic[a][b] = 0;
			}
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++) {

				if (Math.abs(a - row) == 1 && Math.abs(b - column) == 2
						|| Math.abs(a - row) == 2 && Math.abs(b - column) == 1) {
					// If there's something within the range of a knight
					if (boardForLogicPlayer[a][b] != 0)
						moveLogic[a][b] = 0;
					else
						moveLogic[a][b] = 1;
				}
			}
		
		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {

				if (moveLogic[a][b] == 1) {
					// Call method to return whether moveLogic should be reverted or not
					if(!getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy,a,b))
						moveLogic[a][b] = 0;
				}
			}
		}
		
	}



	public int[] selectPositionInterpolator(int rowInput, int colInput) {

		int rowDiff = row - rowInput;
		int colDiff = colInput - column;

		int[] returnInt = new int[3];
		returnInt[0] = 0;
		// Add the 2 appropriate up down/ left right moves based on move input

		if (rowDiff >= 2)
			returnInt[1] = 2;
		else if (rowDiff >= 1)
			returnInt[1] = 1;
		else if (rowDiff >= -1)
			returnInt[1] = 3;
		else
			returnInt[1] = 4;

		if (colDiff >= 2)
			returnInt[2] = 6;
		else if (colDiff >= 1)
			returnInt[2] = 5;
		else if (colDiff >= -1)
			returnInt[2] = 7;
		else
			returnInt[2] = 8;

		return returnInt;
	}

	public PositionInterpolator getPositionInterpolator(int a) {

		return allPositionInterpolators[a];
	}

	public String toString() {
		return "Knight";
	}
	public void loadLocal() {
		// Create the transformGroup for the piece
				// Make it a short box

				Box knightBox = new Box(0.4f, 0.4f, 0.4f, getColor(isWhite));

				fixOrientation = new Transform3D();
				fixOrientation.setTranslation(new Vector3f(0.0f, 0.0f, 0.4f));

				tgPiece = new TransformGroup(fixOrientation);
				tgPiece.addChild(knightBox);
	}
	public void loadPiece() {
		fixOrientation = new Transform3D();
		fixOrientation.setTranslation(new Vector3f(0.0f, 0.0f, 0.6f));

		Transform3D rotate = new Transform3D();
		rotate.rotX(Math.PI/2);
		
		Transform3D rotateKnight = new Transform3D();
		if(isWhite)
			rotateKnight.rotZ(Math.PI/2);
		else
			rotateKnight.rotZ(-Math.PI/2);
		
		rotate.mul(rotateKnight,rotate);
		
		Transform3D scaleDown = new Transform3D();
		scaleDown.setScale(0.7);
		
		rotate.mul(rotate,scaleDown);
		
		fixOrientation.mul(fixOrientation,rotate);
		
		tgPiece = new TransformGroup(fixOrientation);
		
		ObjectFile f = new ObjectFile(ObjectFile.RESIZE);
		Scene s = null;
		try {
			s = f.load("Knight.obj");

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

}
