package net.almer.avm_mod.network;

import net.almer.avm_mod.network.packet.AddCommandsC2SPacket;
import net.minecraft.network.listener.ServerPacketListener;

public interface ModPacketListener extends ServerPacketListener {
    public void onPowerfulStaffClosed(AddCommandsC2SPacket var1);
}
