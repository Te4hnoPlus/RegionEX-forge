package plus.regionx.access;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import plus.region.RegionMapEx;
import plus.region.data.db.RocksDataManager;
import plus.regionx.MainRegionEX;
import plus.regionx.data.RegionData;
import plus.regionx.data.RocksRegionDataCoder;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;


public class WorldLocal {
    private Int2ObjectFunction<RegionData> dataManager;
    private RegionMapEx regionMapEx;
    private RegionMapEx.Context context;;
    Object[] data = new Object[1];
    public int timer;
    private ScheduledExecutorService service;

    public void initIfNeed(World world){
        if(regionMapEx != null) return;

        if(!world.isRemote){
            File file = new File(world.getSaveHandler().getWorldDirectory(), "regionx");
            regionMapEx = new RegionMapEx(file);

            service = MainRegionEX.getExecutor(world.getMinecraftServer());
            dataManager = new RocksDataManager<>(new File(file, "data").getAbsolutePath(), true, RocksRegionDataCoder.instance());
        } else {
            dataManager = new Int2ObjectOpenHashMap<>();
            regionMapEx = new RegionMapEx(null);
        }

        context = regionMapEx.newContext();
    }


    public void tick(World world) {
        ++timer;
        if (timer % 3600 == 0) {
            save(world, false);
        }
    }


    public void save(World world, boolean sync){
        if (regionMapEx == null) return;
        if (regionMapEx.hasDirty()) {
            if(!world.isRemote) {
                regionMapEx.flushToDisk(sync?null:service);
            } else {
                regionMapEx.clearDirty();
            }
        } else {
            if(!world.isRemote) {
                regionMapEx.checkToUnload(MainRegionEX.chunkIterBuilder().apply((WorldServer) world).iterator());
            }
        }
    }


    public void onUnload(){
        if(dataManager instanceof AutoCloseable){
            try {
                ((AutoCloseable) dataManager).close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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
