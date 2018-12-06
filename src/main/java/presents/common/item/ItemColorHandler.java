package presents.common.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemColorHandler implements IItemColor {

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag")) {
            compound = compound.getCompoundTag("BlockEntityTag");
            if (tintIndex == 1) {
                if (compound.hasKey("RibbonColor")) {
                    return compound.getInteger("RibbonColor");
                }
            } else {
                if (compound.hasKey("Color")) {
                    return compound.getInteger("Color");
                }
            }
        }
        return EnumDyeColor.WHITE.getColorValue();
    }
}
