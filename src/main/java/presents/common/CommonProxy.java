package presents.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import presents.Presents;
import presents.common.entity.EntityPresentPrimed;

public class CommonProxy {

    public void registerItemRenderers() { }

    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(Presents.MODID, "present_primed"), EntityPresentPrimed.class, "present_primed", 1, Presents.instance, 64, 3, true);
    }
}
