package presents.common.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
import presents.common.entity.EntityPresentPrimed;
import presents.common.misc.PotionHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPresent extends TileEntityPresentEmpty implements IInventory {

    private ResourceLocation lootTable;

    private String playerName;

    private long lootTableSeed;

    private NonNullList<ItemStack> inventory;

    private NBTTagCompound fireworks;

    private int ribbonColor = 16383998;

    public void setRibbonColor(int color) {
        ribbonColor = color;
        markDirty();
    }

    public TileEntityPresent() {
        inventory = NonNullList.withSize(8, ItemStack.EMPTY);
    }

    public int getRibbonColor() {
        return ribbonColor;
    }

    public void setLootTable(ResourceLocation lootTable, long lootTableSeed) {
        this.lootTable = lootTable;
        this.lootTableSeed = lootTableSeed;
        markDirty();
    }

    private void fillWithLoot(@Nullable EntityPlayer player) {
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
    public boolean makeFireworks() {
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

    public void spawnItems(@Nullable EntityPlayer player) {
        fillWithLoot(player);
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                if (!dispenseItem(stack) && stack.getItem() == Item.getItemFromBlock(Blocks.TNT)) {
                    EntityTNTPrimed presentPrimed = new EntityPresentPrimed(world, (double) ((float) pos.getX() + 0.5F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.5F), player, getColor(), getRibbonColor());
                    world.spawnEntity(presentPrimed);
                    world.playSound(null, presentPrimed.posX, presentPrimed.posY, presentPrimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else if (stack.getItem() instanceof ItemLingeringPotion || stack.getItem() instanceof ItemSplashPotion) {
                    PotionHelper.spawnPotion(stack, world, pos);
                } else {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
        }
    }

    private boolean dispenseItem(ItemStack stack) {
        IBehaviorDispenseItem dispenseBehavior = BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(stack.getItem());
        if (dispenseBehavior instanceof BehaviorProjectileDispense
                || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.SPAWN_EGG)
                || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.FIREWORKS)
                || dispenseBehavior == BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getObject(Items.FIRE_CHARGE)) {
            VirtualDispenser dispenser = new VirtualDispenser(pos, world, stack);
            dispenseBehavior.dispense(dispenser, stack);
            return true;
        }
        return false;
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

        compound.setInteger("RibbonColor", ribbonColor);

        if (playerName != null) {
            compound.setString("PlayerName", playerName);
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

        if (compound.hasKey("RibbonColor")) {
            ribbonColor = compound.getInteger("RibbonColor");
        }

        if (compound.hasKey("PlayerName")) {
            playerName = compound.getString("PlayerName");
        }
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

        updateTag.setInteger("RibbonColor", ribbonColor);

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
        if (updateTag.hasKey("RibbonColor")) {
            ribbonColor = updateTag.getInteger("RibbonColor");
            getWorld().markBlockRangeForRenderUpdate(pos, pos);
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

        VirtualDispenser(BlockPos pos, World world, ItemStack stack) {
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
            //noinspection unchecked
            return (T) dispenser;
        }

        @Override
        public World getWorld() {
            return world;
        }
    }
}
