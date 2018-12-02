package presents.common.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("WeakerAccess")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntityPresent extends TileEntity {

    protected NonNullList<ItemStack> inventory;

    @Nullable
    protected NBTTagCompound fireworks;

    public TileEntityPresent() {
        inventory = NonNullList.withSize(8, ItemStack.EMPTY);
    }

    @SideOnly(Side.CLIENT)
    public boolean makeFireworks(World world, BlockPos pos) {
        if (fireworks == null) {
            return false;
        }
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList tagList = new NBTTagList();
        tagList.appendTag(fireworks);
        compound.setTag("Explosions", tagList);
        world.makeFireworks(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 2, 0, compound);
        return true;
    }

    public void spawnItems(World world, BlockPos pos) {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        ItemStackHelper.saveAllItems(compound, inventory);

        if (fireworks != null) {
            compound.setTag("Explosion", fireworks);
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

        return updateTag;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound updateTag)
    {
        super.handleUpdateTag(updateTag);
        if (updateTag.hasKey("Explosion")) {
            fireworks = updateTag.getCompoundTag("Explosion");
        }
    }

}
