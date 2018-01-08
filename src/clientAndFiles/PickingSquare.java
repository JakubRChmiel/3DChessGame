package clientAndFiles;

import java.io.*;

import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Link;
import javax.print.attribute.standard.Media;
import javax.swing.JOptionPane;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;

import sun.audio.*;

// import com.sun.j3d.utils.geometry.*;

public class PickingSquare extends PickMouseBehavior {

	public static int rowInput, colInput;
	public static int firstMovePieceCol, firstMovePieceRow;
	public static boolean whiteToMove, keepPlaying;
	public static boolean firstMoveValid;
	public static boolean nextMoveValid;
	public static boolean lastMoveDone;
	public static boolean flagCheckPieceOnSquare;
	public static boolean checkAnyLegalMove;
	public static boolean lastMoveCapture;
	public static int[] positionInterpolatorIndex;
	public static int[] tempArray;
	public static boolean inPassEnabled;
	public static int inPassColumn;
	public static boolean flagSelectedPiecePromoted;
	public static boolean gameover;
	public static int firstMovePromotedPiece;

	// Constructor
	public PickingSquare(Canvas3D pCanvas, BranchGroup root, Bounds pBounds) {

		super(pCanvas, root, pBounds);
		setSchedulingBounds(pBounds);

		whiteToMove = true;
		firstMoveValid = false;
		nextMoveValid = false;
		keepPlaying = true;
		lastMoveDone = false;
		lastMoveCapture = false;
		flagCheckPieceOnSquare = false;
		checkAnyLegalMove = false;
		flagSelectedPiecePromoted = false;
		firstMovePromotedPiece = 0;
		gameover = false;

	}

	public void updateScene(int xpos, int ypos) {

		String rowChar, colChar;
		String location;
		// firstMoveValid = false;
		// Click on board, will find Link with userData

		Link pickedShape = null;
		pickCanvas.setShapeLocation(xpos, ypos);
		PickResult pResult = pickCanvas.pickClosest();

		// If something is clicked we'll try to load it as a pickedshape

		if (pResult != null)
			pickedShape = (Link) pResult.getNode(PickResult.LINK);

		// If the clicked thing is not a shape nothing will happen, if it is
		// we get row and column

		if (pickedShape != null) {
			location = (String) pickedShape.getUserData();
			rowChar = location.substring(0, 1);
			colChar = location.substring(1, 2);

			rowInput = Integer.parseInt(rowChar);
			colInput = Integer.parseInt(colChar);
			// System.out.println(rowInput);
			// System.out.println(colInput);
		}

		/**
		 * ********************************************************************
		 * Here we start defining the game logic
		 * ********************************************************************
		 */

		if (lastMoveDone) {

			if (!flagSelectedPiecePromoted)
				Chess3DGameClient.finalizeMove(firstMovePieceRow, firstMovePieceCol, positionInterpolatorIndex,
						!whiteToMove);
			else
				Chess3DGameClient.finalizeMove(firstMovePromotedPiece, rowInput, positionInterpolatorIndex,
						!whiteToMove);

			Chess3DGameClient.setViewerTransform(whiteToMove);
			Chess3DGameClient.resetAlpha();
			lastMoveDone = false;
			flagSelectedPiecePromoted = false;

		}

		// First check if the game is still going or not
		if (keepPlaying) {

			// If there was not a valid first move move inputed yet go here
			if (!firstMoveValid) {

				// Set the flag to false for checking if there is a non captured
				// piece with a legal move to play
				flagCheckPieceOnSquare = false;
				checkAnyLegalMove = false;
				flagSelectedPiecePromoted = false;

				// Scan the chess pieces array by index to see if there's a
				// piece on that square which was not captured
				for (int a = 0; a < 8; a++)
					for (int b = 0; b < 8; b++) {
						if (Chess3DGameClient.checkPieceSquare(a, b, rowInput, colInput, whiteToMove)) {

							flagCheckPieceOnSquare = true;
						}
					}

				if (flagCheckPieceOnSquare) {

					Chess3DGameClient.updateBoardForLogicEnemy(whiteToMove);
					Chess3DGameClient.updateBoardForLogic();

					if (!flagSelectedPiecePromoted)
						Chess3DGameClient.setMoveLogic(firstMovePieceRow, firstMovePieceCol, whiteToMove, inPassColumn,
								inPassEnabled);
					else
						Chess3DGameClient.setMoveLogic(firstMovePromotedPiece, rowInput, whiteToMove, inPassColumn,
								inPassEnabled);

					// Test if there are any legal moves for this piece

					for (int a = 0; a < 8; a++)
						for (int b = 0; b < 8; b++) {
							if (Chess3DGameClient.getMoveLogic(firstMovePieceRow, firstMovePieceCol, a, b)) {
								checkAnyLegalMove = true;
							}
						}
				}
				if(checkAnyLegalMove) {
					// change the material of the selected square to selection color
					
					
				}

				firstMoveValid = checkAnyLegalMove;

			} else {

				firstMoveValid = false;

				if (!flagSelectedPiecePromoted)
					nextMoveValid = Chess3DGameClient.checkNextMoveLegal(firstMovePieceRow, firstMovePieceCol, rowInput,
							colInput, whiteToMove);
				else
					nextMoveValid = Chess3DGameClient.checkNextMoveLegal(firstMovePromotedPiece, rowInput, rowInput,
							colInput, whiteToMove);

				if (nextMoveValid) {

					if (!flagSelectedPiecePromoted)
						positionInterpolatorIndex = Chess3DGameClient.selectPositionInterpolator(firstMovePieceRow,
								firstMovePieceCol, rowInput, colInput, whiteToMove);
					else
						positionInterpolatorIndex = Chess3DGameClient.selectPositionInterpolator(firstMovePromotedPiece,
								rowInput, rowInput, colInput, whiteToMove);

					inPassEnabled = false;

					if (!flagSelectedPiecePromoted)
						Chess3DGameClient.setMove(firstMovePieceRow, firstMovePieceCol, rowInput, colInput,
								positionInterpolatorIndex, whiteToMove);
					else
						Chess3DGameClient.setMove(firstMovePromotedPiece, rowInput, rowInput, colInput,
								positionInterpolatorIndex, whiteToMove);

					Chess3DGameClient.updateBoardForLogicEnemy(!whiteToMove);
					Chess3DGameClient.updateBoardForLogic();

					int resultTest = Chess3DGameClient.postMoveTest(!whiteToMove, inPassColumn, inPassEnabled);
					boolean inCheck = false;

					inCheck = Chess3DGameClient.inCheck(!whiteToMove);
					if (inCheck && resultTest == -1) {
						System.out.println("Checkmate");

						if (whiteToMove)
							JOptionPane.showMessageDialog(null, "White won by checkmate", "White won by checkmate",
									JOptionPane.INFORMATION_MESSAGE);
						else
							JOptionPane.showMessageDialog(null, "Black won by checkmate", "Black won by checkmate",
									JOptionPane.INFORMATION_MESSAGE);
						gameover = true;
						keepPlaying = false;
					}
					if (!inCheck && resultTest == -1) {
						System.out.println("Stalemate");
						JOptionPane.showMessageDialog(null, "Stalemate");
						gameover = true;
						keepPlaying = false;
					}
					if (resultTest != -1 && inCheck) {
						// JOptionPane.showMessageDialog(null, "check");

						try {
							String wav_file = "Assets/check.wav";
							InputStream in = new FileInputStream(wav_file);

							AudioStream audio = new AudioStream(in);
							AudioPlayer.player.start(audio);
						} catch (Exception e) {
							System.out.println("File not found");
						}

					}

					// Reverse who's move it is
					whiteToMove = !whiteToMove;
					// System.out.println("CheckPoint: Reverting player to go");

					// Set last move done to true;
					lastMoveDone = true;

				}
			}

		}
		if (gameover) {

			gameover = false;
			lastMoveDone = false;
			keepPlaying = true;
			whiteToMove = true;

			int answer;
			answer = JOptionPane.showConfirmDialog(null, "Would you like to play again?",
					"Would you like to play again?", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.NO_OPTION) {
				System.exit(0);
			}

			if (!flagSelectedPiecePromoted) {
				// reset the alpha for piece
				Chess3DGameClient.resetAlphaIndividual(firstMovePieceRow, firstMovePieceCol, rowInput, colInput);
				System.out.println("RESET THE ALPHA PICK S GAMEOVER");
			}

			Chess3DGameClient.setViewerTransform(whiteToMove);
			Chess3DGameClient.resetAlpha();

			// Chess3DGameClient.setViewerTransform(false);

			// Currently board is correct when white wins, incorrect when black
			// wins

			Chess3DGameClient.resetGame();

		}

	}

	/*
	 * ***************************************************
	 * ***************************************************
	 *  Mutator Method
	 * ***************************************************
	 * ***************************************************
	 */

	public static void setFirstMoveIndicies(int row, int col) {
		firstMovePieceRow = row;
		firstMovePieceCol = col;
	}

	public static void setFirstMoveIndexPromoted(int row) {
		firstMovePromotedPiece = row;
	}

	public static void flagSelectedPiecePromoted() {
		flagSelectedPiecePromoted = true;
	}

	public static void setLastMoveCapture(boolean capture) {
		lastMoveCapture = capture;
	}

	public static void collectInPassInfo(int inPassCol) {
		inPassEnabled = true;
		inPassColumn = inPassCol;
	}
}
