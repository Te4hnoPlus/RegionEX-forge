package plus.regionx.access;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import plus.region.RegionMapEx;
import plus.regionx.data.RegionData;
import java.io.File;
import java.util.Arrays;


public class WorldLocal {
    private Int2ObjectFunction<RegionData> dataManager;
    private RegionMapEx regionMapEx;
    private RegionMapEx.Context context;;
    Object[] data = new Object[1];

    public void initIfNeed(World world){
        if(regionMapEx != null) return;
        regionMapEx = new RegionMapEx(new File(world.getSaveHandler().getWorldDirectory(), "regionx"));
        context = regionMapEx.newContext();
    }


    public void ensureLoaded(Chunk chunk){
        initIfNeed(chunk.getWorld());
        context.ensureLoaded((chunk.x<<4) + 8, (chunk.z<<4) + 8);
    }


    public RegionMapEx map(){
        return regionMapEx;
    }


    public Int2ObjectFunction<RegionData> getDataManager(){
        return dataManager;
    }


    public void setDataManager(Int2ObjectFunction<RegionData> dataManager){
        this.dataManager = dataManager;
    }


    public RegionMapEx.Context context(){
        return context;
    }


    public Object get(int id) {
        if(id >= data.length) {
            data = Arrays.copyOf(data, id + 1);
            return null;
        }
        return data[id];
    }


    public void set(int id, Object value) {
        if(id >= data.length) {
            data = Arrays.copyOf(data, id + 1);
        }
        data[id] = value;
    }
}
