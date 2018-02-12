package terrails.netherutils.entity.capabilities.deathzone;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import terrails.netherutils.Constants;
import terrails.netherutils.api.capabilities.IDeathZone;
import terrails.netherutils.config.ConfigHandler;
import terrails.netherutils.init.ModFeatures;
import terrails.netherutils.network.CPacketTitle;
import terrails.terracore.capabilities.CapabilitySerializable;

public class CapabilityDeathZone {

    @CapabilityInject(IDeathZone.class)
    public static final Capability<IDeathZone> DEATH_ZONE_CAPABILITY;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDeathZone.class, new Capability.IStorage<IDeathZone>() {

            @Override
            public NBTBase writeNBT(Capability<IDeathZone> capability, IDeathZone instance, EnumFacing side) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("DeathZone", instance.getDeathCounter());
                compound.setInteger("TickCounter", instance.tickCounter());
                return compound;
            }
            @Override
            public void readNBT(Capability<IDeathZone> capability, IDeathZone instance, EnumFacing side, NBTBase nbt) {
                NBTTagCompound compound = (NBTTagCompound) nbt;
                instance.setDeathCounter(compound.getInteger("DeathZone"));
                instance.tickCounter(compound.getInteger("TickCounter"));
            }
        }, DeathZone::new);
    }

    public static class Handler {

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityPlayer) {
                event.addCapability(new ResourceLocation(Constants.MOD_ID, "DeathZone"), new CapabilitySerializable<>(CapabilityDeathZone.DEATH_ZONE_CAPABILITY));
            }
        }
    }

    static {
        DEATH_ZONE_CAPABILITY = null;
    }
}