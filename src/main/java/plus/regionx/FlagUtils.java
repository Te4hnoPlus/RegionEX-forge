package plus.regionx;

import net.minecraft.entity.player.EntityPlayer;
import plus.regionx.data.RegionData;


public class FlagUtils {
    // 1 = protect blocks from users
    // 2 = protect blocks from mobs
    // 4 = allow PVP
    // 8 = allow PVE


    public static boolean canEditBlock(RegionData data, RegionData.Entry entry, EntityPlayer entity){
        if(canPlayerEditBlock(data))return true;
        if(entity == null)return false;
        return entry.isMember();
    }



    public static boolean canPlayerEditBlock(RegionData data){
        return data.getFlag(1);
    }


    public static boolean canMobEditBlock(RegionData data){
        return data.getFlag(2);
    }
}