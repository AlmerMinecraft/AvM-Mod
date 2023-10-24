package net.almer.avm_mod.item.custom;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.mixin.ClientPlayerInteractionManagerMixin;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GameIconItem extends Item {
    private static PlayerEntity player;
    public static int MODE_INDEX = 0;
    public GameIconItem(Settings settings) {
        super(settings);
    }
    public static PlayerEntity getPlayer(){
        return player;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(entity.isPlayer()) {
            player = (PlayerEntity) entity;
            PlayerEntity player = (PlayerEntity) entity;
            if(!player.getAbilities().creativeMode) {
                if (player.getMainHandStack().isOf(ModItem.GAME_ICON) && MODE_INDEX == 1) {
                    player.getAbilities().allowFlying = true;
                } else {
                    player.getAbilities().flying = false;
                    player.getAbilities().allowFlying = false;
                }
            }
            if (AvMModClient.POWERFUL_STAFF_USE.isPressed() && player.getMainHandStack().isOf(ModItem.GAME_ICON)) {
                if (AvMModClient.POWERFUL_STAFF_USE_1.isPressed()) {
                    player.sendMessage(Text.translatable("gui.avm_mod.game_icon_mode1"), true);
                    this.MODE_INDEX = 0;
                } else if (AvMModClient.POWERFUL_STAFF_USE_2.isPressed()) {
                    player.sendMessage(Text.translatable("gui.avm_mod.game_icon_mode2"), true);
                    this.MODE_INDEX = 1;
                }
            }
        }
    }
}
