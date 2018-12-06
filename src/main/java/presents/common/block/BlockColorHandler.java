package presents.common.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import presents.common.tileentity.TileEntityPresent;
import presents.common.tileentity.TileEntityPresentEmpty;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockColorHandler implements IBlockColor {

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
        if (pos != null && worldIn != null) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tintIndex == 1) {
                if (tileEntity instanceof TileEntityPresent) {
                    return ((TileEntityPresent) tileEntity).getRibbonColor();
                }
            }
            if (tileEntity instanceof TileEntityPresentEmpty) {
                return ((TileEntityPresentEmpty) tileEntity).getColor();
            }
        }
        return EnumDyeColor.WHITE.getColorValue();
    }
}
