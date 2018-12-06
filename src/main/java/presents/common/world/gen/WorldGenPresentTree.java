package presents.common.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import presents.Presents;
import presents.common.tileentity.TileEntityPresent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
public class WorldGenPresentTree extends WorldGenTaiga2 {

    public WorldGenPresentTree(boolean notify) {
        super(notify);
    }

    public boolean generate(World world, Random random, BlockPos pos) {
        if (super.generate(world, random, pos)) {
            for (int i = random.nextInt(7) + 1; i > 0; i--) {
                BlockPos airPos = pos.add(random.nextInt(7) - 3, 0, random.nextInt(7) - 3);

                if (world.isAirBlock(airPos)) {
                    while (world.isAirBlock(airPos.down())) {
                        airPos = airPos.down();
                    }
                } else {
                    while (!world.isAirBlock(airPos)) {
                        airPos = airPos.up();
                    }
                }
                if (airPos.getY() < 0 || airPos.getY() > 255) {
                    return true;
                }

                IBlockState state = world.getBlockState(airPos.down());

                if (state.isSideSolid(world, airPos.down(), EnumFacing.UP) && !state.getBlock().isLeaves(state, world, airPos.down())) {
                    world.setBlockState(airPos, Presents.PRESENT_BLOCK.getDefaultState(), 2);

                    TileEntity tileEntity = world.getTileEntity(airPos);

                    if (tileEntity instanceof TileEntityPresent) {
                        ((TileEntityPresent) tileEntity).setLootTable(random.nextInt(12) == 0 ? Presents.LOOTTABLE_PRESENT_SPECIAL : Presents.LOOTTABLE_PRESENT_REGULAR, random.nextLong());
                        ((TileEntityPresent) tileEntity).setColor(random.nextInt(0xFFFFFF));
                        ((TileEntityPresent) tileEntity).setRibbonColor(random.nextInt(0xFFFFFF));
                    }
                }
            }
        }
        return true;
    }
}
