package net.almer.avm_mod.item.custom;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContextBuilder;
import net.almer.avm_mod.AvMMod;
import net.almer.avm_mod.AvMModClient;
import net.almer.avm_mod.client.screen.PowerfulStaffScreen;
import net.almer.avm_mod.entity.ModEntities;
import net.almer.avm_mod.entity.custom.dark.*;
import net.almer.avm_mod.item.ModItem;
import net.almer.avm_mod.network.packet.AddCommandsC2SPacket;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.awt.event.InputEvent;
import java.security.Key;
import java.util.*;
import java.util.stream.Stream;

public class PowerfulStaffItem extends ToolItem {
    private static final String ITEMS_KEY = "Block";
    private static final String BLOCKED_KEY = "Blocked";
    private static final String currentCommandKey = "gui.avm_mod.current_text";
    private static final int zombieIndex = 1;
    private static final int skeletonIndex = 2;
    private static final int creeperIndex = 3;
    private static final int spiderIndex = 4;
    private static final int endermanIndex = 5;
    private static final int phantomIndex = 6;
    private float attackDamage;
    private float attackSpeed;
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private ItemStack staff;
    public static String currentCommand = "";
    public static int currentMode = 1;
    public static List<String> bufferedCommands;
    NbtList commandList;
    NbtList entityList;
    public static List absorpedEntities;
    private int remains = 10;
    private int index = 0;
    private static MinecraftServer server;
    public PowerfulStaffItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        index = 0;
    }
    public float getAttackDamage() {
        return this.attackDamage;
    }
    public static PlayerEntity getPlayer(){
        PlayerEntity player = MinecraftClient.getInstance().player;
        return player;
    }
    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        }
        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
            this.playRemoveOneSound(player);
            this.removeFirstStack(stack).ifPresent(removedStack -> this.addToBundle(stack, slot.insertStack((ItemStack)removedStack)));
        } else if (itemStack.getItem().canBeNested()) {
            int i = (1 - this.getBundleOccupancy(stack)) / this.getItemOccupancy(itemStack);
            if(itemStack.isOf(Blocks.GOLD_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.DIAMOND_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.NETHERITE_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.IRON_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.COPPER_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.EMERALD_BLOCK.asItem()) ||
                    itemStack.isOf(Blocks.COMMAND_BLOCK.asItem()) || itemStack.isOf(ModItem.GAME_ICON)) {
                PowerfulStaffItem.setBlocked(stack, true);
                this.staff = stack;
                int j = this.addToBundle(stack, slot.takeStackRange(itemStack.getCount(), i, player));
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }
        }
        return true;
    }
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        this.staff = stack;
        this.attackDamage = 6 + muliplicate(stack);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)this.attackSpeed, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
        if(hasBlocks(stack, Items.COMMAND_BLOCK) && entity.isPlayer()){
            PlayerEntity player = (PlayerEntity) entity;
            AddCommandsC2SPacket.getCommands1();
            if(AvMModClient.POWERFUL_STAFF_USE.isPressed() && player.isCreativeLevelTwoOp()){
                if(this.commandList != null) {
                    if (AvMModClient.POWERFUL_STAFF_USE_1.isPressed()) {
                        currentCommand = this.commandList.getCompound(0).getString("Command0");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    } else if (AvMModClient.POWERFUL_STAFF_USE_2.isPressed()) {
                        currentCommand = this.commandList.getCompound(1).getString("Command1");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    } else if (AvMModClient.POWERFUL_STAFF_USE_3.isPressed()) {
                        currentCommand = this.commandList.getCompound(2).getString("Command2");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    } else if (AvMModClient.POWERFUL_STAFF_USE_4.isPressed()) {
                        currentCommand = this.commandList.getCompound(3).getString("Command3");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    } else if (AvMModClient.POWERFUL_STAFF_USE_5.isPressed()) {
                        currentCommand = this.commandList.getCompound(4).getString("Command4");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    } else if (AvMModClient.POWERFUL_STAFF_USE_6.isPressed()) {
                        currentCommand = this.commandList.getCompound(5).getString("Command5");
                        player.sendMessage(Text.translatable(currentCommandKey).append(currentCommand), true);
                    }
                }
            }
        }
        if(hasBlocks(stack, ModItem.GAME_ICON) && entity.isPlayer()){
            PlayerEntity player = (PlayerEntity) entity;
            NbtCompound nbtCompound = stack.getOrCreateNbt();
            this.entityList = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
            if(!nbtCompound.contains("Absorbed") || (nbtCompound.contains("Absorbed") && nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE).isEmpty())){
                nbtCompound.put("Absorbed", new NbtList());
                this.entityList = new NbtList();
                index = 0;
                this.addPlaceholder(this.staff);
            }
            if(index >= 10){
                index = 0;
            }
            if(!player.getAbilities().creativeMode) {
                if (player.getMainHandStack().isOf(ModItem.GAME_ICON) && currentMode == 1) {
                    player.getAbilities().allowFlying = true;
                } else {
                    player.getAbilities().flying = false;
                    player.getAbilities().allowFlying = false;
                }
            }
            if(AvMModClient.POWERFUL_STAFF_USE.isPressed()){
                if(AvMModClient.POWERFUL_STAFF_USE_1.isPressed()){
                    currentMode = 1;
                    player.sendMessage(Text.translatable("gui.avm_mod.game_icon_mode2"), true);
                }
                else if(AvMModClient.POWERFUL_STAFF_USE_2.isPressed()){
                    currentMode = 2;
                    player.sendMessage(Text.translatable("gui.avm_mod.game_icon_mode3"), true);
                }
                else if(AvMModClient.POWERFUL_STAFF_USE_3.isPressed()){
                    currentMode = 3;
                    player.sendMessage(Text.translatable("gui.avm_mod.game_icon_mode4"), true);
                }
            }
        }
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hasBlocks(user.getMainHandStack(), Items.COMMAND_BLOCK) && user.isCreativeLevelTwoOp()) {
            if (AvMModClient.POWERFUL_STAFF_USE.isPressed()) {
                PowerfulStaffScreen screen = new PowerfulStaffScreen(this.staff);
                MinecraftClient.getInstance().setScreenAndRender(screen);
                screen.addPlayer(user);
                screen.addCommands(bufferedCommands);
                NbtCompound nbtCompound = this.staff.getOrCreateNbt();
                if (!nbtCompound.contains("Commands")) {
                    nbtCompound.put("Commands", new NbtList());
                }
                this.commandList = nbtCompound.getList("Commands", NbtElement.COMPOUND_TYPE);
                TypedActionResult.success(user.getMainHandStack());
            } else {
                if (currentCommand != "" && !AvMModClient.POWERFUL_STAFF_USE.isPressed()) {
                    if (currentCommand.startsWith("/")) {
                        String readyCommand = currentCommand.substring(1);
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand(readyCommand);
                    } else {
                        MinecraftClient.getInstance().player.networkHandler.sendChatCommand(currentCommand);
                    }
                }
            }
        }
        if(hasBlocks(user.getMainHandStack(), ModItem.GAME_ICON)){
            if(currentMode == 3){
                Random rand = new Random();
                NbtCompound nbtCompound = this.staff.getNbt();
                    if (nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE) != null) {
                        NbtList list = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
                        for (int i = 0; i < 10; i++) {
                            int m = this.entityList.getCompound(i).getInt("Absorb" + i);
                            if (m == 1 || m == 2 || m == 3 || m == 4 || m == 5 || m == 6 || m == 7) {
                                if (m == 1) {
                                    DarkZombieEntity entity = new DarkZombieEntity(ModEntities.DARK_ZOMBIE, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setOwner(user);
                                }
                                if (m == 2) {
                                    DarkSkeletonEntity entity = new DarkSkeletonEntity(ModEntities.DARK_SKELETON, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setStackInHand(entity.getActiveHand(), Items.BOW.getDefaultStack());
                                    entity.setOwner(user);
                                }
                                if (m == 3) {
                                    DarkCreeperEntity entity = new DarkCreeperEntity(ModEntities.DARK_CREEPER, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setOwner(user);
                                }
                                if (m == 4) {
                                    DarkSpiderEntity entity = new DarkSpiderEntity(ModEntities.DARK_SPIDER, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setOwner(user);
                                }
                                if (m == 5) {
                                    DarkEndermanEntity entity = new DarkEndermanEntity(ModEntities.DARK_ENDERMAN, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setOwner(user);
                                }
                                if (m == 6) {
                                    DarkPhantomEntity entity = new DarkPhantomEntity(ModEntities.DARK_PHANTOM, world);
                                    entity.setPos(user.getX() + rand.nextFloat(-3f, 3f), user.getY() + 1, user.getZ() + rand.nextFloat(-3f, 3f));
                                    world.spawnEntity(entity);
                                    entity.setOwner(user);
                                }
                                NbtCompound placeholder = new NbtCompound();
                                placeholder.putInt("Absorb" + i, 0);
                                list.set(i, placeholder);
                                remains = 10;
                                index = 0;
                            }
                            else {
                                user.sendMessage(Text.translatable("gui.avm_mod.fail_summon_empty"), true);
                            }
                    }
                }
            }
        }
        user.getItemCooldownManager().set(this, getUseDuration(user.getStackInHand(hand)));
        return TypedActionResult.fail(user.getMainHandStack());
    }
    public int getUseDuration(ItemStack itemStack){
        return 30;
    }
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(hasBlocks(stack, ModItem.GAME_ICON) && currentMode == 2){
            NbtCompound nbtCompound = this.staff.getOrCreateNbt();
            this.entityList = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
            if(remains > 0) {
                remains--;
                NbtList list = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
                absorpedEntities = new ArrayList();
                if (entity instanceof ZombieEntity) {
                    absorpedEntities.add(zombieIndex);
                    this.addMob(this.staff, zombieIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else if (entity instanceof SkeletonEntity) {
                    absorpedEntities.add(skeletonIndex);
                    this.addMob(this.staff, skeletonIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else if (entity instanceof CreeperEntity) {
                    absorpedEntities.add(creeperIndex);
                    this.addMob(this.staff, creeperIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else if (entity instanceof PhantomEntity) {
                    absorpedEntities.add(phantomIndex);
                    this.addMob(this.staff, phantomIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else if (entity instanceof SpiderEntity) {
                    absorpedEntities.add(spiderIndex);
                    this.addMob(this.staff, spiderIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else if (entity instanceof EndermanEntity) {
                    absorpedEntities.add(endermanIndex);
                    this.addMob(this.staff, endermanIndex, user);
                    entity.discard();
                    user.sendMessage(Text.translatable("gui.avm_mod.absorped").append(entity.getDisplayName()).append(Text.translatable("gui.avm_mod.remain").append(String.valueOf(remains))), true);
                } else {
                    remains++;
                    index--;
                    user.sendMessage(Text.translatable("gui.avm_mod.cant_absorp"), true);
                    return ActionResult.FAIL;
                }
            }
        }
        user.getItemCooldownManager().set(this, getUseDuration(user.getStackInHand(hand)));
        return ActionResult.FAIL;
    }

    private ParseResults<ServerCommandSource> parse(String command, PlayerEntity player) {
        MinecraftServer server = player.getServer();
        CommandDispatcher<ServerCommandSource> commandDispatcher = server.getCommandManager().getDispatcher();
        return commandDispatcher.parse(command, player.getCommandSource());
    }
    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            return Optional.empty();
        }
        PowerfulStaffItem.setBlocked(stack, false);
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return Optional.empty();
        }
        boolean i = false;
        NbtCompound nbtCompound2 = nbtList.getCompound(0);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        nbtList.remove(0);
        if (nbtList.isEmpty()) {
            stack.removeSubNbt(ITEMS_KEY);
        }
        return Optional.of(itemStack);
    }
    private Optional<ItemStack> createCommands(ItemStack stack){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtCompound placeholder = new NbtCompound();
        placeholder.putString("", "");
        NbtList list = nbtCompound.getList("Commands", NbtElement.COMPOUND_TYPE);
        NbtCompound nbtCompound2 = list.getCompound(0);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        for(int i = 0; i < 6; i++){
            String command = bufferedCommands.get(i);
            if(command != null){
                NbtCompound nbtCompound3 = new NbtCompound();
                nbtCompound3.putString("Command" + i, command);
                this.commandList.add(placeholder);
                this.commandList.set(i, nbtCompound3);
                if(this.commandList.size() > 6){
                    for(int j = 6; j < this.commandList.size(); j++) {
                        this.commandList.remove(j);
                    }
                }
            }
        }
        return Optional.of(itemStack);
    }
    private Optional<ItemStack> addMob(ItemStack stack, int mobIndex, PlayerEntity player){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList list = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
        NbtCompound nbtCompound2 = list.getCompound(0);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        if(list.size() <= 10 && index < 10 && index >= 0) {
            NbtCompound nbtCompound3 = new NbtCompound();
            nbtCompound3.putInt("Absorb" + index, mobIndex);
            list.set(index, nbtCompound3);
            this.entityList = list;
            index++;
        }
        else{
            player.sendMessage(Text.translatable("gui.avm_mod.limit"), true);
        }

        return Optional.of(itemStack);
    }
    private Optional<ItemStack> addPlaceholder(ItemStack stack){
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        NbtList list = nbtCompound.getList("Absorbed", NbtElement.COMPOUND_TYPE);
        NbtCompound nbtCompound2 = list.getCompound(0);
        ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
        for (int i = 0; i < 10; i++) {
            NbtCompound placeholder = new NbtCompound();
            placeholder.putInt("Absorb" + i, 0);
            list.add(placeholder);
            if(list.size() > 10){
                for(int j = 10; j < list.size(); j++){
                    list.remove(j);
                }
            }
            this.entityList = list;
        }
        return Optional.of(itemStack);
    }
    public void commands(String command1, String command2, String command3, String command4, String command5, String command6){
        bufferedCommands = new ArrayList<String>();
        bufferedCommands.add(0, command1);
        bufferedCommands.add(1, command2);
        bufferedCommands.add(2, command3);
        bufferedCommands.add(3, command4);
        bufferedCommands.add(4, command5);
        bufferedCommands.add(5, command6);
        this.createCommands(this.staff);
    }
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType != ClickType.RIGHT || !slot.canTakePartial(player)) {
            return false;
        }
        if (otherStack.isEmpty()) {
            this.removeFirstStack(stack).ifPresent(itemStack -> {
                this.playRemoveOneSound(player);
                cursorStackReference.set((ItemStack)itemStack);
            });
        } else {
            int i = this.addToBundle(stack, otherStack);
            if (i > 0) {
                this.playInsertSound(player);
                otherStack.decrement(i);
            }
        }
        return true;
    }
    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            return 0;
        }
        NbtCompound nbtCompound = bundle.getOrCreateNbt();
        if (!nbtCompound.contains(ITEMS_KEY)) {
            nbtCompound.put(ITEMS_KEY, new NbtList());
        }
        int i = getBundleOccupancy(bundle);
        int j = getItemOccupancy(stack);
        int k = Math.min(stack.getCount(), (1 - i) / j);
        if (k == 0) {
            return 0;
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        Optional<NbtCompound> optional = canMergeStack(stack, nbtList);
        if (optional.isPresent()) {
            NbtCompound nbtCompound2 = optional.get();
            ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
            itemStack.increment(k);
            itemStack.writeNbt(nbtCompound2);
            nbtList.remove(nbtCompound2);
            nbtList.add(0, nbtCompound2);
        } else {
            ItemStack itemStack2 = stack.copyWithCount(k);
            NbtCompound nbtCompound3 = new NbtCompound();
            itemStack2.writeNbt(nbtCompound3);
            nbtList.add(0, nbtCompound3);
        }
        return k;
    }
    private static int getItemOccupancy(ItemStack stack) {
        NbtCompound nbtCompound;
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + getBundleOccupancy(stack);
        }
        if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt() && (nbtCompound = BlockItem.getBlockEntityNbt(stack)) != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
            return 1;
        }
        return 64 / stack.getMaxCount();
    }
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        this.getBundledStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, this.getBundleOccupancy(stack)));
    }
    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        if (stack.isOf(Items.BUNDLE)) {
            return Optional.empty();
        }
        return items.stream().filter(NbtCompound.class::isInstance).map(NbtCompound.class::cast).filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack)).findFirst();
    }
    private static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack ->
                getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }
    public static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        }
        NbtList nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
    }
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8f, 0.8f + entity.getWorld().getRandom().nextFloat() * 0.4f);
    }
    private static List<ItemStack> getBlocks(ItemStack staff) {
        NbtList nbtList;
        ArrayList<ItemStack> list = Lists.newArrayList();
        NbtCompound nbtCompound = staff.getNbt();
        if (nbtCompound != null && nbtCompound.contains(ITEMS_KEY, NbtElement.LIST_TYPE) && (nbtList = nbtCompound.getList(ITEMS_KEY, NbtElement.COMPOUND_TYPE)) != null) {
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound2 = nbtList.getCompound(i);
                list.add(ItemStack.fromNbt(nbtCompound2));
            }
        }
        return list;
    }
    public static boolean hasBlocks(ItemStack staff, Item block) {
        return PowerfulStaffItem.getBlocks(staff).stream().anyMatch(s -> s.isOf(block));
    }
    public static boolean isBlocked(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean(BLOCKED_KEY);
    }
    public static void setBlocked(ItemStack stack, boolean blocked) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean(BLOCKED_KEY, blocked);
        if(nbtCompound.getBoolean(BLOCKED_KEY) == false){
            nbtCompound.remove(BLOCKED_KEY);
        }
    }
    public static int muliplicate(ItemStack stack){
        if(PowerfulStaffItem.hasBlocks(stack, Items.COPPER_BLOCK)){
            return 1;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.IRON_BLOCK)){
            return 2;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.GOLD_BLOCK)){
            return 3;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.EMERALD_BLOCK)){
            return 4;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.DIAMOND_BLOCK)){
            return 5;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.NETHERITE_BLOCK)){
            return 7;
        }
        if(PowerfulStaffItem.hasBlocks(stack, Items.COMMAND_BLOCK)){
            return 11;
        }
        if(PowerfulStaffItem.hasBlocks(stack, ModItem.GAME_ICON)){
            return 13;
        }
        else{
            return 0;
        }
    }
}
