package clientAndFiles;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Link;
import javax.media.j3d.Material;
import javax.media.j3d.PointLight;
import javax.media.j3d.PositionInterpolator;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.SharedGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
//import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Chess3DGameClient extends JFrame {

	/**
	 * 3D 1v1 Chess Game.
	 * 
	 * Full defines all logic of game regarding check, checkmate, legal moves
	 * etc.
	 * 
	 * Current Objectives: Full define pawn moves entailing:
	 * 
	 * When piece is allowed to capture Transform/ procedure for piece getting
	 * captured Setting all move logic after each move
	 * 
	 */

	public static final long serialVersionUID = 5L; // Required by JFrame for
													// some reason
	public static Canvas3D myCanvas; // Canvas from which universe created
	public static ChessPieces[][] piecesArray; // Array of pieces excluding
												// promotion
	public static TransformGroup[][] drawPiecesArray; // Array of
														// TransformGroups
														// Updated w/ each move
														// so pieces move
	public static int[][] boardForLogic; // Potentially used for move
											// determination ***
	public static int[][] boardForLogicWhite;
	public static int[][] boardForLogicBlack;
	public static TransformGroup theScene; // Contains transform to center + all
											// objects
	public static TransformGroup theScene1; // Contains rotation Interpolator
	public static BranchGroup bgTheScene; // BranchGroup containing theScene1
	public static BoundingSphere bs; // Bounding area for interpolators
	public static Transform3D moveViewer; // viewer looks down on origin
	public static Transform3D rotBack; // Backwards tilt to see 3D effect
	public static Transform3D changePlayerView; // net change based on who's
												// move it is
	public static Transform3D tfTheScene; // Positions theScene
	public static SimpleUniverse simpUniv; // Location where everything is added
	public static Alpha viewerAlpha; // Defines speed that board rotates
	public static PositionInterpolator rotPlayer; // Rotates the board
	public static int countWhiteDead, countBlackDead;
	public static int capturedRowIndex, capturedColIndex;
	public static BranchGroup bgExtraBS;
	public static Transform3D zAxis;
	public static Transform3D yAxis;
	public static boolean lastMoveWasCastle;
	public static int[] castlingInterpolators;
	public static boolean flagCastleKingSide, flagCastleQueenSide;
	public static boolean flagPromotionEvent;
	public static ChessPieces[] whitePromotionPieces, blackPromotionPieces;
	public static TransformGroup[] drawPiecesPromotionWhite, drawPiecesPromotionBlack;
	public static boolean flagWhitePromoted, flagBlackPromoted;
	public static int countWhitePromoted, countBlackPromoted;
	public static BranchGroup[] bgPromotionWhite, bgPromotionBlack;
	public static boolean flagFirstMovePromoted;

	// En passante stuff
	public static boolean inPassEnabled;
	public static int inPassColumn;

	// Used to easily allow for import of wavefront objects
	public static float reSize = 1.0f; // Keep for now, may help with wavefront
										// ***
	public static boolean lastMoveCapture;
	
	public static TransformGroup[][] tgBoardSquares;
	public static Link[][] squareLinks;

	public Chess3DGameClient() {

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		myCanvas = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
		simpUniv = new SimpleUniverse(myCanvas);

		View v = simpUniv.getViewer().getView();
		v.setBackClipDistance(300.0f); // Used so that no pieces disappear

		moveViewer = new Transform3D();
		moveViewer.setTranslation(new Vector3f(0.0f * reSize, 0.0f * reSize, 17.0f * reSize));
		rotBack = new Transform3D();
		rotBack.rotX(Math.PI / 5);

		moveViewer.mul(rotBack, moveViewer);

		simpUniv.getViewingPlatform().getViewPlatformTransform().setTransform(moveViewer);
		// Positions viewer in the scene
		createSceneGraph(simpUniv);
		addLights(simpUniv);

		OrbitBehavior ob = new OrbitBehavior(myCanvas);
		ob.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE));
		// simpUniv.getViewingPlatform().setViewPlatformBehavior(ob);

		setTitle("Chess Board 3D");
		setSize(1400, 1000);
		this.getContentPane().add("Center", myCanvas);
		setVisible(true);

	}

	public static void main(String[] a) {

		@SuppressWarnings("unused")
		Chess3DGameClient f = new Chess3DGameClient();

	}

	public static void createSceneGraph(SimpleUniverse su) {

		// Create the bs cuz you need it everywhere
		bs = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
		// Create a transform group that can all be moved

		tfTheScene = new Transform3D();
		tfTheScene.setTranslation(new Vector3f(-3.5f, -3.5f, 0.0f)); // Moves
																		// objects
																		// to
																		// origin
																		// for
																		// Proper
																		// rotation

		theScene = new TransformGroup(tfTheScene);
		theScene.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		// Create the board as Link objects that have user data
		createTheSquares();
		initializeThePieces();
		createThePawns();
		createTheRooks();
		createTheKings();
		createTheKnights();
		createTheBishops();
		createTheQueens();

		flagWhitePromoted = false;
		flagBlackPromoted = false;
		countWhitePromoted = 0;
		countBlackPromoted = 0;
		flagFirstMovePromoted = false;

		countWhiteDead = 0;
		countBlackDead = 0;

		lastMoveCapture = false;
		capturedRowIndex = 10;
		capturedColIndex = 10;

		boardForLogic = new int[8][8]; // ***** Not sure how I"ll use this yet
		boardForLogicWhite = new int[8][8];
		boardForLogicBlack = new int[8][8];

		theScene1 = new TransformGroup();
		theScene1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		/*Scanner scan = new Scanner(System.in);
		
		System.out.println("You want animation or nah?");
		
		String animationAnswer = scan.next();
		
		int anim;
		if(animationAnswer.equalsIgnoreCase("yes")) {
			anim = 1600;
		}
		else
			anim = 0;*/
		
		
		viewerAlpha = new Alpha(1, Alpha.INCREASING_ENABLE, 0, 500, 1600, 0, 0, 0, 0, 0);
		viewerAlpha.setStartTime(Long.MAX_VALUE);

		zAxis = new Transform3D();
		zAxis.rotX(Math.PI / 2);
		yAxis = new Transform3D();

		RotationInterpolator rotPlayer = new RotationInterpolator(viewerAlpha, theScene1, zAxis, 0.0f, (float) Math.PI);
		rotPlayer.setSchedulingBounds(bs);

		theScene1.addChild(rotPlayer);

		theScene1.addChild(theScene);

		createTheExtra(su);

		bgTheScene = new BranchGroup();
		bgTheScene.addChild(theScene1);

		allowClick();

		// Add the scene to the simple universe

		bgTheScene.compile();
		
		su.addBranchGraph(bgTheScene);
	}

	/*
	 * *************************************************************************
	 * *************************************************************************
	 * *********** Creation of board
	 * *************************************************************************
	 * *************************************************************************
	 */
	public static void initializeThePieces() {

		// Used to store data about all pieces at play

		drawPiecesArray = new TransformGroup[8][8];
		piecesArray = new ChessPieces[8][8];

		for (int a = 0; a < 8; a++) {

			/*
			 * *****************************************************************
			 * ******* Create the pawn objects to be kept in piecesArray
			 * *****************************************************************
			 * ********
			 */

			// Black Pawns
			// Create an array for all pieces, right now only pawns have been
			// added
			piecesArray[1][a] = new Pawn(1, a, false);

			// Call the update transform function to position the pawns
			// correctly based on their position

			piecesArray[1][a].setTransform();

			/*
			 * *********************************************************** Array
			 * that holds the transform groups that are to be drawn
			 * **********************************************************
			 */
		}
	}

	public static void createTheSquares() {
		// Create Colors for the light and dark sqauares of the board

		Color3f white = new Color3f(0.6f, 0.6f, 0.6f);
		Color3f green = new Color3f(0.0f, 0.5f, 0.0f);

		float shininess = 0.0f;

		Appearance whiteBox = new Appearance();
		whiteBox.setMaterial(new Material(green, white, white, white, shininess));
		Appearance greenBox = new Appearance();
		greenBox.setMaterial(new Material(green, green, green, green, shininess));

		Box whiteSquare = new Box(0.5f * reSize, 0.5f * reSize, 0.05f, whiteBox);
		Box greenSquare = new Box(0.5f * reSize, 0.5f * reSize, 0.05f, greenBox);

		SharedGroup sgWhiteSquare = new SharedGroup();
		sgWhiteSquare.addChild(whiteSquare);

		SharedGroup sgGreenSquare = new SharedGroup();
		sgGreenSquare.addChild(greenSquare);

		int rows = 8;
		int columns = 8;
		float rowStep = 1.0f * reSize;
		float columnStep = 1.0f * reSize;
		boolean switchColor = true;

		tgBoardSquares = new TransformGroup[rows][columns];
		Transform3D[][] tfBoardSquares = new Transform3D[rows][columns];
		squareLinks = new Link[8][8];
		String boardPosition;

		for (int a = 0; a < rows; a++) {
			for (int b = 0; b < columns; b++) {

				tfBoardSquares[a][b] = new Transform3D();
				tfBoardSquares[a][b].setTranslation(new Vector3f(b * columnStep, reSize * 7.0f - a * rowStep, -0.01f));
				tgBoardSquares[a][b] = new TransformGroup(tfBoardSquares[a][b]);

				boardPosition = String.valueOf(a) + String.valueOf(b);
				tgBoardSquares[a][b].setUserData(boardPosition);

				if (switchColor) {

					squareLinks[a][b] = new Link(sgWhiteSquare);
					squareLinks[a][b].setUserData(boardPosition);
					tgBoardSquares[a][b].addChild(squareLinks[a][b]);
				} else {

					squareLinks[a][b] = new Link(sgGreenSquare);
					squareLinks[a][b].setUserData(boardPosition);
					tgBoardSquares[a][b].addChild(squareLinks[a][b]);
				}

				theScene.addChild(tgBoardSquares[a][b]);

				switchColor = !switchColor;
			}
			switchColor = !switchColor;
		}
	}

	public static void createTheExtra(SimpleUniverse simpUniv) {

		bgExtraBS = new BranchGroup();

		Appearance tableApp = new Appearance();
		Color3f tableColor = new Color3f(0.2f, 0.4f, 0.78f);

		Material tableMat = new Material(tableColor, tableColor, tableColor, tableColor, 120.0f);
		tableApp.setMaterial(tableMat);

		Box table = new Box(6.0f, 6.0f, 1.0f, tableApp);

		Transform3D tfTable = new Transform3D();
		tfTable.setTranslation(new Vector3f(0.0f, 0.0f, -1.0f));
		TransformGroup tgTable = new TransformGroup(tfTable);
		tgTable.addChild(table);
		tgTable.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgTable.setPickable(false);

		TransformGroup tgmTable = new TransformGroup();
		tgmTable.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		tgmTable.addChild(tgTable);

		RotationInterpolator rotTable = new RotationInterpolator(viewerAlpha, tgmTable, zAxis, 0.0f, (float) Math.PI);
		rotTable.setSchedulingBounds(bs);

		tgmTable.addChild(rotTable);

		bgExtraBS.addChild(tgmTable);

		simpUniv.addBranchGraph(bgExtraBS);

	}

	public static void createThePawns() {

		for (int a = 0; a < 8; a++) {

			// Black Pawns

			piecesArray[1][a] = new Pawn(1, a, false);

			// Create an array for drawing pieces
			drawPiecesArray[1][a] = new TransformGroup();
			// This step makes the pieces not pickable before adding to
			// drawPieces
			piecesArray[1][a].getTGM().setPickable(false);
			// Add the children to the scene graph. The draw Pieces array is in
			// the scenegraph
			drawPiecesArray[1][a].addChild(piecesArray[1][a].getTGM());
			drawPiecesArray[1][a].setTransform(piecesArray[1][a].getTransform());
			drawPiecesArray[1][a].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			theScene.addChild(drawPiecesArray[1][a]);

			// White Pawns

			// Next add the white pieces to the piece array
			piecesArray[6][a] = new Pawn(6, a, true);

			// Create an array for drawing all the pieces based on their correct
			// positions
			drawPiecesArray[6][a] = new TransformGroup();
			// Disallow pieces from being selected
			piecesArray[6][a].getTGM().setPickable(false);
			// Add the white pieces to the drawPiecesArray which is in the scene
			drawPiecesArray[6][a].addChild(piecesArray[6][a].getTGM());
			drawPiecesArray[6][a].setTransform(piecesArray[6][a].getTransform());
			drawPiecesArray[6][a].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[6][a]);

		}
	}

	public static void createTheKings() {

		// Black King
		piecesArray[0][4] = new King(0, 4, false);

		drawPiecesArray[0][4] = new TransformGroup();
		piecesArray[0][4].getTGM().setPickable(false);
		drawPiecesArray[0][4].addChild(piecesArray[0][4].getTGM());
		drawPiecesArray[0][4].setTransform(piecesArray[0][4].getTransform());
		drawPiecesArray[0][4].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		theScene.addChild(drawPiecesArray[0][4]);

		// White king
		piecesArray[7][4] = new King(7, 4, true);

		drawPiecesArray[7][4] = new TransformGroup();
		piecesArray[7][4].getTGM().setPickable(false);
		drawPiecesArray[7][4].addChild(piecesArray[7][4].getTGM());
		drawPiecesArray[7][4].setTransform(piecesArray[7][4].getTransform());
		drawPiecesArray[7][4].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

		theScene.addChild(drawPiecesArray[7][4]);

	}

	public static void createTheRooks() {

		for (int z = 0; z <= 8; z += 7) {
			piecesArray[0][z] = new Rook(0, z, false);
			// piecesArray[0][z].setTransform();

			drawPiecesArray[0][z] = new TransformGroup();
			piecesArray[0][z].getTGM().setPickable(false);
			drawPiecesArray[0][z].addChild(piecesArray[0][z].getTGM());
			drawPiecesArray[0][z].setTransform(piecesArray[0][z].getTransform());
			drawPiecesArray[0][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[0][z]);

		}
		for (int z = 0; z <= 8; z += 7) {
			piecesArray[7][z] = new Rook(7, z, true);
			// piecesArray[7][z].setTransform();

			drawPiecesArray[7][z] = new TransformGroup();
			piecesArray[7][z].getTGM().setPickable(false);
			drawPiecesArray[7][z].addChild(piecesArray[7][z].getTGM());
			drawPiecesArray[7][z].setTransform(piecesArray[7][z].getTransform());
			drawPiecesArray[7][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[7][z]);

		}

	}

	public static void createTheKnights() {

		for (int z = 1; z <= 6; z += 5) {
			piecesArray[0][z] = new Knight(0, z, false);

			drawPiecesArray[0][z] = new TransformGroup();
			piecesArray[0][z].getTGM().setPickable(false);
			drawPiecesArray[0][z].addChild(piecesArray[0][z].getTGM());
			drawPiecesArray[0][z].setTransform(piecesArray[0][z].getTransform());
			drawPiecesArray[0][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[0][z]);

		}
		for (int z = 1; z <= 6; z += 5) {
			piecesArray[7][z] = new Knight(7, z, true);

			drawPiecesArray[7][z] = new TransformGroup();
			piecesArray[7][z].getTGM().setPickable(false);
			drawPiecesArray[7][z].addChild(piecesArray[7][z].getTGM());
			drawPiecesArray[7][z].setTransform(piecesArray[7][z].getTransform());
			drawPiecesArray[7][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[7][z]);
		}
	}

	public static void createTheBishops() {

		for (int z = 2; z <= 5; z += 3) {
			piecesArray[0][z] = new Bishop(0, z, false);

			drawPiecesArray[0][z] = new TransformGroup();
			piecesArray[0][z].getTGM().setPickable(false);
			// piecesArray[0][z].setTransform();
			drawPiecesArray[0][z].addChild(piecesArray[0][z].getTGM());
			drawPiecesArray[0][z].setTransform(piecesArray[0][z].getTransform());
			drawPiecesArray[0][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[0][z]);

		}
		for (int z = 2; z <= 5; z += 3) {
			piecesArray[7][z] = new Bishop(7, z, true);

			drawPiecesArray[7][z] = new TransformGroup();
			piecesArray[7][z].getTGM().setPickable(false);
			// piecesArray[7][z].setTransform();
			drawPiecesArray[7][z].addChild(piecesArray[7][z].getTGM());
			drawPiecesArray[7][z].setTransform(piecesArray[7][z].getTransform());
			drawPiecesArray[7][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[7][z]);
		}
	}

	public static void createTheQueens() {

		for (int z = 3; z <= 3; z++) {
			piecesArray[0][z] = new Queen(0, z, false);

			drawPiecesArray[0][z] = new TransformGroup();
			piecesArray[0][z].getTGM().setPickable(false);
			// piecesArray[0][z].setTransform();
			drawPiecesArray[0][z].addChild(piecesArray[0][z].getTGM());
			drawPiecesArray[0][z].setTransform(piecesArray[0][z].getTransform());
			drawPiecesArray[0][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[0][z]);

		}
		for (int z = 3; z <= 3; z++) {
			piecesArray[7][z] = new Queen(7, z, true);

			drawPiecesArray[7][z] = new TransformGroup();
			piecesArray[7][z].getTGM().setPickable(false);
			// piecesArray[7][z].setTransform();
			drawPiecesArray[7][z].addChild(piecesArray[7][z].getTGM());
			drawPiecesArray[7][z].setTransform(piecesArray[7][z].getTransform());
			drawPiecesArray[7][z].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			theScene.addChild(drawPiecesArray[7][z]);
		}
	}

	/*
	 * ********************************************************************
	 * ********************************************************************
	 * Moving Pieces
	 * ********************************************************************
	 * ********************************************************************
	 */
	public static void setMove(int rowIndex, int colIndex, int rowInput, int colInput, int[] pIIndex,
			boolean whiteToMove) {

		if (!flagFirstMovePromoted) {

			if (piecesArray[rowIndex][colIndex].toString() == "King"
					&& piecesArray[rowIndex][colIndex].getColumn() - colInput == -2) {

				Chess3DGameClient.setMove(rowIndex, 7, rowIndex, 5,
						piecesArray[rowIndex][7].selectPositionInterpolator(rowIndex, 5), whiteToMove);
				flagCastleKingSide = true;
			}
			if (piecesArray[rowIndex][colIndex].toString() == "King"
					&& piecesArray[rowIndex][colIndex].getColumn() - colInput == 2) {

				Chess3DGameClient.setMove(rowIndex, 0, rowIndex, 3,
						piecesArray[rowIndex][0].selectPositionInterpolator(rowIndex, 3), whiteToMove);
				flagCastleQueenSide = true;
			}
		}

		if (!flagFirstMovePromoted)
			piecesArray[rowIndex][colIndex].setAlphaStart(pIIndex);
		else {
			if (whiteToMove)
				whitePromotionPieces[rowIndex].setAlphaStart(pIIndex);
			else
				blackPromotionPieces[rowIndex].setAlphaStart(pIIndex);
		}

		boolean flagCaptureEvent = false;
		boolean flagCapturePromoted = false;
		capturedRowIndex = 0;
		capturedColIndex = 0;

		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 8; b++) {
				try {
					if (piecesArray[a][b].getRow() == rowInput && piecesArray[a][b].getColumn() == colInput
							&& !piecesArray[a][b].getIsCaptured()) {
						flagCaptureEvent = true;
						capturedRowIndex = a;
						capturedColIndex = b;
						countBlackDead++;
					}
				} catch (NullPointerException e) {
				}
			}
		}
		for (int a = 6; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				try {
					if (piecesArray[a][b].getRow() == rowInput && piecesArray[a][b].getColumn() == colInput
							&& !piecesArray[a][b].getIsCaptured()) {
						flagCaptureEvent = true;
						capturedRowIndex = a;
						capturedColIndex = b;
						countWhiteDead++;
					}
				} catch (NullPointerException e) {
				}
			}
		}
		// Add scans for promoted pieces as far as them being captureable
		// *****************************
		if (flagWhitePromoted && !whiteToMove && !flagCaptureEvent) {
			// if white has promoted pieces and it's black's turn and there
			// hasn't been a capture flag check the black pieces on square
			for (int a = 0; a < whitePromotionPieces.length; a++)
				if (whitePromotionPieces[a].getRow() == rowInput && whitePromotionPieces[a].getColumn() == colInput
						&& !whitePromotionPieces[a].getIsCaptured()) {
					flagCaptureEvent = true;
					flagCapturePromoted = true;
					capturedRowIndex = a;
					countWhiteDead++;
				}
		}
		if (flagBlackPromoted && whiteToMove && !flagCaptureEvent) {

			for (int a = 0; a < blackPromotionPieces.length; a++)
				if (blackPromotionPieces[a].getRow() == rowInput && blackPromotionPieces[a].getColumn() == colInput
						&& !blackPromotionPieces[a].getIsCaptured()) {
					flagCaptureEvent = true;
					flagCapturePromoted = true;
					capturedRowIndex = a;
					countBlackDead++;
				}
		}

		// Add section for flagFirstMovePromoted to test if any piece is
		// supposed to be captured by it

		if (flagCaptureEvent) {
			lastMoveCapture = true;
			PickingSquare.setLastMoveCapture(true);

			if (!flagCapturePromoted) {
				piecesArray[capturedRowIndex][capturedColIndex].getCapturedEvent();
				// Send it the number of its dead

				piecesArray[capturedRowIndex][capturedColIndex].finalizeCapture(countWhiteDead, countBlackDead);
				drawPiecesArray[capturedRowIndex][capturedColIndex]
						.setTransform(piecesArray[capturedRowIndex][capturedColIndex].getCapturedTransform());
			}
			if (flagCapturePromoted) {

				if (!whiteToMove) {
					whitePromotionPieces[capturedRowIndex].getCapturedEvent();
					whitePromotionPieces[capturedRowIndex].finalizeCapture(countWhiteDead, countBlackDead);
					drawPiecesPromotionWhite[capturedRowIndex]
							.setTransform(whitePromotionPieces[capturedRowIndex].getCapturedTransform());
				}
				if (whiteToMove) {
					blackPromotionPieces[capturedRowIndex].getCapturedEvent();
					blackPromotionPieces[capturedRowIndex].finalizeCapture(countWhiteDead, countBlackDead);
					drawPiecesPromotionBlack[capturedRowIndex]
							.setTransform(blackPromotionPieces[capturedRowIndex].getCapturedTransform());
				}

			}

		}
		if (!flagCaptureEvent) {
			lastMoveCapture = false;
		}

		// Set In passable

		if (!flagFirstMovePromoted) {
			if (piecesArray[rowIndex][colIndex].toString() == "Pawn" && Math.abs(rowInput - rowIndex) == 2) {
				inPassColumn = piecesArray[rowIndex][colIndex].getColumn();

				// Send this information to picking square
				PickingSquare.collectInPassInfo(inPassColumn);
			}
		}
		// Add en passante event

		if (!flagFirstMovePromoted) {
			if (piecesArray[rowIndex][colIndex].toString() == "Pawn"
					&& piecesArray[rowIndex][colIndex].getColumn() - colInput != 0
					&& boardForLogic[rowInput][colInput] == 0) {
				// (if you are a pawn and are moving diagonally to a square with
				// a
				// vacancy this is enpass)
				int pawnCaptureRow = 0;
				int pawnCaptureCol = 0;

				lastMoveCapture = true;
				PickingSquare.setLastMoveCapture(true);

				// find which indicies give you the pawn that should get killed
				for (int a = 0; a < 2; a++) {
					for (int b = 0; b < 8; b++) {
						if (piecesArray[a][b].getColumn() == colInput
								&& piecesArray[a][b].getRow() == piecesArray[rowIndex][colIndex].getRow()) {
							pawnCaptureRow = a;
							pawnCaptureCol = b;
						}
					}
				}
				for (int a = 6; a < 8; a++) {
					for (int b = 0; b < 8; b++) {
						if (piecesArray[a][b].getColumn() == colInput
								&& piecesArray[a][b].getRow() == piecesArray[rowIndex][colIndex].getRow()) {
							pawnCaptureRow = a;
							pawnCaptureCol = b;
						}
					}
				}
				if (piecesArray[pawnCaptureRow][pawnCaptureCol].isThePieceWhite()) {
					countWhiteDead++;
				} else
					countBlackDead++;

				piecesArray[pawnCaptureRow][pawnCaptureCol].getCapturedEvent();
				piecesArray[pawnCaptureRow][pawnCaptureCol].finalizeCapture(countWhiteDead, countBlackDead);
				drawPiecesArray[pawnCaptureRow][pawnCaptureCol]
						.setTransform(piecesArray[pawnCaptureRow][pawnCaptureCol].getCapturedTransform());
				
				try {
					String wav_file = "C:/Users/jakub/Documents/Java/ChessBoard3DActive/check.wav";
					InputStream in = new FileInputStream(wav_file);

					AudioStream audio = new AudioStream(in);
					AudioPlayer.player.start(audio);
				} catch (Exception e) {

				}

			}
		}
		// PROMOTION EVENT SCANNING

		// First is and , then is or
		if (!flagFirstMovePromoted) {
			if (piecesArray[rowIndex][colIndex].toString() == "Pawn" && (rowInput == 0 || rowInput == 7)) {

				// First set the piece as captured

				PickingSquare.setLastMoveCapture(true);

				if (piecesArray[rowIndex][colIndex].isThePieceWhite())
					countWhiteDead++;
				else
					countBlackDead++;

				flagPromotionEvent = true;

				promotionMethod(rowIndex, colIndex, rowInput, colInput, pIIndex);

				piecesArray[rowIndex][colIndex].resetAlpha(pIIndex);
				piecesArray[rowIndex][colIndex].getCapturedEvent();
				piecesArray[rowIndex][colIndex].finalizeCapture(countWhiteDead, countBlackDead);
				drawPiecesArray[rowIndex][colIndex]
						.setTransform(piecesArray[rowIndex][colIndex].getCapturedTransform());

			}
		}

		if (!flagFirstMovePromoted) {
			piecesArray[rowIndex][colIndex].setRow(rowInput);
			piecesArray[rowIndex][colIndex].setColumn(colInput);
		}
		if (flagFirstMovePromoted) {
			if (whiteToMove) {
				whitePromotionPieces[rowIndex].setRow(rowInput);
				whitePromotionPieces[rowIndex].setColumn(colInput);
			}
			if (!whiteToMove) {
				blackPromotionPieces[rowIndex].setRow(rowInput);
				blackPromotionPieces[rowIndex].setColumn(colInput);
			}
		}

		if (!flagFirstMovePromoted) {
			if (piecesArray[rowIndex][colIndex].toString() == "Rook" && colIndex == 7)
				piecesArray[rowIndex][4].setMoveYet(3);
			if (piecesArray[rowIndex][colIndex].toString() == "Rook" && colIndex == 0)
				piecesArray[rowIndex][4].setMoveYet(1);
			if (piecesArray[rowIndex][colIndex].toString() == "King")
				piecesArray[rowIndex][4].setMoveYet(2);
		}

		if (!flagCastleKingSide && !flagCastleQueenSide)
			changePlayerView();

	}

	public static void finalizeMove(int rowIndex, int colIndex, int[] piIndex, boolean whiteToMove) {

		if (flagCastleKingSide) {

			piecesArray[rowIndex][7].resetAlpha(piecesArray[rowIndex][7].selectPositionInterpolator(rowIndex, 3));
			piecesArray[rowIndex][7].updateTransform();
			drawPiecesArray[rowIndex][7].setTransform(piecesArray[rowIndex][7].getTransform());
		}
		if (flagCastleQueenSide) {
			piecesArray[rowIndex][0].resetAlpha(piecesArray[rowIndex][0].selectPositionInterpolator(rowIndex, 6));
			piecesArray[rowIndex][0].updateTransform();
			drawPiecesArray[rowIndex][0].setTransform(piecesArray[rowIndex][0].getTransform());
		}

		flagCastleKingSide = false;
		flagCastleQueenSide = false;

		// If it was a promotion which will be flagged in set move then call the
		// promotion method
		// The piece from piecesArray will have alpha reset but will then be
		// killed

		// System.out.println("The flagPromotionEvent has the value: " +
		// flagPromotionEvent);

		if (!flagFirstMovePromoted && !flagPromotionEvent) {

			piecesArray[rowIndex][colIndex].resetAlpha(piIndex);
			piecesArray[rowIndex][colIndex].updateTransform();
			drawPiecesArray[rowIndex][colIndex].setTransform(piecesArray[rowIndex][colIndex].getTransform());
		}

		if (flagFirstMovePromoted) {
			if (whiteToMove) {
				whitePromotionPieces[rowIndex].resetAlpha(piIndex);
				whitePromotionPieces[rowIndex].updateTransform();
				drawPiecesPromotionWhite[rowIndex].setTransform(whitePromotionPieces[rowIndex].getTransform());
			}
			if (!whiteToMove) {
				blackPromotionPieces[rowIndex].resetAlpha(piIndex);
				blackPromotionPieces[rowIndex].updateTransform();
				drawPiecesPromotionBlack[rowIndex].setTransform(blackPromotionPieces[rowIndex].getTransform());
			}
		}

		flagFirstMovePromoted = false;
		flagPromotionEvent = false;

	}

	public static void changePlayerView() {
		viewerAlpha.setStartTime(System.currentTimeMillis() - viewerAlpha.getTriggerTime());
	}

	public static void setViewerTransform(boolean isWhite) {

		Transform3D rotAround = new Transform3D();
		if (!isWhite)
			rotAround.rotZ(Math.PI);

		rotAround.mul(rotAround, tfTheScene);

		theScene.setTransform(rotAround);
	}

	public static void resetAlpha() {
		viewerAlpha.setStartTime(Long.MAX_VALUE);
	}

	public static int[] selectPositionInterpolator(int rowIndex, int colIndex, int rowSelect, int colSelect,
			boolean whiteToMove) {

		int[] returnTemp;
		returnTemp = new int[3];

		if (!flagFirstMovePromoted)
			returnTemp = piecesArray[rowIndex][colIndex].selectPositionInterpolator(rowSelect, colSelect);
		else {
			if (whiteToMove) {
				returnTemp = whitePromotionPieces[rowIndex].selectPositionInterpolator(rowSelect, colSelect);
			}
			if (!whiteToMove) {
				returnTemp = blackPromotionPieces[rowIndex].selectPositionInterpolator(rowSelect, colSelect);
			}
		}
		return returnTemp;
	}

	public static void promotionMethod(int rowIndex, int colIndex, int rowInput, int colInput, int[] pIIndex) {

		Object[] options = { "Queen", "Rook", "Bishop", "Knight" };

		int selection = JOptionPane.showOptionDialog(null, "Enter Promotion Option or default to Queen", "Promotion",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		// Selection will be 0 for queen, 1 for rook, 2 for bishop , 3 for
		// knight
		// First do it for white

		// YOU THOUGHT SOUND EFFECT *******************************************************************************************
		
		
		
		
		// Add more promotion pieces for white
		if (piecesArray[rowIndex][colIndex].isThePieceWhite() && flagWhitePromoted) {

			// we now have to extend array of the whitePromotionPieces and
			// drawPiecesPromotionWhite array
			int length = whitePromotionPieces.length;

			// Copy the
			ChessPieces[] temp = new ChessPieces[length];
			TransformGroup[] tgTemp = new TransformGroup[length];

			for (int a = 0; a < length; a++) {
				temp[a] = whitePromotionPieces[a];
				tgTemp[a] = drawPiecesPromotionWhite[a];
			}

			whitePromotionPieces = new ChessPieces[length + 1];
			drawPiecesPromotionWhite = new TransformGroup[length + 1];
			bgPromotionWhite = new BranchGroup[length + 1];

			for (int a = 0; a < length; a++) {
				whitePromotionPieces[a] = temp[a];
				drawPiecesPromotionWhite[a] = tgTemp[a];
			}

			if (selection == 0 || selection == -1)
				whitePromotionPieces[length] = new Queen(0, colInput, true);
			if (selection == 1)
				whitePromotionPieces[length] = new Rook(0, colInput, true);
			if (selection == 2)
				whitePromotionPieces[length] = new Bishop(0, colInput, true);
			if (selection == 3)
				whitePromotionPieces[length] = new Knight(0, colInput, true);

			whitePromotionPieces[length].getTGM().setPickable(false);
			drawPiecesPromotionWhite[length] = new TransformGroup();
			drawPiecesPromotionWhite[length].addChild(whitePromotionPieces[length].getTGM());
			drawPiecesPromotionWhite[length].setTransform(whitePromotionPieces[length].getTransform());
			drawPiecesPromotionWhite[length].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			bgPromotionWhite[length] = new BranchGroup();
			bgPromotionWhite[length].addChild(drawPiecesPromotionWhite[length]);

			theScene.addChild(bgPromotionWhite[length]);
			countWhitePromoted++;
		}
		// Add more promotion pieces for black
		if (!piecesArray[rowIndex][colIndex].isThePieceWhite() && flagBlackPromoted) {

			// we now have to extend array of the whitePromotionPieces and
			// drawPiecesPromotionWhite array
			int length = blackPromotionPieces.length;

			// Copy the
			ChessPieces[] temp = new ChessPieces[length];
			TransformGroup[] tgTemp = new TransformGroup[length];

			for (int a = 0; a < length; a++) {
				temp[a] = blackPromotionPieces[a];
				tgTemp[a] = drawPiecesPromotionBlack[a];
			}

			blackPromotionPieces = new ChessPieces[length + 1];
			drawPiecesPromotionBlack = new TransformGroup[length + 1];
			bgPromotionBlack = new BranchGroup[length + 1];

			for (int a = 0; a < length; a++) {
				blackPromotionPieces[a] = temp[a];
				drawPiecesPromotionBlack[a] = tgTemp[a];
			}

			if (selection == 0)
				blackPromotionPieces[length] = new Queen(7, colInput, false);
			if (selection == 1)
				blackPromotionPieces[length] = new Rook(7, colInput, false);
			if (selection == 2)
				blackPromotionPieces[length] = new Bishop(7, colInput, false);
			if (selection == 3)
				blackPromotionPieces[length] = new Knight(7, colInput, false);

			blackPromotionPieces[length].getTGM().setPickable(false);
			drawPiecesPromotionBlack[length] = new TransformGroup();
			drawPiecesPromotionBlack[length].addChild(blackPromotionPieces[length].getTGM());
			drawPiecesPromotionBlack[length].setTransform(blackPromotionPieces[length].getTransform());
			drawPiecesPromotionBlack[length].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			bgPromotionBlack[length] = new BranchGroup();
			bgPromotionBlack[length].addChild(drawPiecesPromotionBlack[length]);

			theScene.addChild(bgPromotionBlack[length]);
			countBlackPromoted++;
		}

		// Initialize stuff for white
		if (piecesArray[rowIndex][rowInput].isThePieceWhite() && !flagWhitePromoted) {

			// white piece tells you that the row is gonna be 0
			// colInput tells you which column the piece is on
			// selection tells you which piece to make

			whitePromotionPieces = new ChessPieces[1];
			flagWhitePromoted = true;

			if (selection == 0) {
				whitePromotionPieces[0] = new Queen(0, colInput, true);
			}
			if (selection == 1) {
				whitePromotionPieces[0] = new Rook(0, colInput, true);
			}
			if (selection == 2) {
				whitePromotionPieces[0] = new Bishop(0, colInput, true);
			}
			if (selection == 3) {
				whitePromotionPieces[0] = new Knight(0, colInput, true);
			}

			drawPiecesPromotionWhite = new TransformGroup[1];
			drawPiecesPromotionWhite[0] = new TransformGroup();

			// create the transformGroup to be added to the scene
			whitePromotionPieces[0].getTGM().setPickable(false);
			drawPiecesPromotionWhite[0].addChild(whitePromotionPieces[0].getTGM());
			drawPiecesPromotionWhite[0].setTransform(whitePromotionPieces[0].getTransform());
			drawPiecesPromotionWhite[0].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			bgPromotionWhite = new BranchGroup[8];
			bgPromotionWhite[0] = new BranchGroup();
			bgPromotionWhite[0].addChild(drawPiecesPromotionWhite[0]);

			theScene.addChild(bgPromotionWhite[0]);
			countWhitePromoted++;

		}
		// Initialize stuff for black
		if (!piecesArray[rowIndex][rowInput].isThePieceWhite() && !flagBlackPromoted) {
			// white piece tells you that the row is gonna be 0
			// colInput tells you which column the piece is on
			// selection tells you which piece to make

			blackPromotionPieces = new ChessPieces[1];
			flagBlackPromoted = true;

			if (selection == 0) {
				blackPromotionPieces[0] = new Queen(7, colInput, false);
			}
			if (selection == 1) {
				blackPromotionPieces[0] = new Rook(7, colInput, false);
			}
			if (selection == 2) {
				blackPromotionPieces[0] = new Bishop(7, colInput, false);
			}
			if (selection == 3) {
				blackPromotionPieces[0] = new Knight(7, colInput, false);
			}

			drawPiecesPromotionBlack = new TransformGroup[1];
			drawPiecesPromotionBlack[0] = new TransformGroup();

			// create the transformGroup to be added to the scene

			blackPromotionPieces[0].getTGM().setPickable(false);
			drawPiecesPromotionBlack[0].addChild(blackPromotionPieces[0].getTGM());
			drawPiecesPromotionBlack[0].setTransform(blackPromotionPieces[0].getTransform());
			drawPiecesPromotionBlack[0].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

			bgPromotionBlack = new BranchGroup[8];
			bgPromotionBlack[0] = new BranchGroup();
			bgPromotionBlack[0].addChild(drawPiecesPromotionBlack[0]);

			theScene.addChild(bgPromotionBlack[0]);
			countBlackPromoted++;
		}

	}

	/*
	 * *************************************************************************
	 * *************************************************************************
	 * **** Methods for logic determination
	 * *************************************************************************
	 * *************************************************************************
	 */

	public static boolean checkPieceSquare(

			int rowTest, int colTest, int rowInput, int colInput, boolean isWhite) {

		flagFirstMovePromoted = false;

		int returnInt;
		int saveRow, saveCol;
		try {

			// See if the rowInput matches the row and column of an uncaptured
			// piece of right color

			if (piecesArray[rowTest][colTest].getRow() == rowInput
					&& piecesArray[rowTest][colTest].getColumn() == colInput
					&& piecesArray[rowTest][colTest].isWhite == isWhite
					&& !piecesArray[rowTest][colTest].getIsCaptured()) {
				saveRow = rowTest;
				saveCol = colTest;
				flagFirstMovePromoted = false;
				// System.out.println("CURRENT CHECKPOINT: SAVEROW =" +
				// saveRow);
				// System.out.println("CURRENT CHECKPOINT: SAVECOL =" +
				// saveCol);

				PickingSquare.setFirstMoveIndicies(saveRow, saveCol);

				returnInt = 1;

				// else there is no piece there, the indicies will not be stored
				// and firstmove still invalid
			} else
				returnInt = 0;

		} catch (NullPointerException exception) {
			returnInt = 0;
		}

		boolean flagReturn;
		if (returnInt == 1)
			flagReturn = true;
		else
			flagReturn = false;

		// if you didn't find a regular piece, check if there is a promoted
		// piece on that square
		// flag that this selected piece is a promoted piece so that the
		// rowIndex is interpreted correctly

		if (isWhite && flagWhitePromoted && !flagReturn) {
			// look for a white promoted piece
			for (int a = 0; a < countWhitePromoted; a++)
				if (!whitePromotionPieces[a].getIsCaptured() && whitePromotionPieces[a].getRow() == rowInput
						&& whitePromotionPieces[a].getColumn() == colInput) {

					flagFirstMovePromoted = true;
					PickingSquare.flagSelectedPiecePromoted();
					PickingSquare.setFirstMoveIndexPromoted(a);
					flagReturn = true;
				}
		}
		if (!isWhite && flagBlackPromoted && !flagReturn) {
			// look for a black promoted piece
			for (int a = 0; a < countBlackPromoted; a++)
				if (!blackPromotionPieces[a].getIsCaptured() && blackPromotionPieces[a].getRow() == rowInput
						&& blackPromotionPieces[a].getColumn() == colInput) {

					flagFirstMovePromoted = true;
					PickingSquare.flagSelectedPiecePromoted();
					PickingSquare.setFirstMoveIndexPromoted(a);
				}
			flagReturn = true;
		}

		return flagReturn;

	}

	public static boolean checkNextMoveLegal(int rowIndex, int colIndex, int rowSelect, int colSelect,
			boolean whiteToMove) {

		boolean flagNextMoveLegal = false;

		if (flagFirstMovePromoted) {
			if (whiteToMove) {
				flagNextMoveLegal = whitePromotionPieces[rowIndex].getMoveLogic(rowSelect, colSelect);

			} else {
				flagNextMoveLegal = blackPromotionPieces[rowIndex].getMoveLogic(rowSelect, colSelect);

			}
		} else
			flagNextMoveLegal = piecesArray[rowIndex][colIndex].getMoveLogic(rowSelect, colSelect);

		if (!flagNextMoveLegal)
			flagFirstMovePromoted = false;

		return flagNextMoveLegal;
	}

	public static void updateBoardForLogic() {

		// *************************************************************************************
		// Does not work as intended, should check for piece on a square not
		// System.out.println("Board with all pieces");

		String nameToNum;
		int tempRow, tempCol;

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				boardForLogic[a][b] = 0;
			}
		}

		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 8; b++) {

				if (!piecesArray[a][b].getIsCaptured()) {
					tempRow = piecesArray[a][b].getRow();
					tempCol = piecesArray[a][b].getColumn();
					nameToNum = piecesArray[a][b].toString();

					switch (nameToNum) {
					case "Pawn":
						boardForLogic[tempRow][tempCol] = 1;
						break;
					case "Rook":
						boardForLogic[tempRow][tempCol] = 2;
						break;
					case "Knight":
						boardForLogic[tempRow][tempCol] = 3;
						break;
					case "Bishop":
						boardForLogic[tempRow][tempCol] = 4;
						break;
					case "Queen":
						boardForLogic[tempRow][tempCol] = 5;
						break;
					case "King":
						boardForLogic[tempRow][tempCol] = 6;
						break;
					default:
						boardForLogic[tempRow][tempCol] = 99;
					}
				}
			}
		}
		for (int a = 7; a >= 6; a--) {
			for (int b = 0; b < 8; b++) {

				if (!piecesArray[a][b].getIsCaptured()) {
					tempRow = piecesArray[a][b].getRow();
					tempCol = piecesArray[a][b].getColumn();
					nameToNum = piecesArray[a][b].toString();

					switch (nameToNum) {
					case "Pawn":
						boardForLogic[tempRow][tempCol] = 1;
						break;
					case "Rook":
						boardForLogic[tempRow][tempCol] = 2;
						break;
					case "Knight":
						boardForLogic[tempRow][tempCol] = 3;
						break;
					case "Bishop":
						boardForLogic[tempRow][tempCol] = 4;
						break;
					case "Queen":
						boardForLogic[tempRow][tempCol] = 5;
						break;
					case "King":
						boardForLogic[tempRow][tempCol] = 6;
						break;
					default:
						boardForLogic[tempRow][tempCol] = 99;
					}
				}

			}
		}

		// Now also scan for promoted pieces if there was a promotion event
		if (flagWhitePromoted) {
			for (int a = 0; a < countWhitePromoted; a++) {
				if (!whitePromotionPieces[a].getIsCaptured()) {

					tempRow = whitePromotionPieces[a].getRow();
					tempCol = whitePromotionPieces[a].getColumn();
					nameToNum = whitePromotionPieces[a].toString();

					switch (nameToNum) {
					case "Pawn":
						boardForLogic[tempRow][tempCol] = 1;
						break;
					case "Rook":
						boardForLogic[tempRow][tempCol] = 2;
						break;
					case "Knight":
						boardForLogic[tempRow][tempCol] = 3;
						break;
					case "Bishop":
						boardForLogic[tempRow][tempCol] = 4;
						break;
					case "Queen":
						boardForLogic[tempRow][tempCol] = 5;
						break;
					case "King":
						boardForLogic[tempRow][tempCol] = 6;
						break;
					default:
						boardForLogic[tempRow][tempCol] = 99;
					}
				}
			}
		}
		if (flagBlackPromoted) {
			for (int a = 0; a < countBlackPromoted; a++) {
				if (!blackPromotionPieces[a].getIsCaptured()) {

					tempRow = blackPromotionPieces[a].getRow();
					tempCol = blackPromotionPieces[a].getColumn();
					nameToNum = blackPromotionPieces[a].toString();

					switch (nameToNum) {
					case "Pawn":
						boardForLogic[tempRow][tempCol] = 1;
						break;
					case "Rook":
						boardForLogic[tempRow][tempCol] = 2;
						break;
					case "Knight":
						boardForLogic[tempRow][tempCol] = 3;
						break;
					case "Bishop":
						boardForLogic[tempRow][tempCol] = 4;
						break;
					case "Queen":
						boardForLogic[tempRow][tempCol] = 5;
						break;
					case "King":
						boardForLogic[tempRow][tempCol] = 6;
						break;
					default:
						boardForLogic[tempRow][tempCol] = 99;
					}
				}
			}
		}

		/*
		 * System.out.
		 * println("********************* Test if the board updates properly considering the promoted pieces"
		 * );
		 * 
		 * for (int a = 0; a < 8; a++) { for (int b = 0; b < 8; b++) {
		 * System.out.print(boardForLogic[a][b] + " "); } System.out.println();
		 * }
		 */

	}

	public static void updateBoardForLogicEnemy(boolean isWhite) {

		String nameToNum;
		int tempRow, tempCol;

		for (int a = 0; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				if (!isWhite)
					boardForLogicWhite[a][b] = 0;
				else
					boardForLogicBlack[a][b] = 0;
			}
		}

		if (isWhite) {
			for (int a = 0; a < 2; a++) {
				for (int b = 0; b < 8; b++) {

					if (!piecesArray[a][b].getIsCaptured()) {
						tempRow = piecesArray[a][b].getRow();
						tempCol = piecesArray[a][b].getColumn();
						nameToNum = piecesArray[a][b].toString();

						switch (nameToNum) {
						case "Pawn":
							boardForLogicBlack[tempRow][tempCol] = 1;
							break;
						case "Rook":
							boardForLogicBlack[tempRow][tempCol] = 2;
							break;
						case "Knight":
							boardForLogicBlack[tempRow][tempCol] = 3;
							break;
						case "Bishop":
							boardForLogicBlack[tempRow][tempCol] = 4;
							break;
						case "Queen":
							boardForLogicBlack[tempRow][tempCol] = 5;
							break;
						case "King":
							boardForLogicBlack[tempRow][tempCol] = 6;
							break;
						default:
							boardForLogicBlack[tempRow][tempCol] = 0;
						}
					}
				}
			}

			// scan for black pieces, copy from the other one
			if (flagBlackPromoted) {
				for (int a = 0; a < countBlackPromoted; a++) {
					if (!blackPromotionPieces[a].getIsCaptured()) {

						tempRow = blackPromotionPieces[a].getRow();
						tempCol = blackPromotionPieces[a].getColumn();
						nameToNum = blackPromotionPieces[a].toString();

						switch (nameToNum) {
						case "Pawn":
							boardForLogicBlack[tempRow][tempCol] = 1;
							break;
						case "Rook":
							boardForLogicBlack[tempRow][tempCol] = 2;
							break;
						case "Knight":
							boardForLogicBlack[tempRow][tempCol] = 3;
							break;
						case "Bishop":
							boardForLogicBlack[tempRow][tempCol] = 4;
							break;
						case "Queen":
							boardForLogicBlack[tempRow][tempCol] = 5;
							break;
						case "King":
							boardForLogicBlack[tempRow][tempCol] = 6;
							break;
						default:
							boardForLogicBlack[tempRow][tempCol] = 99;
						}
					}
				}
			}

		} else {
			for (int a = 6; a < 8; a++) {
				for (int b = 0; b < 8; b++) {

					if (!piecesArray[a][b].getIsCaptured()) {
						tempRow = piecesArray[a][b].getRow();
						tempCol = piecesArray[a][b].getColumn();
						nameToNum = piecesArray[a][b].toString();

						switch (nameToNum) {
						case "Pawn":
							boardForLogicWhite[tempRow][tempCol] = 1;
							break;
						case "Rook":
							boardForLogicWhite[tempRow][tempCol] = 2;
							break;
						case "Knight":
							boardForLogicWhite[tempRow][tempCol] = 3;
							break;
						case "Bishop":
							boardForLogicWhite[tempRow][tempCol] = 4;
							break;
						case "Queen":
							boardForLogicWhite[tempRow][tempCol] = 5;
							break;
						case "King":
							boardForLogicWhite[tempRow][tempCol] = 6;
							break;
						default:
							boardForLogicWhite[tempRow][tempCol] = 0;
						}
					}
				}
			}
			// Now also scan for promoted pieces if there was a promotion event
			if (flagWhitePromoted) {
				for (int a = 0; a < countWhitePromoted; a++) {
					if (!whitePromotionPieces[a].getIsCaptured()) {

						tempRow = whitePromotionPieces[a].getRow();
						tempCol = whitePromotionPieces[a].getColumn();
						nameToNum = whitePromotionPieces[a].toString();

						switch (nameToNum) {
						case "Pawn":
							boardForLogicWhite[tempRow][tempCol] = 1;
							break;
						case "Rook":
							boardForLogicWhite[tempRow][tempCol] = 2;
							break;
						case "Knight":
							boardForLogicWhite[tempRow][tempCol] = 3;
							break;
						case "Bishop":
							boardForLogicWhite[tempRow][tempCol] = 4;
							break;
						case "Queen":
							boardForLogicWhite[tempRow][tempCol] = 5;
							break;
						case "King":
							boardForLogicWhite[tempRow][tempCol] = 6;
							break;
						default:
							boardForLogicWhite[tempRow][tempCol] = 99;
						}
					}
				}
			}
		}

		/*
		 * System.out.
		 * println("UPDATED BOARD FOR LOGIC FOR ENEMY. NOW SCANS FOR PROMOTED PIECES CORRECTLY"
		 * );
		 * 
		 * if (!isWhite) { for (int a = 0; a < 8; a++) { for (int b = 0; b < 8;
		 * b++) { System.out.print(boardForLogicWhite[a][b] + " "); }
		 * System.out.println(); } }
		 * 
		 * if (isWhite) { for (int a = 0; a < 8; a++) { for (int b = 0; b < 8;
		 * b++) { System.out.print(boardForLogicBlack[a][b] + " "); }
		 * System.out.println(); } }
		 */

	}

	public static void setMoveLogic(int rowInput, int colInput, boolean isWhite, int inPassCol, boolean inPassEnabled) {

		if (flagFirstMovePromoted) {
			if (isWhite) {
				whitePromotionPieces[rowInput].setMoveLogic(boardForLogic, boardForLogicBlack, inPassCol,
						inPassEnabled);
			}
			if (!isWhite) {
				blackPromotionPieces[rowInput].setMoveLogic(boardForLogic, boardForLogicWhite, inPassCol,
						inPassEnabled);
			}
			// flagFirstMovePromoted = false;
		}

		else {
			if (!isWhite)
				piecesArray[rowInput][colInput].setMoveLogic(boardForLogic, boardForLogicWhite, inPassCol,
						inPassEnabled);
			else
				piecesArray[rowInput][colInput].setMoveLogic(boardForLogic, boardForLogicBlack, inPassCol,
						inPassEnabled);
		}
	}

	public static int[][] getMoveLogicBoard(int rowInput, int colInput) {
		return piecesArray[rowInput][colInput].getMoveLogicArray();
	}

	/*
	 * *************************************************************************
	 * *************************************************************************
	 * ***** Adding lights and adding click ability to the scene
	 * *************************************************************************
	 * *************************************************************************
	 */

	public static void allowClick() {
		PickingSquare ps = new PickingSquare(myCanvas, bgTheScene, bs);
		theScene.addChild(ps);

		theScene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		theScene.setCapability(BranchGroup.ALLOW_DETACH);
	}

	public static boolean getMoveLogic(int rowIndex, int colIndex, int a, int b) {
		return piecesArray[rowIndex][colIndex].getMoveLogic(a, b);
	}

	public static int postMoveTest(boolean whiteToMove, int inPassColumn, boolean inPassEnabled) {

		// Continue scanning all pieces until legal move found

		int returnInt;
		boolean flagLegalMove = false;

		// counta is row on board to check
		// countb is col on board to check

		// if whiteToMove check the white pieces if any is not captured
		// if uncaptured white piece check if it scan if it can move anywhere

		if (whiteToMove) {

			for (int a = 6; a < 8; a++) {

				for (int b = 0; b < 8; b++) {

					if (!piecesArray[a][b].getIsCaptured()) {

						piecesArray[a][b].setMoveLogic(boardForLogic, boardForLogicBlack, inPassColumn, inPassEnabled);

						for (int x = 0; x < 8; x++)
							for (int y = 0; y < 8; y++) {

								if (piecesArray[a][b].getMoveLogic(x, y)) {
									flagLegalMove = true;
								}

							}

					}
				}
			}

			// if this is still false then check all promoted pieces

		}

		if (!whiteToMove) {

			for (int a = 0; a < 2; a++) {

				for (int b = 0; b < 8; b++) {

					if (!piecesArray[a][b].getIsCaptured()) {

						piecesArray[a][b].setMoveLogic(boardForLogic, boardForLogicWhite, inPassColumn, inPassEnabled);

						for (int x = 0; x < 8; x++)
							for (int y = 0; y < 8; y++) {

								if (piecesArray[a][b].getMoveLogic(x, y)) {
									flagLegalMove = true;
								}

							}

					}
				}
			}

			// if this is still false then check all promoted pieces

		}

		if (flagLegalMove) {
			returnInt = 1;
		} else
			returnInt = -1;

		return returnInt;
	}

	public static boolean inCheck(boolean whiteToMove) {

		boolean flagReturn = false;
		int[][] tempArray;
		int tempRow;
		long example;
		int tempCol;
		// Ask the king for the influence array
		if (whiteToMove) {

			tempArray = piecesArray[7][4].generateInfluenceArray(boardForLogic, boardForLogicWhite, boardForLogicBlack);
			tempRow = piecesArray[7][4].getRow();
			tempCol = piecesArray[7][4].getColumn();

			if (tempArray[tempRow][tempCol] != 0)
				flagReturn = true;

		}
		if (!whiteToMove) {

			tempArray = piecesArray[0][4].generateInfluenceArray(boardForLogic, boardForLogicBlack, boardForLogicWhite);
			tempRow = piecesArray[0][4].getRow();
			tempCol = piecesArray[0][4].getColumn();

			if (tempArray[tempRow][tempCol] != 0)
				flagReturn = true;

		}

		return flagReturn;
	}

	public static void addLights(SimpleUniverse su) {

		BranchGroup lights = new BranchGroup();

		// Next let's add a point light so the scene makes some sense
		Color3f pointColor = new Color3f(0.4f, 0.4f, 0.4f);
		Point3f pointPoint = new Point3f(-5.0f, 0.0f, 3.0f);
		Point3f attn = new Point3f(0.0f, 0.07f, 0.0f);

		PointLight pLight = new PointLight(pointColor, pointPoint, attn);
		pLight.setInfluencingBounds(bs);
		// lights.addChild(pLight);

		// Main light
		Vector3f mainVector = new Vector3f(0.5f, 0.5f, -0.5f);

		DirectionalLight mainDL = new DirectionalLight(pointColor, mainVector);
		mainDL.setInfluencingBounds(bs);
		lights.addChild(mainDL);

		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

		Material bulbMat = new Material(pointColor, white, pointColor, pointColor, 0.0f);
		Appearance bulbApp = new Appearance();
		bulbApp.setMaterial(bulbMat);

		// Small Sphere to show source of the light
		Sphere lightBulb = new Sphere(0.7f, 2, 100, bulbApp);

		Transform3D tfLightBulb = new Transform3D();
		tfLightBulb.setTranslation(new Vector3f(-5.0f, 0.0f, 3.0f));

		TransformGroup tgLightBulb = new TransformGroup(tfLightBulb);

		tgLightBulb.addChild(lightBulb);

		// lights.addChild(tgLightBulb);

		// Add a directional light as 2ndary light
		Vector3f directionForLight = new Vector3f(-0.5f, 0.5f, -0.5f);

		Color3f colorForDL = new Color3f(0.25f, 0.25f, 0.25f);

		DirectionalLight dl = new DirectionalLight(colorForDL, directionForLight);
		dl.setInfluencingBounds(bs);

		lights.addChild(dl);

		// Add weak back light
		Vector3f backDLVector = new Vector3f(0.0f, 1.0f, 0.0f);
		Color3f weakLight = new Color3f(0.1f, 0.1f, 0.1f);

		DirectionalLight backDL = new DirectionalLight(weakLight, backDLVector);
		backDL.setInfluencingBounds(bs);

		lights.addChild(backDL);

		su.addBranchGraph(lights);
	}

	/*
	 * *************************************************************************
	 * *************************************************************************
	 * RESET THE GAME FUNCTIONS
	 * *************************************************************************
	 * *************************************************************************
	 */

	public static void resetGame() {

		// Start by reseting all the not promoted pieces and updating their
		// pieces
		for (int a = 0; a < 2; a++) {
			for (int b = 0; b < 8; b++) {
				piecesArray[a][b].resetGameNonPromoted();
				drawPiecesArray[a][b].setTransform(piecesArray[a][b].getTransform());
			}
		}
		for (int a = 6; a < 8; a++) {
			for (int b = 0; b < 8; b++) {
				piecesArray[a][b].resetGameNonPromoted();
				drawPiecesArray[a][b].setTransform(piecesArray[a][b].getTransform());
			}
		}

		// Castling rights are restored
		// Promotion should be taken care of and reset that both sides did no
		// promote

		if (flagWhitePromoted) {
			int length = whitePromotionPieces.length;
			for (int a = 0; a < length; a++) {

				whitePromotionPieces[a].setDeadPromoted();
				drawPiecesPromotionWhite[a].setTransform(whitePromotionPieces[a].getTransform());
			}
		}

		if (flagBlackPromoted) {

			int lengthBlack = blackPromotionPieces.length;
			for (int a = 0; a < lengthBlack; a++) {

				blackPromotionPieces[a].setDeadPromoted();
				drawPiecesPromotionBlack[a].setTransform(blackPromotionPieces[a].getTransform());

			}
		}	
		
		flagWhitePromoted = false;
		flagBlackPromoted = false;
	}

	public static void resetAlphaIndividual(int rowIndex, int colIndex, int rowInput, int colInput) {
		// get the number of interpolators, make array with all the numbers,
		// reset everything
		int numInterp = piecesArray[rowIndex][colIndex].numberOfInterpolators();
		int[] tempArray = new int[numInterp];
		for (int a = 0; a < numInterp; a++)
			tempArray[a] = a;

		piecesArray[rowIndex][colIndex].resetAlpha(tempArray);
	}
	public static void selectSquare(int row, int column) {
		
		
		
	}

}
