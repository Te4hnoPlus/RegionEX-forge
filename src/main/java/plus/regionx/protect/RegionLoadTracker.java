package plus.regionx.protect;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import plus.regionx.access.WorldFieldAccess;
import plus.regionx.access.WorldLocal;


public class RegionLoadTracker extends BaseProtector{
    public RegionLoadTracker(WorldFieldAccess access) {
        super(access);
    }


    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event){
        WorldLocal local = access.get(event.getWorld());
        if(local == null) return;
        local.ensureLoaded(event.getChunk());
    }


    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            WorldLocal local = access.get(event.world);
            if(local == null) return;
            local.tick(event.world);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public void onUnload(WorldEvent.Unload event){
        WorldLocal local = access.get(event.getWorld());
        if(local == null) return;
        local.save(event.getWorld(), true);
        local.onUnload();
    }
}