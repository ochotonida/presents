package presents.client;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.client.model.ModelLoader;
import presents.Presents;
import presents.common.CommonProxy;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerItemRenderers() {
        for (int i = 0; i < 16; i++) {
            ModelLoader.setCustomModelResourceLocation(Presents.PRESENT_ITEM, i, new ModelResourceLocation(Presents.MODID + ":present_" + EnumDyeColor.byMetadata(i).getName()));
            ModelLoader.setCustomModelResourceLocation(Presents.EMPTY_PRESENT_ITEM, i, new ModelResourceLocation(Presents.MODID + ":empty_present_" + EnumDyeColor.byMetadata(i).getName()));
        }
    }
}
