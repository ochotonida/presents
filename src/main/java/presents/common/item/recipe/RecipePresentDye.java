package presents.common.item.recipe;

import java.util.ArrayList;
import java.util.List;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import presents.common.block.BlockPresent;
import presents.common.block.BlockPresentEmpty;

import javax.annotation.ParametersAreNonnullByDefault;

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
        int i = 0;
        int j = 0;

        for (int k = 0; k < inv.getSizeInventory(); ++k) {
            ItemStack stack = inv.getStackInSlot(k);

            if (!stack.isEmpty()) {
                if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
                    presentStack = stack.copy();
                    presentStack.setCount(1);

                    int colour = getColor(presentStack);
                    if (colour != EnumDyeColor.WHITE.getColorValue()) {
                        float f = (float)(colour >> 16 & 255) / 255.0F;
                        float f1 = (float)(colour >> 8 & 255) / 255.0F;
                        float f2 = (float)(colour & 255) / 255.0F;
                        i = (int)((float)i + Math.max(f, Math.max(f1, f2)) * 255.0F);
                        colorValues[0] = (int)((float)colorValues[0] + f * 255.0F);
                        colorValues[1] = (int)((float)colorValues[1] + f1 * 255.0F);
                        colorValues[2] = (int)((float)colorValues[2] + f2 * 255.0F);
                        ++j;
                    }
                } else {
                    if (!net.minecraftforge.oredict.DyeUtils.isDye(stack)) {
                        return ItemStack.EMPTY;
                    }

                    float[] afloat = net.minecraftforge.oredict.DyeUtils.colorFromStack(stack).get().getColorComponentValues();
                    int l1 = (int)(afloat[0] * 255.0F);
                    int i2 = (int)(afloat[1] * 255.0F);
                    int j2 = (int)(afloat[2] * 255.0F);
                    i += Math.max(l1, Math.max(i2, j2));
                    colorValues[0] += l1;
                    colorValues[1] += i2;
                    colorValues[2] += j2;
                    ++j;
                }
            }
        }

        if (presentStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        else {
            int i1 = colorValues[0] / j;
            int j1 = colorValues[1] / j;
            int k1 = colorValues[2] / j;
            float f3 = (float)i / (float)j;
            float f4 = (float)Math.max(i1, Math.max(j1, k1));
            i1 = (int)((float)i1 * f3 / f4);
            j1 = (int)((float)j1 * f3 / f4);
            k1 = (int)((float)k1 * f3 / f4);
            int k2 = (i1 << 8) + j1;
            k2 = (k2 << 8) + k1;
            setColor(presentStack, k2);
            return presentStack;
        }
    }

    private static int getColor(ItemStack stack) {
        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null && compound.hasKey("BlockEntityTag")) {
            compound = compound.getCompoundTag("BlockEntityTag");
            if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresent) {
                return compound.hasKey("RibbonColor") ? compound.getInteger("RibbonColor") : EnumDyeColor.WHITE.getColorValue();
            } else if (Block.getBlockFromItem(stack.getItem()) instanceof BlockPresentEmpty) {
                return compound.hasKey("Color") ? compound.getInteger("Color") : EnumDyeColor.WHITE.getColorValue();
            }
        }
        return EnumDyeColor.WHITE.getColorValue();
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