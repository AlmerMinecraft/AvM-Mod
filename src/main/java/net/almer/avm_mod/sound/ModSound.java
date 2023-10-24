package net.almer.avm_mod.sound;

import net.almer.avm_mod.AvMMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSound {
    public static final Identifier ELECTRIC_GUITAR = new Identifier("avm_mod:electric_guitar");
    public static final Identifier FLUTE_MUSIC = new Identifier("avm_mod:flute_music");
    public static final Identifier GREEN_JAM = new Identifier("avm_mod:green_jam");
    public static final Identifier JAZZY_NOTE_BLOCKS = new Identifier("avm_mod:jazzy_note_blocks");
    public static SoundEvent ELECTRIC_GUITAR_EVENT = SoundEvent.of(ELECTRIC_GUITAR);
    public static SoundEvent FLUTE_MUSIC_EVENT = SoundEvent.of(FLUTE_MUSIC);
    public static SoundEvent GREEN_JAM_EVENT = SoundEvent.of(GREEN_JAM);
    public static SoundEvent JAZZY_NOTE_BLOCKS_EVENT = SoundEvent.of(JAZZY_NOTE_BLOCKS);
}
