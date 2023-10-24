package net.almer.avm_mod.item.custom;

import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.sound.ModSound;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.Random;

public class FluteItem extends Item {
    List wardenList;
    double distance = 40;
    int ticking;
    public FluteItem(Settings settings) {

        super(settings);
    }
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(user.getOffHandStack().isOf(ModItem.NOTE_BOOK) || user.getMainHandStack().isOf(ModItem.NOTE_BOOK)){
            Box box = new Box(user.getBlockPos()).expand(distance);
            wardenList = user.getWorld().getEntitiesByClass(WardenEntity.class, box, e->e.isAlive());
            music(world, user, 4);
            user.setCurrentHand(hand);
            for(int i = 0; i < wardenList.size(); i++){
                WardenEntity warden = (WardenEntity)wardenList.get(i);
                warden.setAiDisabled(true);
            }
            user.getItemCooldownManager().set(this, getUseDuration(user.getStackInHand(hand)));
            cooldown(300, user, world);
        }
        else{
            world.playSoundFromEntity(user, user, SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), SoundCategory.RECORDS, 4.0f, rand.nextFloat());
            world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, user.getPos(), GameEvent.Emitter.of(user));
        }
        ItemStack itemStack = user.getStackInHand(hand);
        return TypedActionResult.consume(itemStack);
    }
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
    public int getUseDuration(ItemStack itemStack){
        return 400;
    }
    Random rand = new Random();
    public void music(World world, PlayerEntity user, float vol){
        world.playSoundFromEntity(user, user,
                ModSound.FLUTE_MUSIC_EVENT,
                SoundCategory.RECORDS, vol, 1f);
        world.emitGameEvent(GameEvent.INSTRUMENT_PLAY, user.getPos(), GameEvent.Emitter.of(user));
    }
    public void cooldown(int duration, PlayerEntity user, World world) {
        double ticking = world.getTime();
        if((world.getTime() - ticking) >= duration){
            Box box = new Box(user.getBlockPos()).expand(distance);
            wardenList = user.getWorld().getEntitiesByClass(WardenEntity.class, box, e->e.isAlive());
            for(int j = 0; j < wardenList.size(); j++){
                WardenEntity warden = (WardenEntity)wardenList.get(j);
                warden.setAiDisabled(false);
            }
        }
    }
}
