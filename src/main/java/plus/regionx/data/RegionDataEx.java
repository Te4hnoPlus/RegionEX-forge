package plus.regionx.data;

import com.google.common.collect.ImmutableMap;
import plus.region.utl.FastExitException;
import plus.regionx.data.flag.ExtendedFlagData;
import plus.tson.utl.Tuple;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import static plus.region.data.IoUtils.*;


public class RegionDataEx extends RegionData{
    private ImmutableMap<RegionFlag.Advanced, ExtendedFlagData> exFlags = ImmutableMap.of();

    @Override
    public ExtendedFlagData getFlagExtended(RegionFlag.Advanced flag) {
        return exFlags.get(flag);
    }


    @Override
    public RegionData setFlagExtended(RegionFlag.Advanced flag, ExtendedFlagData value) {
        HashMap<RegionFlag.Advanced, ExtendedFlagData> newFlags = new HashMap<>(exFlags);
        newFlags.put(flag, value);
        exFlags = ImmutableMap.copyOf(newFlags);
        return this;
    }


    @Override
    public void readFrom(InputStream stream) throws IOException, FastExitException {
        super.readFrom(stream);
        int count = readShort(stream);
        if(count == 0) return;
        HashMap<RegionFlag.Advanced, ExtendedFlagData> newFlags = new HashMap<>(exFlags);

        for (int i = 0; i < count; i++) {
            Tuple<RegionFlag.Advanced, Supplier<ExtendedFlagData>> entry = FlagRegistry.readExtendedFrom(stream);
            ExtendedFlagData newFlag = entry.B.get();
            newFlag.readFrom(stream);
            newFlags.put(entry.A, newFlag);
        }
        exFlags = ImmutableMap.copyOf(newFlags);
    }


    @Override
    public void writeTo(OutputStream stream) throws IOException {
        super.writeTo(stream);
        writeShort(stream, exFlags.size());

        for (Map.Entry<RegionFlag.Advanced, ExtendedFlagData> entry : exFlags.entrySet()) {
            FlagRegistry.writeExtendedTo(entry.getKey(), stream);
            entry.getValue().writeTo(stream);
        }
    }


    @Override
    public boolean isExtended() {
        return true;
    }
}