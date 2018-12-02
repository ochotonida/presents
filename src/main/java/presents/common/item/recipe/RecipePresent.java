package presents.common.item.recipe;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemFireworkCharge;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import presents.Presents;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RecipePresent extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    private ItemStack result = ItemStack.EMPTY;

    public RecipePresent() {
        setRegistryName("recipepresent");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        result = ItemStack.EMPTY;

        ItemStack emptyPresent = ItemStack.EMPTY;
        NBTTagCompound fireworks = null;

        NonNullList<ItemStack> presentLoot = NonNullList.withSize(8, ItemStack.EMPTY);
        int listIndex = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (!stack.isEmpty()) {
                if (stack.getItem() == Presents.PRESENT_ITEM) {
                    return false;
                }
                if (stack.getItem() == Presents.EMPTY_PRESENT_ITEM) {
                    if (!emptyPresent.isEmpty()) {
                        return false;
                    }
                    emptyPresent = stack;
                } else if (stack.getItem() instanceof ItemFireworkCharge) {
                    if (fireworks != null) {
                        return false;
                    }
                    fireworks = stack.getSubCompound("Explosion");
                    if (fireworks == null) {
                        return false;
                    }
                } else {
                    if (listIndex > 8) {
                        return false;
                    }

                    ItemStack presentStack = stack.copy();
                    presentStack.setCount(1);
                    presentLoot.set(listIndex++, presentStack);
                }
            }
        }

        if (emptyPresent.isEmpty()) {
            return false;
        }

        result = new ItemStack(Presents.PRESENT_ITEM, 1, emptyPresent.getMetadata());
        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagCompound blockEntityTag = new NBTTagCompound();
        tagCompound.setTag("BlockEntityTag", blockEntityTag);
        result.setTagCompound(tagCompound);

        ItemStackHelper.saveAllItems(blockEntityTag, presentLoot);

        if (fireworks != null) {
            blockEntityTag.setTag("Explosion", fireworks.copy());
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return result.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
