package presents.common.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPresentEmpty extends BlockColored {

    public BlockPresentEmpty(String name) {
        super(Material.CLOTH);
        setUnlocalizedName(name);
        setRegistryName(name);
        setHardness(0.3F);
        setResistance(0.5F);
        setSoundType(SoundType.CLOTH);
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
