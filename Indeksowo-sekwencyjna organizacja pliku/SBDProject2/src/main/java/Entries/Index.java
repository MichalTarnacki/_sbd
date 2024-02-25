package Entries;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

@Getter
public class Index extends Entry {
    @Setter
    int pageNo = 0;
    public Index(int key, int pageNo){
        super(key);
        this.pageNo = pageNo;
    }

    public Index(byte[] byteArray){
        super();
        ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
        key = byteBuffer.getInt();
        pageNo = byteBuffer.getInt();
    }

    public Index() {
        super();
    }

    @Override
    public String toString(){
        return empty() ? "empty" : "key:" + getKey() + ", pageNo:" + pageNo;
    }
    @Override
    public byte[] toByte() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Index.sizeOf());
        byteBuffer.putInt(key);
        byteBuffer.putInt(pageNo);
        return byteBuffer.array();
    }

    public static int sizeOf(){
        return 2*Integer.BYTES;
    }
}
