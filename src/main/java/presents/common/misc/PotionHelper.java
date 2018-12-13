package presents.common.misc;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class PotionHelper {

    public static void spawnPotion(ItemStack stack, World world, BlockPos pos) {
        if (!world.isRemote) {
            PotionType potionType = PotionUtils.getPotionFromItem(stack);
            List<PotionEffect> potionEffectList = PotionUtils.getEffectsFromStack(stack);
            AxisAlignedBB boundingBox = new AxisAlignedBB(pos.getX() + 0.5 - 2, pos.getY() + 0.5 - 1, pos.getZ() + 0.5 - 2, pos.getX() + 0.5 + 2, pos.getY() + 0.5 + 1, pos.getZ() + 0.5 + 2);

            if (potionType == PotionTypes.WATER && potionEffectList.isEmpty()) {
                List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox, EntityPotion.WATER_SENSITIVE);

                if (!entities.isEmpty()) {
                    for (EntityLivingBase entity : entities) {
                        if (pos.distanceSq(entity.posX, entity.posY, entity.posZ) < 16 && (entity instanceof EntityEnderman || entity instanceof EntityBlaze)) {
                            entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
                        }
                    }
                }
            } else if (!potionEffectList.isEmpty()) {
                doPotionEffects(world, pos, stack, potionType, potionEffectList);
            }
            world.playEvent(potionType.hasInstantEffect() ? 2007 : 2002, pos, PotionUtils.getColor(stack));
        }
    }

    private static void doPotionEffects(World world, BlockPos pos, ItemStack stack, PotionType potionType, List<PotionEffect> potionEffectList) {
        if (stack.getItem() instanceof ItemLingeringPotion) {
            EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            entityareaeffectcloud.setRadius(3.0F);
            entityareaeffectcloud.setRadiusOnUse(-0.5F);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setRadiusPerTick(- entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.setPotion(potionType);

            for (PotionEffect potioneffect : PotionUtils.getFullEffectsFromItem(stack)) {
                entityareaeffectcloud.addEffect(new PotionEffect(potioneffect));
            }

            NBTTagCompound nbttagcompound = stack.getTagCompound();

            if (nbttagcompound != null && nbttagcompound.hasKey("CustomPotionColor", 99))
            {
                entityareaeffectcloud.setColor(nbttagcompound.getInteger("CustomPotionColor"));
            }

            world.spawnEntity(entityareaeffectcloud);

        } else {

            List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + 0.5 - 2, pos.getY() + 0.5 - 1, pos.getZ() + 0.5 - 2, pos.getX() + 0.5 + 2, pos.getY() + 0.5 + 1, pos.getZ() + 0.5 + 2));

            for (EntityLivingBase entity : entities) {
                double distance = pos.distanceSq(entity.posX, entity.posY, entity.posZ);
                if (entity.canBeHitWithPotion() && distance < 16) {
                    double health = 1 - Math.sqrt(distance) / 4;

                    for (PotionEffect potioneffect : potionEffectList) {
                        Potion potion = potioneffect.getPotion();

                        if (potion.isInstant()) {
                            potion.affectEntity(null, null, entity, potioneffect.getAmplifier(), health);
                        } else {
                            int time = (int)(health * potioneffect.getDuration() + 0.5);

                            if (time > 20) {
                                entity.addPotionEffect(new PotionEffect(potion, time, potioneffect.getAmplifier(), potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
                            }
                        }
                    }
                }
            }
        }
    }
}
