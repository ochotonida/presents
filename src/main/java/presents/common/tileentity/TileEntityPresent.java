package presents.common.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import presents.Presents;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPresent extends TileEntity implements IInventory {

    protected ResourceLocation lootTable;
    protected long lootTableSeed;

    protected NonNullList<ItemStack> inventory;

    protected NBTTagCompound fireworks;

    public TileEntityPresent() {
        inventory = NonNullList.withSize(8, ItemStack.EMPTY);
    }

    public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
        this.lootTable = lootTable;
        this.lootTableSeed = lootTableSeed;
        markDirty();
    }

    public void fillWithLoot(@Nullable EntityPlayer player) {
        if (this.lootTable != null) {
            LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(this.lootTable);
            this.lootTable = null;
            Random random;

            if (this.lootTableSeed == 0L) {
                random = new Random();
            } else {
                random = new Random(this.lootTableSeed);
            }

            LootContext.Builder builder = new LootContext.Builder((WorldServer)this.world);

            if (player != null) {
                builder.withLuck(player.getLuck()).withPlayer(player);
            }

            loottable.fillInventory(this, random, builder.build());
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean makeFireworks(World world, BlockPos pos) {
        if (fireworks == null) {
            if (lootTable != null && lootTable.equals(Presents.LOOTTABLE_PRESENT_SPECIAL)) {
                fireworks = new NBTTagCompound();
                Random random = lootTableSeed == 0 ? new Random() : new Random(lootTableSeed);
                fireworks.setBoolean("Flicker", random.nextInt(3) == 0);
                fireworks.setBoolean("Trail", random.nextInt(3) == 0);
                fireworks.setByte("Type", (byte) random.nextInt(5));
                fireworks.setIntArray("Colors", new int[]{ItemDye.DYE_COLORS[random.nextInt(15)], ItemDye.DYE_COLORS[random.nextInt(15)]});
                fireworks.setIntArray("FadeColors", new int[]{ItemDye.DYE_COLORS[random.nextInt(15)], ItemDye.DYE_COLORS[random.nextInt(15)]});
            } else {
                return false;
            }
        }
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        tagList.appendTag(fireworks);
        compound.setTag("Explosions", tagList);
        world.makeFireworks(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.3, 0, compound);
        return true;
    }

    public void spawnItems(World world, BlockPos pos, @Nullable EntityPlayer player) {
        fillWithLoot(player);
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                IBehaviorDispenseItem dispenseBehavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(stack.getItem());
                if (dispenseBehavior instanceof BehaviorProjectileDispense
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Item.getItemFromBlock(Blocks.TNT))
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.SPLASH_POTION)
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.LINGERING_POTION)
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.SPAWN_EGG)
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.FIREWORKS)
                        || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.FIRE_CHARGE)) {
                    VirtualDispenser dispenser = new VirtualDispenser(pos, world, stack);
                    dispenseBehavior.dispense(dispenser, stack);
                } else {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, inventory);

        if (fireworks != null) {
            compound.setTag("Explosion", fireworks);
        }

        if (lootTable != null) {
            compound.setString("LootTable", lootTable.toString());
        }

        if (lootTableSeed != 0L) {
            compound.setLong("LootTableSeed", lootTableSeed);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        inventory = NonNullList.withSize(8, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, inventory);

        if (compound.hasKey("Explosion")) {
            fireworks = compound.getCompoundTag("Explosion");
        }

        if (compound.hasKey("LootTable")) {
            lootTable = new ResourceLocation(compound.getString("LootTable"));
            lootTableSeed = compound.getLong("LootTableSeed");
        }
    }

    private void notifyBlockUpdate() {
        final IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 3);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        notifyBlockUpdate();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = super.getUpdateTag();

        if (fireworks != null) {
            updateTag.setTag("Explosion", fireworks);
        }

        if (lootTable != null) {
            updateTag.setString("LootTable", lootTable.toString());
        }

        if (lootTableSeed != 0L) {
            updateTag.setLong("LootTableSeed", lootTableSeed);
        }

        return updateTag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound updateTag) {
        super.handleUpdateTag(updateTag);
        if (updateTag.hasKey("Explosion")) {
            fireworks = updateTag.getCompoundTag("Explosion");
        }
        if (updateTag.hasKey("LootTable")) {
            lootTable = new ResourceLocation(updateTag.getString("LootTable"));
            lootTableSeed = updateTag.getLong("LootTableSeed");
        }
    }

    @Override
    public int getSizeInventory() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.get(index).splitStack(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return inventory.remove(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.set(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) { return false; }

    @Override
    public void openInventory(EntityPlayer player) { }

    @Override
    public void closeInventory(EntityPlayer player) { }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) { return false; }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) { }

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() { }

    @Override
    public String getName() {
        return "present";
    }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    private class VirtualDispenser implements IBlockSource {

        private final BlockPos pos;

        private final World world;

        private final TileEntityDispenser dispenser;

        public VirtualDispenser(BlockPos pos, World world, ItemStack stack) {
            this.pos = pos;
            this.world = world;
            this.dispenser = new TileEntityDispenser();
            dispenser.addItemStack(stack);
        }

        @Override
        public double getX() {
            return pos.getX() + 0.5;
        }

        @Override
        public double getY() {
            return pos.getY() + 0.5;
        }

        @Override
        public double getZ() {
            return pos.getZ() + 0.5;
        }

        @Override
        public BlockPos getBlockPos() {
            return pos;
        }

        @Override
        public IBlockState getBlockState() {
            return Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.UP);
        }

        @Override
        public <T extends TileEntity> T getBlockTileEntity() {
            return (T) dispenser;
        }

        @Override
        public World getWorld() {
            return world;
        }
    }
}
