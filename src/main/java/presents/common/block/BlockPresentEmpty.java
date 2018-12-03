package presents.common.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPresentEmpty extends BlockColored {

    protected AxisAlignedBB boundingBox;

    public BlockPresentEmpty(String name) {
        super(Material.CLOTH);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setHardness(0.3F);
        setResistance(0.5F);
        setSoundType(SoundType.CLOTH);
        boundingBox = new AxisAlignedBB(3/16D, 0/16D, 3/16D, 13/16D, 10/16D, 13/16D);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boundingBox;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }
}
