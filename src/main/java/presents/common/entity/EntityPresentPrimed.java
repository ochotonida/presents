package presents.common.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityPresentPrimed extends EntityTNTPrimed {

    private static final DataParameter<Integer> COLOR = EntityDataManager.createKey(EntityTNTPrimed.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> COLOR_RIBBON = EntityDataManager.createKey(EntityTNTPrimed.class, DataSerializers.VARINT);

    public int getColor() {
        return dataManager.get(COLOR);
    }

    public int getRibbonColor() {
        return dataManager.get(COLOR_RIBBON);
    }

    private void setColor(int color) {
        dataManager.set(COLOR, color);
        dataManager.setDirty(COLOR);
    }

    private void setRibbonColor(int color) {
        dataManager.set(COLOR_RIBBON, color);
        dataManager.setDirty(COLOR_RIBBON);
    }

    public EntityPresentPrimed(World world) {
        super(world);
        this.setSize(0.98F, 0.98F);
    }

    public EntityPresentPrimed(World world, double x, double y, double z, EntityLivingBase igniter, int color, int ribbonColor) {
        super(world, x, y, z, igniter);
        setSize(10F/16, 10F/16);
        setColor(color);
        setRibbonColor(ribbonColor);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(COLOR, EnumDyeColor.WHITE.getColorValue());
        dataManager.register(COLOR_RIBBON, EnumDyeColor.WHITE.getColorValue());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("Color", getColor());
        compound.setInteger("RibbonColor", getRibbonColor());
    }

    @Override public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Color")) {
            setColor(compound.getInteger("Color"));
        }
        if (compound.hasKey("RibbonColor", getRibbonColor())) {
            setRibbonColor(compound.getInteger("RibbonColor"));
        }
    }
}
