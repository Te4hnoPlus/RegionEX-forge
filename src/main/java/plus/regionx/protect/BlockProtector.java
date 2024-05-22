package plus.regionx.protect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import plus.region.Region;
import plus.region.RegionQuery;
import plus.regionx.FlagUtils;
import plus.regionx.access.WorldFieldAccess;
import plus.regionx.access.WorldLocal;
import plus.regionx.data.RegionData;
import plus.regionx.data.flag.UserData;


public class BlockProtector extends BaseProtector{
    public BlockProtector(WorldFieldAccess access) {
        super(access);
    }


    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event){
        if(!canEdit(access.get(event.getPlayer().world), event.getPlayer(), event.getPos()))
            event.setCanceled(true);
    }


    @SubscribeEvent
    public void onPlaceBlock(BlockEvent.PlaceEvent event){
        if(!canEdit(access.get(event.getPlayer().world), event.getPlayer(), event.getPos()))
            event.setCanceled(true);
    }


    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event){
        if(!canEdit(access.get(event.getEntityPlayer().world), event.getEntityPlayer(), event.getPos()))
            event.setCanceled(true);
    }


    @SubscribeEvent
    public void onEntityPlace(BlockEvent.EntityPlaceEvent event){
        WorldLocal local = access.get(event.getWorld());
        BlockPos pos = event.getPos();

        Entity entity = event.getEntity();

        if(entity instanceof EntityPlayer){
            if(!canEdit(local, (EntityPlayer) entity, pos)){
                event.setCanceled(true);
                return;
            }
        } else {
            // TODO: Add support for other entities
        }
    }


    private boolean canEdit(WorldLocal local, EntityPlayer player, BlockPos pos){
        RegionQuery regions = local.context().getRegions(pos.getX(), pos.getY(), pos.getZ());

        if(regions.isEmpty()) return true;

        for (Region region : regions) {
            RegionData data = region.getData(local.getDataManager());
            if(data == null) continue;

            UserData userData = data.getEntry(player.getUniqueID());

            if(!FlagUtils.canEditBlock(data, userData, player)) {
                return false;
            }
        }
        return true;
    }
}