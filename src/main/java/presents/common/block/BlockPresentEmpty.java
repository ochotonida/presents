package presents.common.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import presents.common.tileentity.TileEntityPresent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.block.state.BlockFaceShape.CENTER_BIG;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPresentEmpty extends Block implements ITileEntityProvider {

    @SuppressWarnings("WeakerAccess")
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

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityPresent();
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return boundingBox;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (face.getAxis() != EnumFacing.Axis.Y) {
            return BlockFaceShape.MIDDLE_POLE_THICK;
        }
        return face == EnumFacing.UP ? BlockFaceShape.UNDEFINED : CENTER_BIG;
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
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
}
