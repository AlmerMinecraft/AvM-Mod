package net.almer.avm_mod.network.packet;

import net.almer.avm_mod.client.screen.PowerfulStaffScreen;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.almer.avm_mod.network.ModPacketListener;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class AddCommandsC2SPacket implements Packet<ModPacketListener> {
    private final String command1;
    private final String command2;
    private final String command3;
    private final String command4;
    private final String command5;
    private final String command6;
    public static List<String> commands;
    public AddCommandsC2SPacket(String command1, String command2, String command3, String command4, String command5, String command6){
        this.command1 = command1;
        this.command2 = command2;
        this.command3 = command3;
        this.command4 = command4;
        this.command5 = command5;
        this.command6 = command6;
        this.getCommands();
    }
    public void getCommands(){
        ArrayList<String> list = new ArrayList<>();
            list.add(0, command1);
            list.add(1, command2);
            list.add(2, command3);
            list.add(3, command4);
            list.add(4, command5);
            list.add(5, command6);
            this.commands = list;
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(command1);
        buf.writeString(command2);
        buf.writeString(command3);
        buf.writeString(command4);
        buf.writeString(command5);
        buf.writeString(command6);
    }

    @Override
    public void apply(ModPacketListener listener) {
        listener.onPowerfulStaffClosed(this);
    }
    public static List<String> getCommands1(){
        return commands;
    }
}
