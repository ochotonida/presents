package presents.common;

import net.minecraftforge.common.config.Config;
import presents.Presents;

@SuppressWarnings("unused")
@Config(modid = Presents.MODID, name = Presents.MODNAME)
public class PresentsConfig {
    @Config.Comment({"Chance to generate a tree with presents, per chunk in most cold biomes"})
    @Config.RangeDouble(min = 0, max = 1)
    public static double worldGenChance = 1D/25;

    public static double getWorldGenChance() {
        return Math.max(0, Math.min(1, worldGenChance));
    }
}
