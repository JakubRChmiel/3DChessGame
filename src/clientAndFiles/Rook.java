package clientAndFiles;

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
import com.sun.j3d.utils.geometry.Sphere;

public class Rook extends ChessPieces {

	private boolean canCastle;

	// Consructor Method

	public Rook(int row, int column, boolean isWhite) {

		super(row, column, isWhite);

		canCastle = true;

		// Create the transformGroup for the piece
		
		try {
			loadPiece();
		}
		catch(Exception e) {
		loadLocal();
		}

		tgmPiece = new TransformGroup();
		tgmPiece.addChild(tgPiece);

		// number of interpolators required
		numberOfInterpolators = 28;

		// Create all the alphas for this
		alphas = new Alpha[numberOfInterpolators];
		for (int a = 0; a < numberOfInterpolators; a++) {
			alphas[a] = new Alpha(1, 500);
			alphas[a].setStartTime(Long.MAX_VALUE);
		}

		// Create the position Interpolators
		allPositionInterpolators = new PositionInterpolator[numberOfInterpolators];

		PositionInterpolator up1 = new PositionInterpolator(alphas[0], tgmPiece, yaxis, 0.0f, 1.0f);
		PositionInterpolator up2 = new PositionInterpolator(alphas[1], tgmPiece, yaxis, 0.0f, 2.0f);
		PositionInterpolator up3 = new PositionInterpolator(alphas[2], tgmPiece, yaxis, 0.0f, 3.0f);
		PositionInterpolator up4 = new PositionInterpolator(alphas[3], tgmPiece, yaxis, 0.0f, 4.0f);
		PositionInterpolator up5 = new PositionInterpolator(alphas[4], tgmPiece, yaxis, 0.0f, 5.0f);
		PositionInterpolator up6 = new PositionInterpolator(alphas[5], tgmPiece, yaxis, 0.0f, 6.0f);
		PositionInterpolator up7 = new PositionInterpolator(alphas[6], tgmPiece, yaxis, 0.0f, 7.0f);

		PositionInterpolator Left1 = new PositionInterpolator(alphas[7], tgmPiece, xaxis, 0.0f, -1.0f);
		PositionInterpolator Left2 = new PositionInterpolator(alphas[8], tgmPiece, xaxis, 0.0f, -2.0f);
		PositionInterpolator Left3 = new PositionInterpolator(alphas[9], tgmPiece, xaxis, 0.0f, -3.0f);
		PositionInterpolator Left4 = new PositionInterpolator(alphas[10], tgmPiece, xaxis, 0.0f, -4.0f);
		PositionInterpolator Left5 = new PositionInterpolator(alphas[11], tgmPiece, xaxis, 0.0f, -5.0f);
		PositionInterpolator Left6 = new PositionInterpolator(alphas[12], tgmPiece, xaxis, 0.0f, -6.0f);
		PositionInterpolator Left7 = new PositionInterpolator(alphas[13], tgmPiece, xaxis, 0.0f, -7.0f);

		PositionInterpolator Down1 = new PositionInterpolator(alphas[14], tgmPiece, yaxis, 0.0f, -1.0f);
		PositionInterpolator Down2 = new PositionInterpolator(alphas[15], tgmPiece, yaxis, 0.0f, -2.0f);
		PositionInterpolator Down3 = new PositionInterpolator(alphas[16], tgmPiece, yaxis, 0.0f, -3.0f);
		PositionInterpolator Down4 = new PositionInterpolator(alphas[17], tgmPiece, yaxis, 0.0f, -4.0f);
		PositionInterpolator Down5 = new PositionInterpolator(alphas[18], tgmPiece, yaxis, 0.0f, -5.0f);
		PositionInterpolator Down6 = new PositionInterpolator(alphas[19], tgmPiece, yaxis, 0.0f, -6.0f);
		PositionInterpolator Down7 = new PositionInterpolator(alphas[20], tgmPiece, yaxis, 0.0f, -7.0f);

		PositionInterpolator Right1 = new PositionInterpolator(alphas[21], tgmPiece, xaxis, 0.0f, 1.0f);
		PositionInterpolator Right2 = new PositionInterpolator(alphas[22], tgmPiece, xaxis, 0.0f, 2.0f);
		PositionInterpolator Right3 = new PositionInterpolator(alphas[23], tgmPiece, xaxis, 0.0f, 3.0f);
		PositionInterpolator Right4 = new PositionInterpolator(alphas[24], tgmPiece, xaxis, 0.0f, 4.0f);
		PositionInterpolator Right5 = new PositionInterpolator(alphas[25], tgmPiece, xaxis, 0.0f, 5.0f);
		PositionInterpolator Right6 = new PositionInterpolator(alphas[26], tgmPiece, xaxis, 0.0f, 6.0f);
		PositionInterpolator Right7 = new PositionInterpolator(alphas[27], tgmPiece, xaxis, 0.0f, 7.0f);

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

		for (int a = 0; a < numberOfInterpolators; a++) {

			allPositionInterpolators[a].setSchedulingBounds(bs);
			tgmPiece.addChild(allPositionInterpolators[a]);
		}
		
		// Add position interpolator for capture event
				captureAlpha = new Alpha(1, Alpha.INCREASING_ENABLE,0,200,300,0,0,0,0,0);
				captureAlpha.setStartTime(Long.MAX_VALUE);
				captureUnderBoard = new PositionInterpolator(captureAlpha, tgmPiece, zaxis, 0.0f,-5.0f);
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
		boolean flagEnd;
		
		// Moving up and down
		
		// for moving up
		
		a = row - 1;
		flagEnd = false;
		
		while(a<8 && a>-1 &&!flagEnd) {
			if (boardForLogicPlayer[a][column] != 0) {
				flagEnd = true;
			}
			if(boardForLogicPlayer[a][column] ==0) {
				moveLogic[a][column] = 1;
			}
			if (boardForLogicEnemy[a][column] != 0) {
				flagEnd = true;
			}
			
			a--;
		}
		// for moving up
		
				a = row + 1;
				flagEnd = false;
				
				while(a<8 && a>-1 &&!flagEnd) {
					if (boardForLogicPlayer[a][column] != 0) {
						flagEnd = true;
					}
					if(boardForLogicPlayer[a][column] ==0) {
						moveLogic[a][column] = 1;
					}
					if (boardForLogicEnemy[a][column] != 0) {
						flagEnd = true;
					}
					
					a++;
				}
				// for moving right
				
				b = column + 1;
				flagEnd = false;
				
				while(b<8 && b>-1 &&!flagEnd) {
					if (boardForLogicPlayer[row][b] != 0) {
						flagEnd = true;
					}
					if(boardForLogicPlayer[row][b] ==0) {
						moveLogic[row][b] = 1;
					}
					if (boardForLogicEnemy[row][b] != 0) {
						flagEnd = true;
					}
					
					b++;
				}
				// for moving right
				
				b = column - 1;
				flagEnd = false;
				
				while(b<8 && b>-1 &&!flagEnd) {
					if (boardForLogicPlayer[row][b] != 0) {
						flagEnd = true;
					}
					if(boardForLogicPlayer[row][b] ==0) {
						moveLogic[row][b] = 1;
					}
					if (boardForLogicEnemy[row][b] != 0) {
						flagEnd = true;
					}
					
					b--;
				}
				
		
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {

						if (moveLogic[x][y] == 1) {
							// Call method to return whether moveLogic should be reverted or not
							if(!getCheckConditionLogic(boardForLogic, boardForLogicPlayer, boardForLogicEnemy,x,y))
								moveLogic[x][y] = 0;
						}
					}
				}
/*
				System.out.println("THE UPDATED MOVE LOGIC BOARD IS THE FOLLOWING: ");
				
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						System.out.print(moveLogic[x][y] + " ");
					}
					System.out.println();
				}
				*/
				
		
	}



	public int[] selectPositionInterpolator(int rowInput, int colInput) {

		int[] returnInt = new int[1];
		
		int select, rowDiff, colDiff;
		rowDiff = row - rowInput; // so that positive is up
		colDiff = colInput - column; // so that positive is right


		if (rowDiff == 0) {

			if (colDiff > 0)
				// Move Right conditions
				select = colDiff + 20;
			else
				// Move Left conditions
				select = (int) Math.abs(colDiff) + 6;
		} else {

			if (rowDiff > 0)
				// Move up conditions
				select = rowDiff - 1;
			else
				// Move down conditions
				select = Math.abs(rowDiff) + 13;
		}
		returnInt[0] = select;
		return returnInt;
	}

	public PositionInterpolator getPositionInterpolator(int selection) {
		return allPositionInterpolators[selection];
	}

	public String toString() {
		String name = "Rook";
		return name;
	}

	public void loadLocal() {
		// Create the transformGroup for the piece

				Sphere rookSphere = new Sphere(0.3f, getColor(isWhite));

				fixOrientation = new Transform3D();
				fixOrientation.setTranslation(new Vector3f(0.0f, 0.0f, 0.3f));

				tgPiece = new TransformGroup(fixOrientation);
				tgPiece.addChild(rookSphere);
	}
	public void loadPiece() {
		// Create the transformGroup for the piece

				Sphere rookSphere = new Sphere(0.3f, getColor(isWhite));

				fixOrientation = new Transform3D();
				fixOrientation.setTranslation(new Vector3f(0.0f, 0.0f, 0.6f));
				
				Transform3D rotate = new Transform3D();
				rotate.rotX(Math.PI/2);

				Transform3D scaleDown = new Transform3D();
				scaleDown.setScale(.6);
				
				rotate.mul(rotate,scaleDown);
				
				fixOrientation.mul(fixOrientation, rotate);
				
				tgPiece = new TransformGroup(fixOrientation);
				
				
				ObjectFile f = new ObjectFile(ObjectFile.RESIZE);
				Scene s = null;
				try {
					s = f.load("Rook.obj");

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
