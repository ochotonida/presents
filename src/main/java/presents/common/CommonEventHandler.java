package presents.common;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import presents.common.world.gen.WorldGenPresentTree;

public class CommonEventHandler {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onBiomeDecorate(DecorateBiomeEvent.Decorate event) {
        if (event.getType() == DecorateBiomeEvent.Decorate.EventType.TREE) {
            Biome biome = event.getWorld().getBiome(event.getChunkPos().getBlock(0, 0, 0));
            WorldGenAbstractTree treeFeature = biome.getRandomTreeFeature(event.getRand());
            if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD) || treeFeature instanceof WorldGenTaiga2 || treeFeature instanceof WorldGenTaiga1) && event.getRand().nextInt(24) == 0) {
                int k6 = event.getRand().nextInt(16) + 8;
                int l = event.getRand().nextInt(16) + 8;
                WorldGenAbstractTree worldgenabstracttree = new WorldGenPresentTree(false);
                worldgenabstracttree.setDecorationDefaults();
                BlockPos blockpos = event.getWorld().getHeight(event.getChunkPos().getBlock(k6, 0, l));

                if (worldgenabstracttree.generate(event.getWorld(), event.getRand(), blockpos)) {
                    worldgenabstracttree.generateSaplings(event.getWorld(), event.getRand(), blockpos);
                }
            }
        }
    }
}