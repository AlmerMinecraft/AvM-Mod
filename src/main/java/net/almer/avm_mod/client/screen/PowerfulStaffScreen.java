package net.almer.avm_mod.client.screen;

import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.item.custom.PowerfulStaffItem;
import net.almer.avm_mod.network.ModMessages;
import net.almer.avm_mod.network.packet.AddCommandsC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PowerfulStaffScreen extends Screen {
    public PowerfulStaffScreen(ItemStack staff) {
        super(Text.translatable("gui.avm_mod.powerful_staff_screen"));
        this.staff = staff;
    }
    private static final Text SET_COMMAND_TEXT = Text.translatable("gui.avm_mod.setCommand");
    private static final Text COMMAND_TEXT = Text.translatable("advMode.command");
    protected static TextFieldWidget consoleCommandTextField1;
    protected static TextFieldWidget consoleCommandTextField2;
    protected static TextFieldWidget consoleCommandTextField3;
    protected static TextFieldWidget consoleCommandTextField4;
    protected static TextFieldWidget consoleCommandTextField5;
    protected static TextFieldWidget consoleCommandTextField6;
    ModChatInputSuggestor commandSuggestor1;
    ModChatInputSuggestor commandSuggestor2;
    ModChatInputSuggestor commandSuggestor3;
    ModChatInputSuggestor commandSuggestor4;
    ModChatInputSuggestor commandSuggestor5;
    ModChatInputSuggestor commandSuggestor6;
    private final ItemStack staff;
    private static PlayerEntity player;

    @Override
    protected void init() {
        this.consoleCommandTextField1 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField1.setMaxLength(32500);
        this.consoleCommandTextField1.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField1);
        this.consoleCommandTextField2 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 80, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField2.setMaxLength(32500);
        this.consoleCommandTextField2.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField2);
        this.consoleCommandTextField3 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 110, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField3.setMaxLength(32500);
        this.consoleCommandTextField3.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField3);
        this.consoleCommandTextField4 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 140, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField4.setMaxLength(32500);
        this.consoleCommandTextField4.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField4);
        this.consoleCommandTextField5 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 170, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField5.setMaxLength(32500);
        this.consoleCommandTextField5.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField5);
        this.consoleCommandTextField6 = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 200, 300, 20, (Text)Text.translatable("advMode.command"));
        this.consoleCommandTextField6.setMaxLength(32500);
        this.consoleCommandTextField6.setChangedListener(this::onCommandChanged);
        this.addSelectableChild(this.consoleCommandTextField6);
        this.setInitialFocus(this.consoleCommandTextField1);
        this.commandSuggestor1 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField1, this.textRenderer, true, false, 0, 7, 72, false, Integer.MIN_VALUE);
        this.commandSuggestor2 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField2, this.textRenderer, true, false, 0, 7, 102, false, Integer.MIN_VALUE);
        this.commandSuggestor3 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField3, this.textRenderer, true, false, 0, 7, 132,false, Integer.MIN_VALUE);
        this.commandSuggestor4 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField4, this.textRenderer, true, false, 0, 7, 162,false, Integer.MIN_VALUE);
        this.commandSuggestor5 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField5, this.textRenderer, true, false, 0, 7, 192,false, Integer.MIN_VALUE);
        this.commandSuggestor6 = new ModChatInputSuggestor(this.client, this, this.consoleCommandTextField6, this.textRenderer, true, false, 0, 7, 222,false, Integer.MIN_VALUE);
        this.commandSuggestor1.setWindowActive(true);
        this.commandSuggestor2.setWindowActive(true);
        this.commandSuggestor3.setWindowActive(true);
        this.commandSuggestor4.setWindowActive(true);
        this.commandSuggestor5.setWindowActive(true);
        this.commandSuggestor6.setWindowActive(true);
        this.commandSuggestor1.refresh();
        this.commandSuggestor2.refresh();
        this.commandSuggestor3.refresh();
        this.commandSuggestor4.refresh();
        this.commandSuggestor5.refresh();
        this.commandSuggestor6.refresh();
    }
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string1 = this.consoleCommandTextField1.getText();
        String string2 = this.consoleCommandTextField2.getText();
        String string3 = this.consoleCommandTextField3.getText();
        String string4 = this.consoleCommandTextField4.getText();
        String string5 = this.consoleCommandTextField5.getText();
        String string6 = this.consoleCommandTextField6.getText();
        this.init(client, width, height);
        this.consoleCommandTextField1.setText(string1);
        this.consoleCommandTextField2.setText(string2);
        this.consoleCommandTextField3.setText(string3);
        this.consoleCommandTextField4.setText(string4);
        this.consoleCommandTextField5.setText(string5);
        this.consoleCommandTextField6.setText(string6);
        this.commandSuggestor1.refresh();
        this.commandSuggestor2.refresh();
        this.commandSuggestor3.refresh();
        this.commandSuggestor4.refresh();
        this.commandSuggestor5.refresh();
        this.commandSuggestor6.refresh();
    }
    private void onCommandChanged(String text) {
        this.commandSuggestor1.refresh();
        this.commandSuggestor2.refresh();
        this.commandSuggestor3.refresh();
        this.commandSuggestor4.refresh();
        this.commandSuggestor5.refresh();
        this.commandSuggestor6.refresh();
        PowerfulStaffItem staff = (PowerfulStaffItem)this.getPlayer().getMainHandStack().getItem();
        staff.commands(getReadyText(consoleCommandTextField1.getText()),
                getReadyText(consoleCommandTextField2.getText()),
                getReadyText(consoleCommandTextField3.getText()),
                getReadyText(consoleCommandTextField4.getText()),
                getReadyText(consoleCommandTextField5.getText()),
                getReadyText(consoleCommandTextField6.getText()));
    }
    public String getReadyText(String text){
        return text != null ? text : "";
    }
    public static void addCommands(List<String> commands){
        if(commands != null) {
            consoleCommandTextField1.setText(commands.get(0));
            consoleCommandTextField2.setText(commands.get(1));
            consoleCommandTextField3.setText(commands.get(2));
            consoleCommandTextField4.setText(commands.get(3));
            consoleCommandTextField5.setText(commands.get(4));
            consoleCommandTextField6.setText(commands.get(5));
        }
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.commandSuggestor1.mouseScrolled(horizontalAmount)) {
            return true;
        }
        if (this.commandSuggestor2.mouseScrolled(horizontalAmount)) {
            return true;
        }
        if (this.commandSuggestor3.mouseScrolled(horizontalAmount)) {
            return true;
        }
        if (this.commandSuggestor4.mouseScrolled(horizontalAmount)) {
            return true;
        }
        if (this.commandSuggestor5.mouseScrolled(horizontalAmount)) {
            return true;
        }
        if (this.commandSuggestor6.mouseScrolled(horizontalAmount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.commandSuggestor1.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commandSuggestor2.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commandSuggestor3.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commandSuggestor4.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commandSuggestor5.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.commandSuggestor6.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, SET_COMMAND_TEXT, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, COMMAND_TEXT, this.width / 2 - 150, 40, 0xA0A0A0);
        this.consoleCommandTextField1.render(context, mouseX, mouseY, delta);
        this.consoleCommandTextField2.render(context, mouseX, mouseY, delta);
        this.consoleCommandTextField3.render(context, mouseX, mouseY, delta);
        this.consoleCommandTextField4.render(context, mouseX, mouseY, delta);
        this.consoleCommandTextField5.render(context, mouseX, mouseY, delta);
        this.consoleCommandTextField6.render(context, mouseX, mouseY, delta);
        int i = 75;
        super.render(context, mouseX, mouseY, delta);
        this.commandSuggestor1.render(context, mouseX, mouseY);
        this.commandSuggestor2.render(context, mouseX, mouseY);
        this.commandSuggestor3.render(context, mouseX, mouseY);
        this.commandSuggestor4.render(context, mouseX, mouseY);
        this.commandSuggestor5.render(context, mouseX, mouseY);
        this.commandSuggestor6.render(context, mouseX, mouseY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.commandSuggestor1.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.commandSuggestor2.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.commandSuggestor3.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.commandSuggestor4.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.commandSuggestor5.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (this.commandSuggestor6.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.syncSettingsToServer();
            return true;
        }
        return false;
    }
    public List<ModChatInputSuggestor> getSuggestors(){
        ArrayList<ModChatInputSuggestor> list = new ArrayList<>();
        list.add(this.commandSuggestor1);
        list.add(this.commandSuggestor2);
        list.add(this.commandSuggestor3);
        list.add(this.commandSuggestor4);
        list.add(this.commandSuggestor5);
        list.add(this.commandSuggestor6);
        return list;
    }
    public static List<TextFieldWidget> getWidgets(){
        ArrayList<TextFieldWidget> list = new ArrayList<>();
        list.add(consoleCommandTextField1);
        list.add(consoleCommandTextField2);
        list.add(consoleCommandTextField3);
        list.add(consoleCommandTextField4);
        list.add(consoleCommandTextField5);
        list.add(consoleCommandTextField6);
        for(int i = 0; i < list.size(); i++){
            AvMMod.LOGGER.info(list.get(i).getText());
        }
        return list;
    }
    private static PlayerEntity getPlayer(){
        return player;
    }
    public void addPlayer(PlayerEntity player){
        this.player = player;
    }
    protected void syncSettingsToServer() {
        this.client.getNetworkHandler().sendPacket(new AddCommandsC2SPacket(consoleCommandTextField1.getText(),
                consoleCommandTextField2.getText(),
                consoleCommandTextField3.getText(),
                consoleCommandTextField4.getText(),
                consoleCommandTextField5.getText(),
                consoleCommandTextField6.getText()));
    }
}
