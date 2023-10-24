package net.almer.avm_mod.block.custom;

import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class DeactivatedStaffBlock extends Block {
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 3);
    int multplier = 1;
    public DeactivatedStaffBlock(Settings settings) {
        super(settings);
    }
    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(7.6, 26.5, 7.6, 9.1, 28, 9.1),
            Block.createCuboidShape(7.6, -2, 7.6, 9.1, 19.75, 9.1),
            Block.createCuboidShape(7.6, 19.75, 7.6, 9.1, 20.5, 11.725),
            Block.createCuboidShape(7.6, 25.75, 7.6, 9.1, 26.5, 11.725),
            Block.createCuboidShape(7.6, 20.5, 10.975, 9.1, 25.75, 11.725)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int currentLevel = this.getLevel(state);
        if(player.getMainHandStack().isEmpty()){
            if(currentLevel == 0){
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Hand handle = player.getActiveHand();
                player.setStackInHand(handle, ModItem.DEACTIVATED_STAFF.getDefaultStack());
            }
            else if(currentLevel == 3){
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                Hand handle = player.getActiveHand();
                player.setStackInHand(handle, ModItem.POWERFUL_STAFF.getDefaultStack());
            }
            else{
                player.sendMessage(Text.translatable("block.avm_mod.deactivated_staff.not_powerful"), true);
            }
        }
        else if(player.getMainHandStack().isOf(Items.LAPIS_LAZULI)){
            if(world.getBlockState(pos.down(1)).isOf(Blocks.ENCHANTING_TABLE)){
                if(player.experienceLevel >= multplier) {
                    if(currentLevel < 3) {
                        world.setBlockState(pos, state.with(LEVEL, ++currentLevel));
                        player.getMainHandStack().decrement(1);
                        player.experienceLevel -= multplier;
                        world.playSound(player, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS);
                        world.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
                        if(currentLevel == 2){
                            multplier = 1;
                        }
                        else {
                            multplier += 2;
                        }
                        return ActionResult.SUCCESS;
                    }
                    else{
                        player.sendMessage(Text.translatable("block.avm_mod.deactivated_staff.too_charged"), true);
                    }
                }
                else{
                    player.sendMessage(Text.translatable("block.avm_mod.deactivated_staff.not_experienced"), true);
                }
            }
            else{
                player.sendMessage(Text.translatable("block.avm_mod.deactivated_staff.cant_charge"), true);
                return ActionResult.FAIL;
            }
        }
        else{
            return ActionResult.FAIL;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
    public int getLevel(BlockState state){
        return state.get(this.LEVEL);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }
}
