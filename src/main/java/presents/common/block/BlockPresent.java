package presents.common.block;

import com.google.common.collect.Lists;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Items;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import presents.common.tileentity.TileEntityPresent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPresent extends BlockPresentEmpty implements ITileEntityProvider {

    public BlockPresent(String name) {
        super(name);
        hasTileEntity = true;
        boundingBox = new AxisAlignedBB(2.5/16D, 0/16D, 2.5/16D, 13.5/16D, 11/16D, 13.5/16D);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
        NBTTagCompound tagCompound = stack.getTagCompound();

        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag")) {
            NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");

            if (blockEntityTag.hasKey("LootTable") || !blockEntityTag.getString("PlayerName").equals(Minecraft.getMinecraft().player.getDisplayNameString())) {
                tooltip.add("???????");
            } else {

                if (blockEntityTag.hasKey("Items")) {
                    NonNullList<ItemStack> inventory = NonNullList.withSize(8, ItemStack.EMPTY);
                    ItemStackHelper.loadAllItems(blockEntityTag, inventory);

                    tooltip.add(I18n.translateToLocal("presents:container.present.contents"));

                    for (ItemStack inventoryStack : inventory) {
                        if (!inventoryStack.isEmpty()) {
                            tooltip.add("  " + inventoryStack.getDisplayName());
                        }
                    }
                }

                if (blockEntityTag.hasKey("Explosion")) {
                    tooltip.add(I18n.translateToLocal("presents:container.present.explosion"));

                    List<String> list = Lists.newArrayList();
                    ItemFireworkCharge.addExplosionInfo(blockEntityTag.getCompoundTag("Explosion"), list);

                    if (!list.isEmpty()) {
                        for (int j = 0; j < list.size(); ++j) {
                            list.set(j, "  " + list.get(j));
                        }
                        tooltip.addAll(list);
                    }
                }

                tooltip.add(TextFormatting.ITALIC + I18n.translateToLocal("presents:container.present.ownerinfo"));

            }
        }

        super.addInformation(stack, world, tooltip, tooltipFlag);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPresent();
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        return super.getExpDrop(state, world, pos, fortune);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.AIR;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityPresent) {
            ((TileEntityPresent) tileEntity).spawnItems(null);
        }
        super.breakBlock(world, pos, state);
    }

    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, net.minecraft.client.particle.ParticleManager manager) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityPresent) {
            return ((TileEntityPresent) tileEntity).makeFireworks();
        }
        return false;
    }
}
