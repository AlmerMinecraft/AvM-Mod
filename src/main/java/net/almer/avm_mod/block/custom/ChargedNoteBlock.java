package net.almer.avm_mod.block.custom;

import net.almer.avm_mod.block.ModBlock;
import net.almer.avm_mod.item.ModItem;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Predicate;

public class ChargedNoteBlock extends Block {
    public ChargedNoteBlock(Settings settings) {
        super(settings);
    }
    public static final EnumProperty<Instrument> INSTRUMENT;
    public static final BooleanProperty POWERED;
    public static final IntProperty NOTE;
    public static final int field_41678 = 3;
    @Nullable
    private BlockPattern guitarDispenserPattern;
    @Nullable
    private BlockPattern guitarPattern;
    private static final Predicate<BlockState> IS_GOLEM_HEAD_PREDICATE;

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.trySpawnEntity(world, pos);
        }
    }
    private void trySpawnEntity(World world, BlockPos pos) {
        BlockPattern.Result result = this.getGuitarPattern().searchAround(world, pos);
        if (result != null) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ModItem.GUITAR.getDefaultStack());
            if (itemEntity != null) {
                spawnEntity(world, result, itemEntity, result.translate(0, 2, 0).getBlockPos());
            }
        }
    }
    private BlockPattern getGuitarDispenserPattern() {
        if (this.guitarDispenserPattern == null) {
            this.guitarDispenserPattern = BlockPatternBuilder.start().aisle(new String[]{" ##", "%~~"}).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.BRICK_WALL))).where('%', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIME_WOOL))).where('~', (pos) -> {
                return pos.getBlockState().isAir();
            }).build();
        }

        return this.guitarDispenserPattern;
    }

    private BlockPattern getGuitarPattern() {
        if (this.guitarPattern == null) {
            this.guitarPattern = BlockPatternBuilder.start().aisle(new String[]{"^##", "%~~"}).where('^', CachedBlockPosition.matchesBlockState(IS_GOLEM_HEAD_PREDICATE)).where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.BRICK_WALL))).where('%', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.LIME_WOOL))).where('~', (pos) -> {
                return pos.getBlockState().isAir();
            }).build();
        }

        return this.guitarPattern;
    }
    private static void spawnEntity(World world, BlockPattern.Result patternResult, Entity entity, BlockPos pos) {
        breakPatternBlocks(world, patternResult);
        entity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY() + 0.05, (double)pos.getZ() + 0.5, 0.0F, 0.0F);
        world.spawnEntity(entity);
        Iterator var4 = world.getNonSpectatingEntities(ServerPlayerEntity.class, entity.getBoundingBox().expand(5.0)).iterator();

        while(var4.hasNext()) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var4.next();
            Criteria.SUMMONED_ENTITY.trigger(serverPlayerEntity, entity);
        }

        updatePatternBlocks(world, patternResult);
    }
    public static void breakPatternBlocks(World world, BlockPattern.Result patternResult) {
        for(int i = 0; i < patternResult.getWidth(); ++i) {
            for(int j = 0; j < patternResult.getHeight(); ++j) {
                CachedBlockPosition cachedBlockPosition = patternResult.translate(i, j, 0);
                world.setBlockState(cachedBlockPosition.getBlockPos(), Blocks.AIR.getDefaultState(), 2);
                world.syncWorldEvent(2001, cachedBlockPosition.getBlockPos(), Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
            }
        }

    }
    public static void updatePatternBlocks(World world, BlockPattern.Result patternResult) {
        for(int i = 0; i < patternResult.getWidth(); ++i) {
            for(int j = 0; j < patternResult.getHeight(); ++j) {
                CachedBlockPosition cachedBlockPosition = patternResult.translate(i, j, 0);
                world.updateNeighbors(cachedBlockPosition.getBlockPos(), Blocks.AIR);
            }
        }

    }

    private BlockState getStateWithInstrument(WorldAccess world, BlockPos pos, BlockState state) {
        Instrument instrument = world.getBlockState(pos.up()).getInstrument();
        if (instrument.isNotBaseBlock()) {
            return (BlockState)state.with(INSTRUMENT, instrument);
        } else {
            Instrument instrument2 = world.getBlockState(pos.down()).getInstrument();
            Instrument instrument3 = instrument2.isNotBaseBlock() ? Instrument.HARP : instrument2;
            return (BlockState)state.with(INSTRUMENT, instrument3);
        }
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getStateWithInstrument(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState());
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        boolean bl = direction.getAxis() == Direction.Axis.Y;
        return bl ? this.getStateWithInstrument(world, pos, state) : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != (Boolean)state.get(POWERED)) {
            if (bl) {
                this.playNote((Entity)null, state, world, pos);
            }

            world.setBlockState(pos, (BlockState)state.with(POWERED, bl), 3);
        }

    }

    private void playNote(@Nullable Entity entity, BlockState state, World world, BlockPos pos) {
        if (((Instrument)state.get(INSTRUMENT)).isNotBaseBlock() || world.getBlockState(pos.up()).isAir()) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.emitGameEvent(entity, GameEvent.NOTE_BLOCK_PLAY, pos);
        }

    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isIn(ItemTags.NOTEBLOCK_TOP_INSTRUMENTS) && hit.getSide() == Direction.UP) {
            return ActionResult.PASS;
        } else if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            state = (BlockState)state.cycle(NOTE);
            world.setBlockState(pos, state, 3);
            this.playNote(player, state, world, pos);
            player.incrementStat(Stats.TUNE_NOTEBLOCK);
            return ActionResult.CONSUME;
        }
    }

    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (!world.isClient) {
            this.playNote(player, state, world, pos);
            player.incrementStat(Stats.PLAY_NOTEBLOCK);
        }
    }

    public static float getNotePitch(int note) {
        return (float)Math.pow(2.0, (double)(note - 12) / 12.0);
    }

    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        Instrument instrument = (Instrument)state.get(INSTRUMENT);
        float f;
        if (instrument.shouldSpawnNoteParticles()) {
            int i = (Integer)state.get(NOTE);
            f = getNotePitch(i);
            world.addParticle(ParticleTypes.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)i / 24.0, 0.0, 0.0);
        } else {
            f = 1.0F;
        }

        RegistryEntry registryEntry;
        if (instrument.hasCustomSound()) {
            Identifier identifier = this.getCustomSound(world, pos);
            if (identifier == null) {
                return false;
            }

            registryEntry = RegistryEntry.of(SoundEvent.of(identifier));
        } else {
            registryEntry = instrument.getSound();
        }

        world.playSound((PlayerEntity)null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, registryEntry, SoundCategory.RECORDS, 3.0F, f, world.random.nextLong());
        return true;
    }

    @Nullable
    private Identifier getCustomSound(World world, BlockPos pos) {
        BlockEntity var4 = world.getBlockEntity(pos.up());
        if (var4 instanceof SkullBlockEntity skullBlockEntity) {
            return skullBlockEntity.getNoteBlockSound();
        } else {
            return null;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{INSTRUMENT, POWERED, NOTE});
    }

    static {
        INSTRUMENT = Properties.INSTRUMENT;
        POWERED = Properties.POWERED;
        NOTE = Properties.NOTE;
        IS_GOLEM_HEAD_PREDICATE = (state) -> {
            return state != null && (state.isOf(ModBlock.CHARGED_NOTE_BLOCK));
        };
    }
}
