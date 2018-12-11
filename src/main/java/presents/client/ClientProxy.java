package presents.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import presents.Presents;
import presents.client.entity.RenderPresentPrimed;
import presents.common.CommonProxy;
import presents.common.block.BlockColorHandler;
import presents.common.entity.EntityPresentPrimed;
import presents.common.item.ItemColorHandler;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderers() {
        ModelLoader.setCustomModelResourceLocation(Presents.PRESENT_ITEM, 0, new ModelResourceLocation(Presents.MODID + ":present", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Presents.EMPTY_PRESENT_ITEM, 0, new ModelResourceLocation(Presents.MODID + ":present_empty", "inventory"));
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityPresentPrimed.class, RenderPresentPrimed.FACTORY);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new BlockColorHandler(), Presents.PRESENT_BLOCK, Presents.EMPTY_PRESENT_BLOCK);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemColorHandler(), Presents.PRESENT_BLOCK, Presents.EMPTY_PRESENT_BLOCK);
    }
}
