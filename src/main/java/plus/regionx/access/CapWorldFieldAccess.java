package plus.regionx.access;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class CapWorldFieldAccess implements WorldFieldAccess{
    private final ResourceLocation ID = new ResourceLocation("regionx");
    @CapabilityInject(WorldLocal.class)
    public static Capability<WorldLocal> CONTAINER_CAP = null;

    public static final Capability.IStorage<WorldLocal> STORAGE = new Capability.IStorage<WorldLocal>() {
        @Nullable
        @Override
        public NBTBase writeNBT(Capability<WorldLocal> capability, WorldLocal instance, EnumFacing side) {
            return null;
        }

        @Override
        public void readNBT(Capability<WorldLocal> capability, WorldLocal instance, EnumFacing side, NBTBase nbt) {

        }
    };

    private final Provider PROVIDER = new Provider();


    public CapWorldFieldAccess(){
        CapabilityManager.INSTANCE.register(WorldLocal.class, STORAGE, WorldLocal::new);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private static class Provider implements ICapabilityProvider {
        private static final WorldLocal empty = new WorldLocal();

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CONTAINER_CAP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return capability == CONTAINER_CAP ? CONTAINER_CAP.cast(empty) : null;
        }
    }


    @SubscribeEvent
    public void attach(AttachCapabilitiesEvent<World> event){
        event.addCapability(ID, PROVIDER);
    }


    @Override
    public WorldLocal get(World world) {
        return world.getCapability(CONTAINER_CAP, EnumFacing.DOWN);
    }
}
