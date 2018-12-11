package presents.common.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import presents.common.tileentity.TileEntityPresent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPresent extends BlockPresentEmpty implements ITileEntityProvider {

    public BlockPresent(String name) {
        super(name);
        hasTileEntity = true;
        boundingBox = new AxisAlignedBB(2.5/16D, 0/16D, 2.5/16D, 13.5/16D, 11/16D, 13.5/16D);
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
