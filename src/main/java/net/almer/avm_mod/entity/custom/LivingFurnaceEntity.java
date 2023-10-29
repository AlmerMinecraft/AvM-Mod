package net.almer.avm_mod.entity.custom;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.almer.avm_mod.entity.ModEntities;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LivingFurnaceEntity extends TameableEntity implements VehicleInventory {
    List playerList;
    double distance = 5;
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;
    protected SimpleInventory items;
    protected static final int INPUT_SLOT_INDEX = 0;
    protected static final int FUEL_SLOT_INDEX = 1;
    protected static final int OUTPUT_SLOT_INDEX = 2;
    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int COOK_TIME_PROPERTY_INDEX = 2;
    public static final int COOK_TIME_TOTAL_PROPERTY_INDEX = 3;
    public static final int PROPERTY_COUNT = 4;
    public static final int DEFAULT_COOK_TIME = 200;
    public static final int field_31295 = 2;
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    int burnTime;
    int fuelTime;
    int cookTime;
    int cookTimeTotal;
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return LivingFurnaceEntity.this.burnTime;
                }
                case 1: {
                    return LivingFurnaceEntity.this.fuelTime;
                }
                case 2: {
                    return LivingFurnaceEntity.this.cookTime;
                }
                case 3: {
                    return LivingFurnaceEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    LivingFurnaceEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    LivingFurnaceEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    LivingFurnaceEntity.this.cookTime = value;
                    break;
                }
                case 3: {
                    LivingFurnaceEntity.this.cookTimeTotal = value;
                    break;
                }
            }
        }
        @Override
        public int size() {
            return 4;
        }
    };
    public LivingFurnaceEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.SMELTING);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 0.7, 2, 40, true));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.5));
        this.goalSelector.add(4, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5);
    }

    @Override
    public EntityView method_48926() {
        return this.getWorld();
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.LIVING_FURNACE.create(world);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.isSneaking()) {
            if (this.isTamed()) {
                if (this.isOwner(player)) {
                    if (this.isSitting()) {
                        setSitting(false);
                        return ActionResult.SUCCESS;
                    } else {
                        setSitting(true);
                        this.jumping = false;
                        this.navigation.stop();
                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                setOwner(player);
                return ActionResult.SUCCESS;
            }
        }
        else{
            ActionResult actionResult = this.open(player);
            if (actionResult.isAccepted()) {
                this.emitGameEvent(GameEvent.ENTITY_INTERACT, player);
            }
            return actionResult;
        }
        return super.interactMob(player, hand);
    }

    @Nullable
    @Override
    public Identifier getLootTableId() {
        return this.lootTableId;
    }

    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootSeed = lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 4;
    }
    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        LivingFurnaceEntity.addFuel(map, Items.LAVA_BUCKET, 20000);
        LivingFurnaceEntity.addFuel(map, Blocks.COAL_BLOCK, 16000);
        LivingFurnaceEntity.addFuel(map, Items.BLAZE_ROD, 2400);
        LivingFurnaceEntity.addFuel(map, Items.COAL, 1600);
        LivingFurnaceEntity.addFuel(map, Items.CHARCOAL, 1600);
        LivingFurnaceEntity.addFuel(map, ItemTags.LOGS, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.BAMBOO_BLOCKS, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.PLANKS, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.BAMBOO_MOSAIC, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_STAIRS, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_SLABS, 150);
        LivingFurnaceEntity.addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_FENCES, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.FENCE_GATES, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.NOTE_BLOCK, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.BOOKSHELF, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.LECTERN, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.JUKEBOX, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.CHEST, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.TRAPPED_CHEST, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.CRAFTING_TABLE, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.BANNERS, 300);
        LivingFurnaceEntity.addFuel(map, Items.BOW, 300);
        LivingFurnaceEntity.addFuel(map, Items.FISHING_ROD, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.LADDER, 300);
        LivingFurnaceEntity.addFuel(map, ItemTags.SIGNS, 200);
        LivingFurnaceEntity.addFuel(map, ItemTags.HANGING_SIGNS, 800);
        LivingFurnaceEntity.addFuel(map, Items.WOODEN_SHOVEL, 200);
        LivingFurnaceEntity.addFuel(map, Items.WOODEN_SWORD, 200);
        LivingFurnaceEntity.addFuel(map, Items.WOODEN_HOE, 200);
        LivingFurnaceEntity.addFuel(map, Items.WOODEN_AXE, 200);
        LivingFurnaceEntity.addFuel(map, Items.WOODEN_PICKAXE, 200);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_DOORS, 200);
        LivingFurnaceEntity.addFuel(map, ItemTags.BOATS, 1200);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOOL, 100);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        LivingFurnaceEntity.addFuel(map, Items.STICK, 100);
        LivingFurnaceEntity.addFuel(map, ItemTags.SAPLINGS, 100);
        LivingFurnaceEntity.addFuel(map, Items.BOWL, 100);
        LivingFurnaceEntity.addFuel(map, ItemTags.WOOL_CARPETS, 67);
        LivingFurnaceEntity.addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        LivingFurnaceEntity.addFuel(map, Items.CROSSBOW, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.BAMBOO, 50);
        LivingFurnaceEntity.addFuel(map, Blocks.DEAD_BUSH, 100);
        LivingFurnaceEntity.addFuel(map, Blocks.SCAFFOLDING, 50);
        LivingFurnaceEntity.addFuel(map, Blocks.LOOM, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.BARREL, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.FLETCHING_TABLE, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.SMITHING_TABLE, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.COMPOSTER, 300);
        LivingFurnaceEntity.addFuel(map, Blocks.AZALEA, 100);
        LivingFurnaceEntity.addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        LivingFurnaceEntity.addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (LivingFurnaceEntity.isNonFlammableWood(registryEntry.value())) continue;
            fuelTimes.put(registryEntry.value(), fuelTime);
        }
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (LivingFurnaceEntity.isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        fuelTimes.put(item2, fuelTime);
    }
    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 0 && slot < this.inventory.size()) {
            return this.inventory.get(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void markDirty() {
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (this.lootTableId == null || !player.isSpectator()) {
            this.generateInventoryLoot(playerInventory.player);
            return createScreenHandler(syncId, playerInventory);
        }
        return null;
    }

    @Override
    public void clear() {
        this.clearInventory();
    }
    @Override
    public void onClose(PlayerEntity player) {
        this.getWorld().emitGameEvent(GameEvent.CONTAINER_CLOSE, this.getPos(), GameEvent.Emitter.of(player));
    }
    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory == null) {
            return;
        }
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.get(i);
            if (itemStack.isEmpty() || EnchantmentHelper.hasVanishingCurse(itemStack)) continue;
            this.dropStack(itemStack);
        }
        if (!this.getWorld().isClient) {
            this.dropItem(Blocks.FURNACE);
        }
    }

    @Override
    public void tick() {
        World world = this.getWorld();
        boolean bl4;
        boolean bl = this.isBurning();
        boolean bl2 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        ItemStack itemStack = this.inventory.get(1);
        boolean bl3 = !this.inventory.get(0).isEmpty();
        boolean bl5 = bl4 = !itemStack.isEmpty();
        if (this.isBurning() || bl4 && bl3) {
            Recipe recipe = bl3 ? (Recipe)this.matchGetter.getFirstMatch(this, world).orElse(null) : null;
            int i = this.getMaxCountPerStack();
            if (!this.isBurning() && LivingFurnaceEntity.canAcceptRecipeOutput(world.getRegistryManager(), recipe, this.inventory, i)) {
                this.fuelTime = this.burnTime = this.getFuelTime(itemStack);
                if (this.isBurning()) {
                    bl2 = true;
                    if (bl4) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            this.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }
            if (this.isBurning() && LivingFurnaceEntity.canAcceptRecipeOutput(world.getRegistryManager(), recipe, this.inventory, i)) {
                ++this.cookTime;
                if (this.cookTime == this.cookTimeTotal) {
                    this.cookTime = 0;
                    this.cookTimeTotal = LivingFurnaceEntity.getCookTime(world, this);
                    if (LivingFurnaceEntity.craftRecipe(world.getRegistryManager(), recipe, this.inventory, i)) {
                        this.setLastRecipe(recipe);
                    }
                    bl2 = true;
                }
            } else {
                this.cookTime = 0;
            }
        } else if (!this.isBurning() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
        }
        super.tick();
    }
    private static boolean canAcceptRecipeOutput(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (slots.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput(registryManager);
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(2);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!ItemStack.areItemsEqual(itemStack2, itemStack)) {
            return false;
        }
        if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
            return true;
        }
        return itemStack2.getCount() < itemStack.getMaxCount();
    }
    private static boolean craftRecipe(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null || !LivingFurnaceEntity.canAcceptRecipeOutput(registryManager, recipe, slots, count)) {
            return false;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack2 = recipe.getOutput(registryManager);
        ItemStack itemStack3 = slots.get(2);
        if (itemStack3.isEmpty()) {
            slots.set(2, itemStack2.copy());
        } else if (itemStack3.isOf(itemStack2.getItem())) {
            itemStack3.increment(1);
        }
        if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !slots.get(1).isEmpty() && slots.get(1).isOf(Items.BUCKET)) {
            slots.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        itemStack.decrement(1);
        return true;
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    private static int getCookTime(World world, LivingFurnaceEntity furnace) {
        return furnace.matchGetter.getFirstMatch(furnace, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }
    }
    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        List<Recipe<?>> list = this.getRecipesUsedAndDropExperience(player.getServerWorld(), player.getPos());
        player.unlockRecipes(list);
        for (Recipe<?> recipe : list) {
            if (recipe == null) continue;
            player.onRecipeCrafted(recipe, this.inventory);
        }
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
            world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
                list.add((Recipe<?>)recipe);
                LivingFurnaceEntity.dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }
        return list;
    }

    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        int i = MathHelper.floor((float)multiplier * experience);
        float f = MathHelper.fractionalPart((float)multiplier * experience);
        if (f != 0.0f && Math.random() < (double)f) {
            ++i;
        }
        ExperienceOrbEntity.spawn(world, pos, i);
    }
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new FurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && ItemStack.canCombine(itemStack, stack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.cookTimeTotal = LivingFurnaceEntity.getCookTime(this.getWorld(), this);
            this.cookTime = 0;
            this.markDirty();
        }
    }
}
