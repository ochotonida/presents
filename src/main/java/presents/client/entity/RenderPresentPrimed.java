package presents.client.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import presents.Presents;
import presents.common.entity.EntityPresentPrimed;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RenderPresentPrimed extends Render<EntityPresentPrimed> {

    public static final Factory FACTORY = new Factory();
    private static final ResourceLocation TEXTURES = new ResourceLocation(Presents.MODID, "textures/entity/present_primed.png");
    private final ModelBase MODEL = new ModelPresentPrimed();

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPresentPrimed entity) {
        return TEXTURES;
    }

    private RenderPresentPrimed(RenderManager rendermanager) {
        super(rendermanager);
        shadowSize = 0.4F;
    }

    @Override
    public void doRender(EntityPresentPrimed entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        bindEntityTexture(entity);
        GlStateManager.translate((float) x, (float) y, (float) z);

        if ((float)entity.getFuse() - partialTicks + 1.0F < 10.0F)
        {
            float f = 1.0F - ((float)entity.getFuse() - partialTicks + 1.0F) / 10.0F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float f1 = 1.0F + f * 0.3F;
            GlStateManager.scale(f1, f1, f1);
        }

        float f2 = (1.0F - ((float)entity.getFuse() - partialTicks + 1.0F) / 100.0F) * 0.8F;

        MODEL.render(entity, 0, 0, 0, 0, 0, 0.0625F);

        if (entity.getFuse() / 5 % 2 == 0)
        {
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
            GlStateManager.doPolygonOffset(-3.0F, -3.0F);
            GlStateManager.enablePolygonOffset();
            MODEL.render(entity, 1, 0, 0, 0, 0, 0.0625F);
            GlStateManager.doPolygonOffset(0.0F, 0.0F);
            GlStateManager.disablePolygonOffset();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static class Factory implements IRenderFactory<EntityPresentPrimed> {

        @Override
        public Render<? super EntityPresentPrimed> createRenderFor(RenderManager manager) {
            return new RenderPresentPrimed(manager);
        }
    }

    private static class ModelPresentPrimed extends ModelBase {

        private final ModelRenderer box;
        private final ModelRenderer boxRibbon;
        private final ModelRenderer lid;
        private final ModelRenderer lidRibbon;
        private final ModelRenderer ribbon1;
        private final ModelRenderer ribbon2;

        ModelPresentPrimed() {
            textureWidth = 128;
            textureHeight = 64;
            box = new ModelRenderer(this, 0, 0);
            boxRibbon = new ModelRenderer(this, 0, 20);
            lid = new ModelRenderer(this, 40, 0);
            lidRibbon = new ModelRenderer(this, 40, 15);
            ribbon1 = new ModelRenderer(this, 0, 40);
            ribbon2 = new ModelRenderer(this, 0, 40);

            box.addBox(-5, 0, -5, 10, 10, 10);
            boxRibbon.addBox(-5, 0, -5, 10, 10, 10);
            lid.addBox(-6, 8, -6, 12, 3, 12);
            lidRibbon.addBox(-6, 8, -6, 12, 3, 12);
            ribbon1.addBox(-8, 11, 0, 16, 4, 0);
            ribbon2.addBox(-8, 11, 0, 16, 4, 0);
        }

        @Override
        public void render(@Nullable Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            GlStateManager.pushMatrix();

            float[] c1 = EnumDyeColor.WHITE.getColorComponentValues();
            float[] c2 = c1;

            if (entity instanceof EntityPresentPrimed) {
                int color = ((EntityPresentPrimed) entity).getColor();
                int r = (color & 16711680) >> 16;
                int g = (color & 65280) >> 8;
                int b = color & 255;
                c1 = new float[]{(float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F};
                color = ((EntityPresentPrimed) entity).getRibbonColor();
                r = (color & 16711680) >> 16;
                g = (color & 65280) >> 8;
                b = color & 255;
                c2 = new float[]{(float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F};
            }

            GlStateManager.color(c1[0], c1[1], c1[2]);
            box.render(scale);

            if (limbSwing == 0) {
                GlStateManager.color(c2[0], c2[1], c2[2]);
                boxRibbon.render(scale);
            }

            GlStateManager.scale(11F/12, 1, 11F/12);

            GlStateManager.color(c1[0], c1[1], c1[2]);
            lid.render(scale);

            if (limbSwing == 0) {
                GlStateManager.color(c2[0], c2[1], c2[2]);
                lidRibbon.render(scale);
            }

            GlStateManager.scale(1, 1, 1);

            if (limbSwing == 0) {
                GlStateManager.color(c2[0], c2[1], c2[2]);
                GlStateManager.scale(Math.sqrt(2), 1, Math.sqrt(2));
                GlStateManager.disableLighting();

                GlStateManager.rotate(45, 0, 1, 0);
                ribbon1.render(scale);
                GlStateManager.rotate(90, 0, 1, 0);
                ribbon2.render(scale);
                GlStateManager.enableLighting();
            }

            GlStateManager.popMatrix();
        }
    }
}
