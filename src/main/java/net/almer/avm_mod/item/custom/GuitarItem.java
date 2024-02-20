package net.almer.avm_mod.item.custom;

import net.almer.avm_mod.effect.ModEffect;
import net.almer.avm_mod.sound.ModSound;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class GuitarItem extends Item {
    public GuitarItem(Settings settings) {
        super(settings);
    }
    Random rand = new Random();

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!(user.getStatusEffects().stream().anyMatch(s->s.getEffectType() == ModEffect.MUSICALITY))) {
            world.playSound(user, BlockPos.ofFloored(user.getPos()),
                    ModSound.ELECTRIC_GUITAR_EVENT,
                    SoundCategory.RECORDS, 3f, rand.nextFloat(1, 2));
            world.addParticle(ParticleTypes.NOTE.getType(),
                    user.getX() + rand.nextFloat(-1f, 1f),
                    user.getY() + 1d,
                    user.getZ() + rand.nextFloat(-1f, 1f), 0, 0, 0);
        }
        else{
            world.playSound(user, BlockPos.ofFloored(user.getPos()),
                    ModSound.GREEN_JAM_EVENT,
                    SoundCategory.RECORDS, 3f, 1f);
            user.getItemCooldownManager().set(this, getUseDuration(user.getStackInHand(hand)));
        }
        return super.use(world, user, hand);
    }
    public int getUseDuration(ItemStack itemStack){
        return 240;
    }
}

