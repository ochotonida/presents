package presents;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import presents.common.CommonEventHandler;
import presents.common.CommonProxy;
import presents.common.block.BlockColorHandler;
import presents.common.block.BlockPresent;
import presents.common.block.BlockPresentEmpty;
import presents.common.item.ItemColorHandler;
import presents.common.item.recipe.RecipePresent;
import presents.common.item.recipe.RecipePresentDye;
import presents.common.tileentity.TileEntityPresent;

@SuppressWarnings({"WeakerAccess", "unused"})
@Mod(modid = Presents.MODID, name = Presents.MODNAME, version = Presents.MODVERSION, updateJSON = "https://github.com/ochotonida/presents/blob/master/update.json")
public class Presents {

    public Presents() {
        MinecraftForge.TERRAIN_GEN_BUS.register(CommonEventHandler.class);
    }

    public static final String MODID = "presents";
    public static final String MODNAME = "Presents";
    public static final String MODVERSION = "1.12.2-1.2.0";

    public static final Block PRESENT_BLOCK = new BlockPresent("present");
    public static final Block EMPTY_PRESENT_BLOCK = new BlockPresentEmpty("present_empty");

    public static final Item PRESENT_ITEM = new ItemBlock(PRESENT_BLOCK).setRegistryName("present");
    public static final Item EMPTY_PRESENT_ITEM = new ItemBlock(EMPTY_PRESENT_BLOCK).setRegistryName("present_empty");

    public static final ResourceLocation LOOTTABLE_PRESENT_REGULAR = new ResourceLocation(MODID, "present_regular");
    public static final ResourceLocation LOOTTABLE_PRESENT_SPECIAL = new ResourceLocation(MODID, "present_special");

    @Mod.Instance
    public static Presents instance;

    @SidedProxy(serverSide = "presents.common.CommonProxy", clientSide = "presents.client.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        LootTableList.register(LOOTTABLE_PRESENT_REGULAR);
        LootTableList.register(LOOTTABLE_PRESENT_SPECIAL);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColorHandler(), PRESENT_BLOCK, EMPTY_PRESENT_BLOCK);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColorHandler(), PRESENT_BLOCK, EMPTY_PRESENT_BLOCK);
    }

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
            event.getRegistry().registerAll(new RecipePresent(), new RecipePresentDye());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event) {
            proxy.registerItemRenderers();
        }
    }
}
