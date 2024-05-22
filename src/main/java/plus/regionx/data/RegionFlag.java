package plus.regionx.data;


public class RegionFlag {
    final String name;
    final int id;
    private final Type type;

    private RegionFlag(String name, int id, Type type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }


    public final Type getType() {
        return type;
    }


    public final int getId() {
        return id;
    }


    @Override
    public final int hashCode() {
        return id;
    }


    public String getName() {
        return name;
    }


    @Override
    public String toString(){
        return id+(type == Type.ADVANCED ? "-e:":":")+name;
    }


    public enum Type{
        BASE, ADVANCED;
    }


    public static final class Base extends RegionFlag{
        Base(int id, String name) {
            super(name, id, Type.BASE);
        }


        public boolean getValue(RegionData data){
            return data.getFlag(id);
        }


        public void setValue(RegionData data, boolean value){
            data.setFlag(id, value);
        }
    }


    public static final class Advanced<T extends ExtendedFlagData> extends RegionFlag{
        Advanced(int id, String name) {
            super(name, id, Type.ADVANCED);
        }


        public T getValue(RegionData data){
            return (T) data.getFlagExtended(this);
        }


        public void setValue(RegionData data, T value){
            data.setFlagExtended(this, value);
        }
    }
}
