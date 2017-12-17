package clientAndFiles;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Material;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public abstract class ChessPieces {

	protected int row, column;
	protected final int COLUMN_INITIAL, ROW_INITIAL;
	protected static Appearance whiteApp;
	protected static Appearance blackApp;
	protected static Color3f white, black, weakWhite, weakBlack;
	protected boolean isWhite;
	protected boolean isKing;
	protected boolean isCaptured;
	protected Transform3D fixOrientation;
	protected Transform3D moveToSquare;
	protected Transform3D netTransform; // Might not be necessary
	protected TransformGroup tgmPiece;
	protected TransformGroup tgPiece;
	protected int[][] moveLogic;
	protected Alpha[] alphas;
	protected Alpha captureAlpha;
	protected Transform3D moveToGraveyard;
	protected PositionInterpolator captureUnderBoard;
	protected static Transform3D yaxis;
	protected static Transform3D upRightAxis;
	protected static Transform3D upLeftAxis;
	protected static Transform3D xaxis;
	protected static Transform3D zaxis;
	protected static BoundingSphere bs;
	protected PositionInterpolator[] allPositionInterpolators;
	protected int numberOfInterpolators;
	protected boolean rookQueenMoved, rookKingMoved, kingMoved;

	public ChessPieces(int rowInitial, int columnInitial, boolean isWhite) {

		// Set final values for initial position
		COLUMN_INITIAL = columnInitial;
		ROW_INITIAL = rowInitial;

		// Set starting values for variable position
		column = COLUMN_INITIAL;
		row = ROW_INITIAL;

		// Set for object whether it is white or black
		this.isWhite = isWhite;

		// Set that object is not captured
		isCaptured = false;

		white = new Color3f(0.55f, 0.55f, 0.55f);
		black = new Color3f(0.42f, 0.36f, 0.33f);
		weakWhite = new Color3f(0.3f,0.3f,0.3f);
		weakBlack = new Color3f(0.21f,0.18f,0.16f);
		
		Color3f trueBlack = new Color3f(0.0f,0.0f,0.0f);
		float shininessBlack = 10.0f;
		float shininessWhite = 20.0f;

		whiteApp = new Appearance();
		whiteApp.setMaterial(new Material(white, white, white, white, shininessWhite));
		blackApp = new Appearance();
		blackApp.setMaterial(new Material(black, weakBlack, black, black, shininessBlack));

		// Initialize move logic for the piece
		moveLogic = new int[8][8];
		for (int a = 0; a < 8; a++)
			for (int b = 0; b < 8; b++)
				moveLogic[a][b] = 1;

		yaxis = new Transform3D();
		yaxis.rotZ(Math.PI / 2);

		xaxis = new Transform3D();

		upLeftAxis = new Transform3D();
		upLeftAxis.rotZ(3 * Math.PI / 4);

		upRightAxis = new Transform3D();
		upRightAxis.rotZ(Math.PI / 4);

		zaxis = new Transform3D();
		zaxis.rotY(-Math.PI / 2);
		// The object is expected to be positioned correctly
		// This moves the object from origin into it's square from row and
		// column

		moveToSquare = new Transform3D();
		moveToSquare.setTranslation(new Vector3f((float) column, (float) 7.0f - row, 0.0f));

		bs = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);

		moveToGraveyard = new Transform3D();

	}

	/* ***********************************************************************
	 * ***********************************************************************
	 * Mutator Methods
	 * ***********************************************************************
	 * ***********************************************************************
	 */

	public void setRow(int row) {
		// Used to update position throughout the game
		this.row = row;
	}

	public void setColumn(int column) {
		// Used to update position throughout the game
		this.column = column;
	}

	public void setTransform() {
		tgmPiece.setTransform(moveToSquare);
	}

	public void updateTransform() {
		moveToSquare.setTranslation(new Vector3f((float) column, (float) 7.0f - row, 0.0f));
	//	System.out.println("AT UPDATE TRANSFORM ROW AND COLUMN ARE: " + row + " " + column);

	}

	public abstract void setMoveLogic(int[][] boardForLogic, int[][] boardForLogicEnemy, int inPassCol,
			boolean inPassEnabled);

	public void setAlphaStart(int selection) {
		alphas[selection].setStartTime(System.currentTimeMillis() - alphas[selection].getTriggerTime());
	}

	public void setAlphaStart(int[] selection) {

		int length = selection.length;

		for (int a = 0; a < length; a++) {
			alphas[selection[a]].setStartTime(System.currentTimeMillis() - alphas[selection[a]].getTriggerTime());

		//	System.out.println("Supposedly I actually changed the alphas for: " + selection[a]);
		}
	}

	public void resetAlpha(int[] selection) {
		int length = selection.length;
		for (int a = 0; a < length; a++)
			alphas[selection[a]].setStartTime(Long.MAX_VALUE);
		

		

	}
	public void getCapturedEvent() {
		this.isCaptured = true;
	}

	public void finalizeCapture(int countWhiteDead, int countBlackDead) {
		captureAlpha.setStartTime(Long.MAX_VALUE);

		// Now set appropriate transform based on number of corresponding color
		// dead
		if (this.isWhite) {
			if (countWhiteDead <= 8) {
				moveToGraveyard.setTranslation(new Vector3f(-1.0f, 8.0f - (float) countWhiteDead, 0.0f));
				// moveToGraveyard.setTranslation(new Vector3f(0.0f, 0.0f,
				// 1.0f));
		//		System.out.println(" WHITE DEAD CHOSEN COUNTWHITEDEAD = " + countWhiteDead);
			} else {
				moveToGraveyard.setTranslation(new Vector3f(-2.0f, 16.0f - (float) countWhiteDead, 0.0f));
				// moveToGraveyard.setTranslation(new Vector3f(0.0f, 0.0f,
				// 1.0f));
		//		System.out.println(" WHITE DEAD CHOSEN COUNTWHITEDEAD = " + countWhiteDead);
			}
		}
		if (!this.isWhite) {
			if (countBlackDead <= 8) {
				moveToGraveyard.setTranslation(new Vector3f(8.0f, -1.0f + (float) countBlackDead, 0.0f));
				// moveToGraveyard.setTranslation(new Vector3f(0.0f, 0.0f,
				// 1.0f));
		//		System.out.println(" BLACK DEAD CHOSEN COUNTBLACKDEAD = " + countBlackDead);
			} else {
				moveToGraveyard.setTranslation(new Vector3f(9.0f, -9.0f + (float) countBlackDead, 0.0f));
				// moveToGraveyard.setTranslation(new Vector3f(0.0f, 0.0f,
				// 1.0f));
		//		System.out.println(" BLACK DEAD CHOSEN COUNTBLACKDEAD = " + countBlackDead);
			}
		}
	}



	public int[][] generateInfluenceArray(int[][] tempBoardForLogic, int[][] tempBoardForLogicPlayer,
			int[][] tempBoardForLogicEnemy) {

		int[][] influenceArray = new int[8][8];

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				influenceArray[a][b] = 0;
			}
		}

		// Scan the enemy board. if there's a piece generate the influence of
		// that and add it to the influence Array;

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {

				if (tempBoardForLogicEnemy[a][b] != 0) {

					// influence of pawns is set correctly

					if (tempBoardForLogicEnemy[a][b] == 1) {
						if (isThePieceWhite()) {

							if (b > 0) {
								influenceArray[a + 1][b - 1] = influenceArray[a + 1][b - 1] + 1;
							}
							if (b < 7) {
								influenceArray[a + 1][b + 1] = influenceArray[a + 1][b + 1] + 1;
							}

						} else {

							if (b > 0) {
								influenceArray[a - 1][b - 1] = influenceArray[a - 1][b - 1] + 1;
							}
							if (b < 7) {
								influenceArray[a - 1][b + 1] = influenceArray[a - 1][b + 1] + 1;
							}
						}

					}
					// Set influence of enemy king
					// Doesn't matter if there's anything there or not, just if
					// in bounds, a and b are location of king
					if (tempBoardForLogicEnemy[a][b] == 6) {

						if (a > 0)
							influenceArray[a - 1][b] = influenceArray[a - 1][b] + 1;
						if (a < 7)
							influenceArray[a + 1][b] = influenceArray[a + 1][b] + 1;
						if (b > 0)
							influenceArray[a][b - 1] = influenceArray[a][b - 1] + 1;
						if (b < 7)
							influenceArray[a][b + 1] = influenceArray[a][b + 1] + 1;

						// diagonals
						if (a > 0 && b > 0)
							influenceArray[a - 1][b - 1] = influenceArray[a - 1][b - 1] + 1;
						if (a > 0 && b < 7)
							influenceArray[a - 1][b + 1] = influenceArray[a - 1][b + 1] + 1;
						if (a < 7 && b > 0)
							influenceArray[a + 1][b - 1] = influenceArray[a + 1][b - 1] + 1;
						if (a < 7 && b < 7)
							influenceArray[a + 1][b + 1] = influenceArray[a + 1][b + 1] + 1;
					}
					// Set influence of the knights

					if (tempBoardForLogicEnemy[a][b] == 3) {

						// Must do all 8 cases separately to keep in bounds
						// a and b are location of the knight

						// down 2 right 1
						if (a <= 5 && b < 7) {
							influenceArray[a + 2][b + 1] = influenceArray[a + 2][b + 1] + 1;
						}
						// down 2 left 1
						if (a <= 5 && b > 0) {
							influenceArray[a + 2][b - 1] = influenceArray[a + 2][b - 1] + 1;
						}
						// up 2 right 1
						if (a > 1 && b < 7) {
							influenceArray[a - 2][b + 1] = influenceArray[a - 2][b + 1] + 1;
						}
						// up 2 left 1
						if (a > 1 && b > 0) {
							influenceArray[a - 2][b - 1] = influenceArray[a - 2][b - 1] + 1;
						}
						// down 1 right 2
						if (a < 7 && b < 6) {
							influenceArray[a + 1][b + 2] = influenceArray[a + 1][b + 2] + 1;
						}
						// down 2 left 1
						if (a < 7 && b > 1) {
							influenceArray[a + 1][b - 2] = influenceArray[a + 1][b - 2] + 1;
						}
						// up 2 right 1
						if (a > 0 && b < 6) {
							influenceArray[a - 1][b + 2] = influenceArray[a - 1][b + 2] + 1;
						}
						// up 2 left 1
						if (a > 0 && b > 1) {
							influenceArray[a - 1][b - 2] = influenceArray[a - 1][b - 2] + 1;
						}
					}

					// influence of rooks (use while loop to keep moving down
					// the row and then column
					if (tempBoardForLogicEnemy[a][b] == 2) {
						int tempRow = a;
						int tempCol = b;

						// set influence for moving down
						if (a != 7) {
							tempRow++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempRow < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving down
						tempRow = a;
						tempCol = b;
						if (a != 0) {
							tempRow--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempRow > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving right
						tempRow = a;
						tempCol = b;
						if (b != 7) {
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving left
						tempRow = a;
						tempCol = b;
						if (b != 0) {
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
					}
					// Influence of bishops
					if (tempBoardForLogicEnemy[a][b] == 4) {
						int tempRow, tempCol;

						// Add down right influence
						tempRow = a;
						tempCol = b;
						if (a < 7 && b < 7) {
							tempRow++;
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow < 7 && tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add down left influence
						tempRow = a;
						tempCol = b;
						if (a < 7 && b > 0) {
							tempRow++;
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow < 7 && tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add up right influence
						tempRow = a;
						tempCol = b;
						if (a > 0 && b < 7) {
							tempRow--;
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow > 0 && tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add up left influence
						tempRow = a;
						tempCol = b;
						if (a > 0 && b > 0) {
							tempRow--;
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow > 0 && tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
					}
					// Set logic for queen
					if (tempBoardForLogicEnemy[a][b] == 5) {

						int tempRow = a;
						int tempCol = b;

						// set influence for moving down
						if (a != 7) {
							tempRow++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempRow < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving down
						tempRow = a;
						tempCol = b;
						if (a != 0) {
							tempRow--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempRow > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving right
						tempRow = a;
						tempCol = b;
						if (b != 7) {
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

						// set influence for moving left
						tempRow = a;
						tempCol = b;
						if (b != 0) {
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;

							while (tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add down right influence
						tempRow = a;
						tempCol = b;
						if (a < 7 && b < 7) {
							tempRow++;
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow < 7 && tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add down left influence
						tempRow = a;
						tempCol = b;
						if (a < 7 && b > 0) {
							tempRow++;
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow < 7 && tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow++;
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add up right influence
						tempRow = a;
						tempCol = b;
						if (a > 0 && b < 7) {
							tempRow--;
							tempCol++;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow > 0 && tempCol < 7 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								tempCol++;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}
						// Add up left influence
						tempRow = a;
						tempCol = b;
						if (a > 0 && b > 0) {
							tempRow--;
							tempCol--;
							influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							while (tempRow > 0 && tempCol > 0 && tempBoardForLogic[tempRow][tempCol] == 0) {
								tempRow--;
								tempCol--;
								influenceArray[tempRow][tempCol] = influenceArray[tempRow][tempCol] + 1;
							}
						}

					}

				}
			}
		}

		return influenceArray;

	}
	public void setMoveYet(int moveCode) {

		if (moveCode == 1)
			rookQueenMoved = true;
		if (moveCode == 2)
			kingMoved = true;
		if (moveCode == 3)
			rookKingMoved = true;

	}

	/*
	 * ***************************************************************
	 * ***************************************************************
	 *  Accessor Methods 
	 * ***************************************************************
	 * ***************************************************************
	 */
	
	public Transform3D getCapturedTransform() {
		return moveToGraveyard;
	}
	
	public int getRow() {
		// Used to return current position on the board
		return row;
	}

	public int getColumn() {
		// Used to return column position of piece on the board
		return column;
	}

	public Transform3D getTransform() {
		return moveToSquare;
	}

	public Appearance getColor(boolean isWhite) {

		if (isWhite)
			return whiteApp;
		else
			return blackApp;
	}

	public boolean isThePieceWhite() {
		return isWhite;
	}

	public TransformGroup getTGM() {
		return tgmPiece;
	}

	public boolean getIsCaptured() {
		return isCaptured;
	}

	public abstract int[] selectPositionInterpolator(int row, int col);

	public abstract PositionInterpolator getPositionInterpolator(int a);

	public abstract String toString();

	public boolean getCheckConditionLogic(int[][] boardForLogic, int[][] boardForLogicPlayer,
			int[][] boardForLogicEnemy, int rowMove, int colMove) {
		// True means that the move is safe/ no check
		// False means that the move causes check and is not allowed

		int[][] tempBoardForLogic = new int[8][8];
		int[][] tempBoardForLogicPlayer = new int[8][8];
		int[][] tempBoardForLogicEnemy = new int[8][8];

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				tempBoardForLogic[x][y] = boardForLogic[x][y];
				tempBoardForLogicPlayer[x][y] = boardForLogicPlayer[x][y];
				tempBoardForLogicEnemy[x][y] = boardForLogicEnemy[x][y];
			}
		}

		// update the boards

		if (toString() == "Pawn") {
			tempBoardForLogic[rowMove][colMove] = 1;
			tempBoardForLogicPlayer[rowMove][colMove] = 1;
		} else if (toString() == "Rook") {
			tempBoardForLogic[rowMove][colMove] = 2;
			tempBoardForLogicPlayer[rowMove][colMove] = 2;
		} else if (toString() == "Knight") {
			tempBoardForLogic[rowMove][colMove] = 3;
			tempBoardForLogicPlayer[rowMove][colMove] = 3;
		} else if (toString() == "Bishop") {
			tempBoardForLogic[rowMove][colMove] = 4;
			tempBoardForLogicPlayer[rowMove][colMove] = 4;
		} else if (toString() == "Queen") {
			tempBoardForLogic[rowMove][colMove] = 5;
			tempBoardForLogicPlayer[rowMove][colMove] = 5;
		} else if (toString() == "King") {
			tempBoardForLogic[rowMove][colMove] = 6;
			tempBoardForLogicPlayer[rowMove][colMove] = 6;
		} else
			tempBoardForLogic[rowMove][colMove] = 0;

		tempBoardForLogic[row][column] = 0;
		tempBoardForLogicPlayer[row][column] = 0;
		tempBoardForLogicEnemy[rowMove][colMove] = 0;

		// Generate the influence board based on this updated board
		// Perhaps create this as a public static method in the chess pieces
		// where you send it a all the boards and it gives back an influence
		// array

		int kingRow;
		int kingCol;

		kingRow = 0;
		kingCol = 0;

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {

				if (tempBoardForLogicPlayer[a][b] == 6) {
					kingRow = a;
					kingCol = b;
				}
			}
		}

		int[][] tempInfluenceBoard = new int[8][8];

		// call method of chess pieces that will give you an influence board
		tempInfluenceBoard = generateInfluenceArray(tempBoardForLogic, tempBoardForLogicPlayer, tempBoardForLogicEnemy);

		boolean isLegalMove;
		if (tempInfluenceBoard[kingRow][kingCol] != 0)
			isLegalMove = false;
		else
			isLegalMove = true;

		return isLegalMove;
	}

	public boolean getMoveLogic(int row, int col) {

		boolean isOne = moveLogic[row][col] == 1;
		return isOne;
	}

	public int[][] getMoveLogicArray() {
		return moveLogic;
	}



	public boolean getKingMoved() {
		return kingMoved;
	}
	public void resetGameNonPromoted() {
		row = ROW_INITIAL;
		column = COLUMN_INITIAL;
		isCaptured = false;
		updateTransform();
		rookQueenMoved = false;
		rookKingMoved = false;
		kingMoved = false;
		
		
	}
	public void setDeadPromoted() {
		moveToSquare.setTranslation(new Vector3f(0.0f,0.0f,-5.0f));
	}
	public int numberOfInterpolators() {
		int returnInt = 0;
		returnInt = allPositionInterpolators.length;
		return returnInt;
	}

}
