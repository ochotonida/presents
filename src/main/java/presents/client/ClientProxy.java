package presents.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import presents.Presents;
import presents.common.CommonProxy;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderers() {
        ModelLoader.setCustomModelResourceLocation(Presents.PRESENT_ITEM, 0, new ModelResourceLocation(Presents.MODID + ":present", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Presents.EMPTY_PRESENT_ITEM, 0, new ModelResourceLocation(Presents.MODID + ":present_empty", "inventory"));
    }
}
