package plus.regionx.data;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;


public class FlagRegistry {
    private static ImmutableMap<String, RegionFlag> flags = ImmutableMap.of();
    private static int nextId = 1, nextIdEx = 0;

    public static final RegionFlag.Base BLOCK_PLAYER = regSystem("block_player");
    public static final RegionFlag.Base BLOCK_ENTITY = regSystem("block_entity");
    public static final RegionFlag.Base PVP          = regSystem("pvp");
    public static final RegionFlag.Base PVE          = regSystem("pve");


    static {
        expand(BLOCK_PLAYER, BLOCK_ENTITY, PVP, PVE);
    }


    public static RegionFlag flagForName(String name) {
        return flags.get(name);
    }


    public static RegionFlag.Base register(String name) {
        if (flags.containsKey(name)) {
            throw new IllegalArgumentException("Flag already exists: " + name);
        }

        RegionFlag.Base flag = new RegionFlag.Base(nextId, name);
        nextId <<= 1;
        expand(flag);
        return flag;
    }


    public static <T extends ExtendedFlagData> RegionFlag.Advanced<T> register(Class<T> clazz, String name) {
        if (flags.containsKey(name)) {
            throw new IllegalArgumentException("Flag already exists: " + name);
        }

        RegionFlag.Advanced<T> flag = new RegionFlag.Advanced<>(nextIdEx, name);
        expand(flag);
        return flag;
    }


    private static RegionFlag.Base regSystem(String name) {
        RegionFlag.Base res = new RegionFlag.Base(nextId, name);
        nextId <<= 1;
        return res;
    }


    private static <T extends ExtendedFlagData> RegionFlag.Advanced<T> regSystem(Class<T> clazz, String name) {
        return new RegionFlag.Advanced<>(nextIdEx++, name);
    }


    private static void expand(RegionFlag... addFlags){
        HashMap<String, RegionFlag> copy = new HashMap<>(flags);

        for (RegionFlag flag : addFlags) {
            copy.put(flag.getName(), flag);
        }

        flags = ImmutableMap.copyOf(copy);
    }


    private static void expand(RegionFlag flag){
        HashMap<String, RegionFlag> copy = new HashMap<>(flags);

        copy.put(flag.getName(), flag);

        flags = ImmutableMap.copyOf(copy);
    }
}
