package net.almer.avm_mod.util;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;

public class BrothCauldronBehaviour implements CauldronBehavior {
    public static final Map<Item, CauldronBehavior> BROTH_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final Map<Item, CauldronBehavior> OIL_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    public static final CauldronBehavior FILL_WITH_BROTH = (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModBlock.BROTH_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
    public static final CauldronBehavior FILL_WITH_OIL = (state, world, pos, player, hand, stack) -> CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ModBlock.OIL_CAULDRON.getDefaultState(), SoundEvents.ITEM_BUCKET_EMPTY);
    @Override
    public ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return null;
    }
    public static void registerBehavior(){
        BrothCauldronBehaviour.registerBucketBehavior(BROTH_CAULDRON_BEHAVIOR);
        BROTH_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModItem.BROTH_BUCKET), statex -> true, SoundEvents.ITEM_BUCKET_FILL));
        BrothCauldronBehaviour.registerBucketBehavior(OIL_CAULDRON_BEHAVIOR);
        OIL_CAULDRON_BEHAVIOR.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(ModItem.OIL_BUCKET), statex -> true, SoundEvents.ITEM_BUCKET_FILL));
    }
    public static void registerBucketBehavior(Map<Item, CauldronBehavior> behavior){
        behavior.put(ModItem.BROTH_BUCKET, FILL_WITH_BROTH);
        behavior.put(ModItem.OIL_BUCKET, FILL_WITH_OIL);
    }
}
