package net.almer.avm_mod.network;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.network.packet.AddCommandsC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages {
    public static final Identifier STAFF_ID = new Identifier(AvMMod.MOD_ID, "staff");

    public static void registerC2SPackets(){

    }
}
