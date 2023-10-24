package net.almer.avm_mod.util;

import net.minecraft.Bootstrap;

public class ModBoostrap extends Bootstrap {
    public static void initialize(){
        BrothCauldronBehaviour.registerBehavior();
    }
}
