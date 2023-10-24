package net.almer.avm_mod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WinnerStatueBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public WinnerStatueBlock(Settings settings) {
        super(settings);
    }
    private static final VoxelShape SHAPE_N = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 0.5, 11),
            Block.createCuboidShape(5, 10.5, 7.25, 11, 12, 8.75),
            Block.createCuboidShape(7.5, 12, 7.5, 8.5, 15, 8.5),
            Block.createCuboidShape(6, 11.499999999999998, 6, 10, 15.500000000000005, 10),
            Block.createCuboidShape(6, 7, 7, 10, 12.500000000000004, 9),
            Block.createCuboidShape(4.5, 10, 7, 6.5, 12.500000000000004, 9),
            Block.createCuboidShape(9.5, 10, 7, 11.5, 12.500000000000004, 9),
            Block.createCuboidShape(8, 1.1, 7, 10, 5.600000000000001, 9),
            Block.createCuboidShape(6, 1.1, 7, 8, 5.600000000000001, 9),
            Block.createCuboidShape(6, 5.6000000000000005, 7, 10, 8.100000000000001, 9),
            Block.createCuboidShape(8, 0.4, 7, 10, 3.4, 9),
            Block.createCuboidShape(6, 0.4, 7, 8.000000000000002, 3.4, 9)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_S = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 0.5, 11),
            Block.createCuboidShape(5, 10.5, 7.25, 11, 12, 8.75),
            Block.createCuboidShape(7.5, 12, 7.5, 8.5, 15, 8.5),
            Block.createCuboidShape(6, 11.499999999999998, 6, 10, 15.500000000000005, 10),
            Block.createCuboidShape(6, 7, 7, 10, 12.500000000000004, 9),
            Block.createCuboidShape(4.5, 10, 7, 6.5, 12.500000000000004, 9),
            Block.createCuboidShape(9.5, 10, 7, 11.5, 12.500000000000004, 9),
            Block.createCuboidShape(8, 1.1, 7, 10, 5.600000000000001, 9),
            Block.createCuboidShape(6, 1.1, 7, 8, 5.600000000000001, 9),
            Block.createCuboidShape(6, 5.6000000000000005, 7, 10, 8.100000000000001, 9),
            Block.createCuboidShape(8, 0.4, 7, 10, 3.4, 9),
            Block.createCuboidShape(6, 0.4, 7, 8.000000000000002, 3.4, 9)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_W = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 0.5, 11),
            Block.createCuboidShape(7.25, 10.5, 5, 8.75, 12, 11),
            Block.createCuboidShape(7.5, 12, 7.5, 8.5, 15, 8.5),
            Block.createCuboidShape(6, 11.499999999999998, 6, 10, 15.500000000000005, 10),
            Block.createCuboidShape(7, 7, 6, 9, 12.500000000000004, 10),
            Block.createCuboidShape(7, 10, 9.5, 9, 12.500000000000004, 11.5),
            Block.createCuboidShape(7, 10, 4.5, 9, 12.500000000000004, 6.5),
            Block.createCuboidShape(7, 1.1, 6, 9, 5.600000000000001, 8),
            Block.createCuboidShape(7, 1.1, 8, 9, 5.600000000000001, 10),
            Block.createCuboidShape(7, 5.6000000000000005, 6, 9, 8.100000000000001, 10),
            Block.createCuboidShape(7, 0.4, 6, 9, 3.4, 8),
            Block.createCuboidShape(7, 0.4, 7.999999999999998, 9, 3.4, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    private static final VoxelShape SHAPE_E = Stream.of(
            Block.createCuboidShape(5, 0, 5, 11, 0.5, 11),
            Block.createCuboidShape(7.25, 10.5, 5, 8.75, 12, 11),
            Block.createCuboidShape(7.5, 12, 7.5, 8.5, 15, 8.5),
            Block.createCuboidShape(6, 11.499999999999998, 6, 10, 15.500000000000005, 10),
            Block.createCuboidShape(7, 7, 6, 9, 12.500000000000004, 10),
            Block.createCuboidShape(7, 10, 9.5, 9, 12.500000000000004, 11.5),
            Block.createCuboidShape(7, 10, 4.5, 9, 12.500000000000004, 6.5),
            Block.createCuboidShape(7, 1.1, 6, 9, 5.600000000000001, 8),
            Block.createCuboidShape(7, 1.1, 8, 9, 5.600000000000001, 10),
            Block.createCuboidShape(7, 5.6000000000000005, 6, 9, 8.100000000000001, 10),
            Block.createCuboidShape(7, 0.4, 6, 9, 3.4, 8),
            Block.createCuboidShape(7, 0.4, 7.999999999999998, 9, 3.4, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH:
                return SHAPE_N;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            case EAST:
                return SHAPE_E;
            default:
                return SHAPE_N;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
