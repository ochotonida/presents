package presents;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemColored;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import presents.common.CommonProxy;
import presents.common.block.BlockPresent;
import presents.common.block.BlockPresentEmpty;
import presents.common.item.recipe.RecipePresent;
import presents.common.tileentity.TileEntityPresent;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(modid = Presents.MODID, name = Presents.MODNAME, version = Presents.MODVERSION, updateJSON = "https://github.com/ochotonida/presents/blob/master/update.json")
public class Presents {

    public static final String MODID = "presents";
    public static final String MODNAME = "Presents";
    public static final String MODVERSION = "1.12.2-1.0.0";

    public static final Block PRESENT_BLOCK = new BlockPresent("present");
    public static final Block EMPTY_PRESENT_BLOCK = new BlockPresentEmpty("present_empty");

    public static final Item PRESENT_ITEM = new ItemColored(PRESENT_BLOCK, true).setRegistryName("present");
    public static final Item EMPTY_PRESENT_ITEM = new ItemColored(EMPTY_PRESENT_BLOCK, true).setRegistryName("present_empty");

    @Mod.Instance
    public static Presents instance;

    @SidedProxy(serverSide = "presents.common.CommonProxy", clientSide = "presents.client.ClientProxy")
    public static CommonProxy proxy;

    @net.minecraftforge.fml.common.Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(PRESENT_BLOCK, EMPTY_PRESENT_BLOCK);
            GameRegistry.registerTileEntity(TileEntityPresent.class, new ResourceLocation(MODID, "present"));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event ) {
            event.getRegistry().registerAll(PRESENT_ITEM, EMPTY_PRESENT_ITEM);
        }

        @SubscribeEvent
        public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
            event.getRegistry().register(new RecipePresent());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            proxy.registerItemRenderers();
        }
    }
}
