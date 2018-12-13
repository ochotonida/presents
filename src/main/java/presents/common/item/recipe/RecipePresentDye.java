package presents.common.item.recipe;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import presents.common.block.BlockPresent;
import presents.common.block.BlockPresentEmpty;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RecipePresentDye extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public RecipePresentDye() {
        setRegistryName("recipe_present_dye");
    }

    public boolean matches(InventoryCrafting inv, World worldIn) {
        ItemStack presentStack = ItemStack.EMPTY;
        List<ItemStack> list = new ArrayList<>();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);

            if (!stack.isEmpty()) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
                    presentStack = stack;
                } else {
                    if (!net.minecraftforge.oredict.DyeUtils.isDye(stack)) {
                        return false;
                    }
                    list.add(stack);
                }
            }
        }

        return !presentStack.isEmpty() && !list.isEmpty();
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        // vanilla spaghetti
        ItemStack presentStack = ItemStack.EMPTY;
        int[] colorValues = new int[3];
        int largestValueSum = 0;
        int dyeAmount = 0;

        for (int k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack stack = inv.getStackInSlot(k);

            if (!stack.isEmpty()) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
                    presentStack = stack.copy();
                    presentStack.setCount(1);

                    int colour = getColor(presentStack);
                    if (colour != 16383998) {
                        float red = (float)(colour >> 16 & 255) / 255.0F;
                        float green = (float)(colour >> 8 & 255) / 255.0F;
                        float blue = (float)(colour & 255) / 255.0F;
                        largestValueSum = (int)((float)largestValueSum + Math.max(red, Math.max(green, blue)) * 255.0F);
                        colorValues[0] = (int)((float)colorValues[0] + red * 255.0F);
                        colorValues[1] = (int)((float)colorValues[1] + green * 255.0F);
                        colorValues[2] = (int)((float)colorValues[2] + blue * 255.0F);
                        dyeAmount++;
                    }
                } else {
                    if (!net.minecraftforge.oredict.DyeUtils.isDye(stack)) {
                        return ItemStack.EMPTY;
                    }

                    // noinspection ConstantConditions
                    float[] afloat = net.minecraftforge.oredict.DyeUtils.colorFromStack(stack).get().getColorComponentValues();
                    int red = (int)(afloat[0] * 255.0F);
                    int green = (int)(afloat[1] * 255.0F);
                    int blue = (int)(afloat[2] * 255.0F);
                    largestValueSum += Math.max(red, Math.max(green, blue));
                    colorValues[0] += red;
                    colorValues[1] += green;
                    colorValues[2] += blue;
                    dyeAmount++;
                }
            }
        }

        if (presentStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        else {
            int red = colorValues[0] / dyeAmount;
            int green = colorValues[1] / dyeAmount;
            int blue = colorValues[2] / dyeAmount;
            float f3 = (float)largestValueSum / (float)dyeAmount;
            float largestValue = (float)Math.max(red, Math.max(green, blue));
            red = (int)((float)red * f3 / largestValue);
            green = (int)((float)green * f3 / largestValue);
            blue = (int)((float)blue * f3 / largestValue);
            int color = (red << 8) + green;
            color = (color << 8) + blue;
            setColor(presentStack, color);
            return presentStack;
        }
    }

    private static int getColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag")) {
            compound = compound.getCompoundTag("BlockEntityTag");
            if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresent) {
                return compound.hasKey("RibbonColor") ? compound.getInteger("RibbonColor") : 16383998;
            } else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
                return compound.hasKey("Color") ? compound.getInteger("Color") : 16383998;
            }
        }
        return 16383998;
    }

    private static void setColor(ItemStack stack, int color) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) {
            if (!compound.hasKey("BlockEntityTag")) {
                compound.setTag("BlockEntityTag", new NBTTagCompound());
            }
        } else {
            compound = new NBTTagCompound();
            stack.setTagCompound(compound);
            compound.setTag("BlockEntityTag", new NBTTagCompound());
        }
        compound = compound.getCompoundTag("BlockEntityTag");
        if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresent) {
            compound.setInteger("RibbonColor", color);
        } else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
            compound.setInteger("Color", color);
        }
    }

    public ItemStack getRecipeOutput()
    {
        return ItemStack.EMPTY;
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
    {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < list.size(); ++i)
        {
            ItemStack itemstack = inv.getStackInSlot(i);
            list.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }

        return list;
    }

    public boolean isDynamic()
    {
        return true;
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canFit(int width, int height)
    {
        return width * height >= 2;
    }
}