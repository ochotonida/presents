package presents.common.item.recipe;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
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
        setRegistryName("recipe_present");
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        result = ItemStack.EMPTY;

        ItemStack emptyPresent = ItemStack.EMPTY;
        NBTTagCompound fireworks = null;
        int tntAmount = 0;

        NonNullList<ItemStack> presentLoot = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        int listIndex = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (!stack.isEmpty()) {
                if (stack.getItem() == Presents.PRESENT_ITEM || net.minecraftforge.oredict.DyeUtils.isDye(stack)) {
                    return false;
                }
                if (stack.getItem() == Item.getItemFromBlock(Blocks.TNT)) {
                    tntAmount++;
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

        if (emptyPresent.isEmpty() || tntAmount > 1) {
            return false;
        }

        result = new ItemStack(Presents.PRESENT_ITEM, 1, emptyPresent.getMetadata());


        NBTTagCompound tagCompound = emptyPresent.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
        tagCompound.setTag("BlockEntityTag", blockEntityTag);
        result.setTagCompound(tagCompound);

        ItemStackHelper.saveAllItems(blockEntityTag, presentLoot);

        if (fireworks != null) {
            blockEntityTag.setTag("Explosion", fireworks.copy());
        }

        System.out.println("d");
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
