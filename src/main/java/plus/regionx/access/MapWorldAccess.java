package plus.regionx.access;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.HashMap;


public class MapWorldAccess implements WorldFieldAccess {
    private ImmutableMap<World, WorldLocal> data = ImmutableMap.of();

    public MapWorldAccess() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLoadWorld(WorldEvent.Load event) {
        synchronized (this) {
            HashMap<World, WorldLocal> copy = new HashMap<>(data);
            copy.put(event.getWorld(), new WorldLocal());
            data = ImmutableMap.copyOf(copy);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUnloadWorld(WorldEvent.Unload event) {
        synchronized (this) {
            HashMap<World, WorldLocal> copy = new HashMap<>(data);
            copy.remove(event.getWorld());
            data = ImmutableMap.copyOf(copy);
        }
    }


    @Override
    public WorldLocal get(World world) {
        return data.get(world);
    }
}
