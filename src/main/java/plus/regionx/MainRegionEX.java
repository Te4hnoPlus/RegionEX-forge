package plus.regionx;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.regionx.access.MapWorldAccess;


@Mod(modid = "region_ex", name = "RegionEX", version = "1.0-forge")
public class MainRegionEX {
    public static final Logger log = LogManager.getLogger("RegionEX");

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        //CapWorldFieldAccess access = new CapWorldFieldAccess();
        MapWorldAccess access = new MapWorldAccess();



        BlockProtector events = new BlockProtector(access);

    }


    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {

    }


    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {

    }
}
