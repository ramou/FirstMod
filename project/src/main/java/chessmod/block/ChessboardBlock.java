package chessmod.block;

import chessmod.tileentity.ChessboardTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public abstract class ChessboardBlock extends GlassBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public ChessboardBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape BOARD = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		VoxelShape STAND = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
		return VoxelShapes.or(BOARD, STAND);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return false;
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	private static final Direction[] ClockWise_Directions = {
			Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
	};


	@Override
	public ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos, final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
		// Only open the gui on the physical client
		DistExecutor.runWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
			if (handIn.equals(Hand.MAIN_HAND) && player.getHeldItemMainhand().getItem().getRegistryName().getPath().equals("chess_wrench")) {
				Direction currentFacing = state.get(FACING);
				Direction newFacing = getDirection(currentFacing);
				worldIn.setBlockState(pos, state.with(FACING, newFacing));
			}
		});

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			if (player.getHeldItemMainhand().getItem().getRegistryName().getPath().equals("chess_wrench")) {
				if(handIn.equals(Hand.MAIN_HAND)) {
					Direction currentFacing = state.get(FACING);
					Direction newFacing = getDirection(currentFacing);
					worldIn.setBlockState(pos, state.with(FACING, newFacing));
				}
			} else {
				openGui(worldIn, pos);
			}
		});
		return ActionResultType.PASS;
	}

	private Direction getDirection(Direction direction){
		int index = direction.getHorizontalIndex();
		return ClockWise_Directions[(index+1) % ClockWise_Directions.length];
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		if(player.getHeldItemMainhand().getItem().getRegistryName().getPath().equals("chess_wrench")) {
			if(world.getTileEntity(pos) instanceof ChessboardTileEntity) {
				ChessboardTileEntity chessboard = (ChessboardTileEntity) world.getTileEntity(pos);
				chessboard.initialize();
				chessboard.notifyClientOfBoardChange();
				return false;
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	protected abstract void openGui(final World worldIn, final BlockPos pos);

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		//Don't mess around, if they're looking up or down, direction is east.
		//If we really want to make this nice we can examine relative directions
		//carefully at a later date.

		Direction face = context.getNearestLookingDirection().getOpposite();
		if(face == Direction.UP || face == Direction.DOWN) face = Direction.NORTH;
		return this.getDefaultState().with(FACING, face);
	}


}