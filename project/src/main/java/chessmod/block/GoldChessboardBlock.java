package chessmod.block;

import javax.annotation.Nullable;

import chessmod.blockentity.ChessboardBlockEntity;
import chessmod.blockentity.GoldChessboardBlockEntity;
import chessmod.client.gui.entity.GoldChessboardGui;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GoldChessboardBlock extends ChessboardBlock {
	public GoldChessboardBlock() {
		super();	
	}

	public GoldChessboardBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GoldChessboardBlockEntity(pos, state);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	protected void openGui(final Level levelIn, final BlockPos pos) {
		final BlockEntity blockEntity = levelIn.getBlockEntity(pos);
		if (blockEntity instanceof ChessboardBlockEntity) {
			Minecraft.getInstance().setScreen(new GoldChessboardGui((ChessboardBlockEntity)blockEntity));
		}
	}

	
}
