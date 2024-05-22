package plus.regionx;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.regionx.access.DefaultCIter;
import plus.regionx.access.MapWorldAccess;
import plus.regionx.cmd.RegionCmd;
import plus.regionx.protect.BaseProtector;
import plus.regionx.protect.BlockProtector;
import plus.regionx.protect.EntityProtector;
import plus.regionx.protect.RegionLoadTracker;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

import static net.minecraft.network.NetworkSystem.SERVER_EPOLL_EVENTLOOP;
import static net.minecraft.network.NetworkSystem.SERVER_NIO_EVENTLOOP;


@Mod(modid = "region_ex", name = "RegionEX", version = "1.0-forge")
public class MainRegionEX {
    public static final Logger log = LogManager.getLogger("RegionEX");
    private boolean hasElca = false;
    private MapWorldAccess access;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        //CapWorldFieldAccess access = new CapWorldFieldAccess();
        access = new MapWorldAccess();

        boolean protectBlocks = true, protectEntities = true, trackChunks = true;

        ArrayList<BaseProtector> list = new ArrayList<>();

        if(trackChunks)        list.add(new RegionLoadTracker(access) );
        if(protectBlocks)      list.add(new BlockProtector(access)    );
        if(protectEntities)    list.add(new EntityProtector(access)   );


        list.forEach(BaseProtector::init);
    }


    public static ScheduledExecutorService getExecutor(MinecraftServer server) {
        LazyLoadBase <? extends EventLoopGroup> lazyloadbase;

        if (Epoll.isAvailable() && server.shouldUseNativeTransport()) {
            lazyloadbase = SERVER_EPOLL_EVENTLOOP;
        } else {
            lazyloadbase = SERVER_NIO_EVENTLOOP;
        }
        return lazyloadbase.getValue();
    }


    public static Function<WorldServer,Iterable<Long>> chunkIterBuilder(){
        return DefaultCIter::new;
    }


    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {

    }


    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {

    }


    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new RegionCmd(access));
    }
}
