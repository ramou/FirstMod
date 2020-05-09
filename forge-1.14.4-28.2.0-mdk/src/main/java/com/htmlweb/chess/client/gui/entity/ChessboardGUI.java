package com.htmlweb.chess.client.gui.entity;

import java.util.HashMap;

import javax.vecmath.Point2f;

import org.lwjgl.opengl.GL11;

import com.htmlweb.chess.ChessMod;
import com.htmlweb.chess.common.dom.model.chess.Move;
import com.htmlweb.chess.common.dom.model.chess.PieceInitializer;
import com.htmlweb.chess.common.dom.model.chess.Point;
import com.htmlweb.chess.common.dom.model.chess.Side;
import com.htmlweb.chess.common.dom.model.chess.board.Board;
import com.htmlweb.chess.common.dom.model.chess.piece.Piece;
import com.htmlweb.chess.common.network.ChessPlay;
import com.htmlweb.chess.common.network.PacketHandler;
import com.htmlweb.chess.tileentity.ChessboardTileEntity;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;


public abstract class ChessboardGUI extends Screen {

	protected final ResourceLocation background;
	protected ChessboardTileEntity board;
	Tessellator tessellator = Tessellator.getInstance();
	BufferBuilder bufferbuilder = tessellator.getBuffer();
	protected Point selected = null;

	protected static HashMap<Character, TilePiece> pieceMap = new HashMap<Character, TilePiece>();

	protected enum TilePiece {
		WHITE_KING(0, Side.WHITE, PieceInitializer.nmK, 'K', new ResourceLocation(ChessMod.MODID, "textures/gui/wk.png")),
		WHITE_QUEEN(1, Side.WHITE, PieceInitializer.Q, 'Q', new ResourceLocation(ChessMod.MODID, "textures/gui/wq.png")),
		WHITE_ROOK(2, Side.WHITE, PieceInitializer.nmR, 'R', new ResourceLocation(ChessMod.MODID, "textures/gui/wr.png")),
		WHITE_KNIGHT(3, Side.WHITE, PieceInitializer.N, 'N', new ResourceLocation(ChessMod.MODID, "textures/gui/wn.png")),
		WHITE_BISHOP(4, Side.WHITE, PieceInitializer.B, 'B', new ResourceLocation(ChessMod.MODID, "textures/gui/wb.png")),
		WHITE_PAWN(5, Side.WHITE, PieceInitializer.P, 'P', new ResourceLocation(ChessMod.MODID, "textures/gui/wp.png")),
		BLACK_KING(0, Side.BLACK, PieceInitializer.nmK, 'k', new ResourceLocation(ChessMod.MODID, "textures/gui/bk.png")),
		BLACK_QUEEN(1, Side.BLACK, PieceInitializer.Q, 'q', new ResourceLocation(ChessMod.MODID, "textures/gui/bq.png")),
		BLACK_ROOK(2, Side.BLACK, PieceInitializer.nmR, 'r', new ResourceLocation(ChessMod.MODID, "textures/gui/br.png")),
		BLACK_KNIGHT(3, Side.BLACK, PieceInitializer.N, 'n', new ResourceLocation(ChessMod.MODID, "textures/gui/bn.png")),
		BLACK_BISHOP(4, Side.BLACK, PieceInitializer.B, 'b', new ResourceLocation(ChessMod.MODID, "textures/gui/bb.png")),
		BLACK_PAWN(5, Side.BLACK, PieceInitializer.P, 'p', new ResourceLocation(ChessMod.MODID, "textures/gui/bp.png"));
	
		public void draw(ChessboardGUI current, int x, int y) {
			current.drawPiece(x, y, tile);
		}
		
		public char getPiece() {
			return piece;
		}
	
		public int getIndex() {
			return index;
		}
	
		public Side getSide() {
			return side;
		}
		
		public PieceInitializer getPieceInitializer() {
			return pi;
		}
		
		public Piece create(Point p) {
			return pi.create(p, side);
		}
	
		int index;
		Side side;
		char piece;
		PieceInitializer pi;
		ResourceLocation tile;
		
		public ResourceLocation getTile() {
			return tile;
		}
	
		TilePiece(int i, Side s, PieceInitializer pi, char c, ResourceLocation tile) {
			this.index=i;
			this.side=s;
			this.pi=pi;
			this.piece=c;
			this.tile=tile;
			pieceMap.put(c, this);
		}
		
	}

	public ChessboardGUI(ChessboardTileEntity board) {
		super(null);
		background = new ResourceLocation(ChessMod.MODID, "textures/gui/chessboard.png");
		this.board = board;
	}

	@Override
	public String getNarrationMessage() {
		return "";
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	public void init() {

	}

	protected Piece pieceAt(Point s) {
		if(s == null) return null;
		return board.getBoard().pieceAt(Point.create(s.x, s.y));
	}
	
	protected void notifyServerOfMove(Move m) {
		PacketHandler.sendToServer(new ChessPlay(m.serialize(), board.getPos()));
	}


	protected void drawBackground() {
		this.minecraft.getTextureManager().bindTexture(background);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		float myHeight=Math.min(height, 256);
		float x1 = width/2f - 128;
		float x2 = x1+256;
		float y1 = height/2f - myHeight/2F;
		float y2 = y1+myHeight;
		bufferbuilder.pos(x1, y2, blitOffset).tex(0,1).endVertex();
		bufferbuilder.pos(x2, y2, blitOffset).tex(1,1).endVertex();
		bufferbuilder.pos(x2, y1, blitOffset).tex(1,0).endVertex();
		bufferbuilder.pos(x1, y1, blitOffset).tex(0,0).endVertex();
		tessellator.draw();
	}

	protected static class Color4f {
		float r,g,b,a;

		public Color4f(float r, float g, float b, float a) {
			super();
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}
		
		public static Color4f SELECTED = new Color4f(0.5F, 0.5F, 0.8F, 0.5F);
		public static Color4f POSSIBLE = new Color4f(0.5F, 0.8F, 0.5F, 0.5F);
		public static Color4f WHITE = new Color4f(1F, 1F, 1F, 1F);
		
		public void apply() {
			GlStateManager.color4f(r, g, b, a);
		}
	}
	
	protected void highlightSelected() {
		float myHeight=Math.min(height, 256);
		float x1 = width/2f - 128+32+selected.x*24;
		float x2 = x1+24;
		float y1 = height/2f - myHeight/2f+(32+selected.y*24)*myHeight/256f;
		float y2 = y1+24*myHeight/256f;
		
		Point2f p1 = new Point2f(x1, y1);
		Point2f p2 = new Point2f(x2, y2);
		
		highlightSquare(p1, p2, Color4f.SELECTED);
	}

	protected void highlightSquare(Point2f p1, Point2f p2, Color4f c) {
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		c.apply();
		  
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(p1.x, p2.y, blitOffset).endVertex();
		bufferbuilder.pos(p2.x, p2.y, blitOffset).endVertex();
		bufferbuilder.pos(p2.x, p1.y, blitOffset).endVertex();
		bufferbuilder.pos(p1.x, p1.y, blitOffset).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture();
		
		Color4f.WHITE.apply();
		GlStateManager.disableBlend();
	}



	protected void drawPiece(int bx, int by, ResourceLocation piece) {
		float myHeight=Math.min(height, 256);
		this.minecraft.getTextureManager().bindTexture(piece);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		float x1 = width/2f - 128+32+bx*24;
		float x2 = x1+24;
		float y1 = height/2f - myHeight/2f+(32+by*24)*myHeight/256f;
		float y2 = y1+24*myHeight/256f;
		bufferbuilder.pos(x1, y2, blitOffset).tex(0,1).endVertex();
		bufferbuilder.pos(x2, y2, blitOffset).tex(1,1).endVertex();
		bufferbuilder.pos(x2, y1, blitOffset).tex(1,0).endVertex();
		bufferbuilder.pos(x1, y1, blitOffset).tex(0,0).endVertex();
		tessellator.draw();
	}

	protected void drawSideboardPiece(TilePiece piece) {
		float myHeight=Math.min(height, 256);
		this.minecraft.getTextureManager().bindTexture(piece.tile);
	
		float x1 = width/2f - 128+(piece.side.equals(Side.BLACK)?0:16+9*24);
		float x2 = x1+24;
		float y1 = height/2f - myHeight/2f+(32+piece.index*24)*myHeight/256f;
		float y2 = y1+24*myHeight/256f;
		
		
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
	
		bufferbuilder.pos(x1, y2, blitOffset).tex(0,1).endVertex();
		bufferbuilder.pos(x2, y2, blitOffset).tex(1,1).endVertex();
		bufferbuilder.pos(x2, y1, blitOffset).tex(1,0).endVertex();
		bufferbuilder.pos(x1, y1, blitOffset).tex(0,0).endVertex();
		tessellator.draw();
	}

	protected void drawPieces() {
		Board b = board.getBoard();
	    for(int by = 0; by < 8; by++) { 
			for(int bx = 0; bx < 8; bx++) {
				Piece piece = b.pieceAt(Point.create(bx, by));
				if(piece != null) {
					pieceMap.get(piece.getCharacter()).draw(this, bx, by);
				}
			}
	    }
	}

}