package plus.regionx.data;

import com.google.common.collect.ImmutableMap;
import plus.region.utl.FastExitException;
import plus.regionx.data.flag.ExtendedFlagData;
import plus.regionx.data.flag.StringData;
import plus.tson.utl.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.function.Supplier;

import static plus.region.data.IoUtils.*;


public class FlagRegistry {
    private static ImmutableMap<String, RegionFlag> flags = ImmutableMap.of();
    private static ImmutableMap<Integer, Tuple<RegionFlag.Advanced,Supplier<ExtendedFlagData>>> constructors = ImmutableMap.of();
    private static int nextId = 1, nextIdEx = 0;

    public static final RegionFlag.Base BLOCK_PLAYER = regSystem("block_player");
    public static final RegionFlag.Base BLOCK_ENTITY = regSystem("block_entity");
    public static final RegionFlag.Base PVP          = regSystem("pvp");
    public static final RegionFlag.Base PVE          = regSystem("pve");

    public static final RegionFlag.Advanced<StringData> NAME = regSystem(StringData::new, "name");

    static {
        expand(BLOCK_PLAYER, BLOCK_ENTITY, PVP, PVE, NAME);
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


    public static <T extends ExtendedFlagData> RegionFlag.Advanced<T> register(Supplier<T> constructor, String name) {
        if (flags.containsKey(name)) {
            throw new IllegalArgumentException("Flag already exists: " + name);
        }

        RegionFlag.Advanced<T> flag = new RegionFlag.Advanced<>(nextIdEx++, name);
        expand(flag);
        regConstructor(flag, (Supplier<ExtendedFlagData>) constructor);
        return flag;
    }


    private static void regConstructor(RegionFlag.Advanced<?> flag, Supplier<ExtendedFlagData> constructor){
        HashMap<Integer,Tuple<RegionFlag.Advanced,Supplier<ExtendedFlagData>>> copy = new HashMap<>(constructors);
        copy.put(flag.getId(), new Tuple<>(flag, constructor));
        constructors = ImmutableMap.copyOf(copy);
    }


    public static Tuple<RegionFlag.Advanced,Supplier<ExtendedFlagData>> readExtendedFrom(InputStream stream) throws FastExitException, IOException {
        int id = nextId > 255 ? readShort(stream) : readByte(stream);
        return constructors.get(id);
    }


    public static void writeExtendedTo(RegionFlag.Advanced flag, OutputStream stream) throws IOException {
        if(nextId > 255) writeShort(stream, flag.getId());
        else writeByte(stream, flag.getId());
    }


    public static Tuple<RegionFlag.Advanced,Supplier<ExtendedFlagData>> byId(int id){
        return constructors.get(id);
    }


    private static RegionFlag.Base regSystem(String name) {
        RegionFlag.Base res = new RegionFlag.Base(nextId, name);
        nextId <<= 1;
        return res;
    }


    private static <T extends ExtendedFlagData> RegionFlag.Advanced<T> regSystem(Supplier<T> constructor, String name) {
        RegionFlag.Advanced<T> adv = new RegionFlag.Advanced<>(nextIdEx++, name);
        regConstructor(adv, (Supplier<ExtendedFlagData>) constructor);
        return adv;
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
