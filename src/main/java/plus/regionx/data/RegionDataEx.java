package plus.regionx.data;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;


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
    public boolean isExtended() {
        return true;
    }
}