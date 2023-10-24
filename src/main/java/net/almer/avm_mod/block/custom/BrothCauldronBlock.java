package net.almer.avm_mod.block.custom;

import net.almer.avm_mod.util.BrothCauldronBehaviour;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class BrothCauldronBlock extends AbstractCauldronBlock {

    public BrothCauldronBlock(Settings settings) {
        super(settings, BrothCauldronBehaviour.BROTH_CAULDRON_BEHAVIOR);
    }
    @Override
    public boolean isFull(BlockState state) {
        return true;
    }
    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return 3;
    }
}
