package Entries;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
@Getter
public class Record extends Entry {
    private double capacity= 0;
    private char height=0;
    private char radius=0;
    @Setter
    private int overflowAreaIndex = -1;


    public Record(int key, char radius, char height){
        super(key);
        this.height = height;
        this.radius = radius;
        setCapacity();
    }
    public Record(Record rec){
        super(rec.getKey());
        this.height = rec.getHeight();
        this.radius = rec.getRadius();
        this.overflowAreaIndex = rec.getOverflowAreaIndex();
        setCapacity();
    }
    public Record(){
        super();
    }

    public Record(byte[] byteArray){
        super();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        key = byteBuffer.getInt();
        capacity = byteBuffer.getDouble();
        radius = byteBuffer.getChar();
        height = byteBuffer.getChar();
        overflowAreaIndex = byteBuffer.getInt();
    }

    public void setHeight(char height) {
        this.height = height;
        setCapacity();
    }

    public void setRadius(char radius) {
        this.radius = radius;
        setCapacity();
    }

    private void setCapacity(){
        this.capacity = (double) 1/3*Math.PI*height*Math.pow(radius, 2);
    }

    @Override
    public String toString(){
        String tmp;
        if(isDeleted())
            tmp=", deleted";
        else if(getKey()==0)
            tmp=", initial";
        else
            tmp = (", capacity:" + capacity +
                    ", radius:" + (int)radius + ", height:" + (int)height);
        return "index:" + getKey() + tmp + (overflowAreaIndex >=0 ? ", overflow:" + overflowAreaIndex: "");
    }

    @Override
    public byte[] toByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Record.sizeOf());
        byteBuffer.putInt(key);
        byteBuffer.putDouble(capacity);
        byteBuffer.putChar(radius);
        byteBuffer.putChar(height);
        byteBuffer.putInt(overflowAreaIndex);
        return byteBuffer.array();
    }

    public static int sizeOf(){
        return 2*Integer.BYTES + Double.BYTES + Character.BYTES*2;
    }

    public static Record randomRecord(int index){
        return new Record(index, (char) (Math.random()%(Character.MAX_VALUE-2)+1), (char) (Math.random()%(Character.MAX_VALUE-2)+1));
    }

    public int compare(Record obj1, Record  obj2){
        return Double.compare(obj1.getCapacity(), obj2.getCapacity());
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Record){
            return this.getKey() == ((Record) o).getKey()
                    && this.getHeight() == ((Record) o).getHeight()
                    && this.getCapacity() == ((Record) o).getCapacity()
                    && this.getRadius() == ((Record) o).getRadius()
                    && this.getOverflowAreaIndex() == ((Record) o).getOverflowAreaIndex()
                    && this.isDeleted() == ((Record) o).isDeleted();
        }
        return false;

    }
    public boolean hasOverflowArea(){
        return overflowAreaIndex >=0;
    }
}
