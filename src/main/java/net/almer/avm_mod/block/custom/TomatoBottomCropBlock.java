package net.almer.avm_mod.block.custom;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemConvertible;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;

public class TomatoBottomCropBlock extends CropBlock {
    public static final int FIRST_STAGE_MAX_AGE = 3;
    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    public static final IntProperty AGE = IntProperty.of("age", 0, 3);
    public TomatoBottomCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[this.getAge(state)];
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int currentAge = this.getAge(state);
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            if (currentAge < this.getMaxAge()) {
                float f = getAvailableMoisture(this, world, pos);
                if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                    this.applyGrowth(world, pos, state);
                }
            }
        }
        if(world.getBlockState(pos.up(1)).isOf(Blocks.AIR)){
            if (currentAge == 1) {
                world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 0), 2);
            }
            if (currentAge == 2) {
                world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 1), 2);
            }
            if (currentAge == 3) {
                world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 2), 2);
            }
        }
    }
    @Override
    public void applyGrowth(World world, BlockPos pos, BlockState state) {
        int nextAge = this.getAge(state) + this.getGrowthAmount(world);
        int maxAge = this.getMaxAge();
        int currentAge = this.getAge(state);
        if(nextAge > maxAge) {
            nextAge = maxAge;
        }
        if (currentAge == 0) {
            world.setBlockState(pos, this.withAge(1), 2);
            world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 0), 2);
        }
        if (currentAge == 1) {
            world.setBlockState(pos, this.withAge(2), 2);
            world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 1), 2);
        }
        if (currentAge == 2) {
            world.setBlockState(pos, this.withAge(3), 2);
            world.setBlockState(pos.up(1), ModBlock.TOMATO_UPPER_CROP.getDefaultState().with(Properties.AGE_7, 2), 2);
        }
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return super.canPlaceAt(state, world, pos) || (world.getBlockState(pos.down(1)).isOf(this) &&
                world.getBlockState(pos.down(1)).get(AGE) == 7);
    }

    @Override
    public int getMaxAge() {
        return FIRST_STAGE_MAX_AGE;
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItem.TOMATO_SEEDS;
    }

    @Override
    protected IntProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}
