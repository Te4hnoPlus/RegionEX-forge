package plus.regionx.data;

import plus.region.data.db.RocksDataManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class RocksRegionDataCoder implements RocksDataManager.Coder<RegionData> {
    private static RocksRegionDataCoder inst;

    public static RocksRegionDataCoder instance() {
        if (inst == null) {
            inst = new RocksRegionDataCoder();
        }
        return inst;
    }


    @Override
    public byte[] code(RegionData obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            RegionData.writeTo(obj, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }


    @Override
    public RegionData encode(byte[] bytes) {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes, 0, bytes.length);
        return null;
    }
}