package Files;

import Entries.Record;
import Files.OverflowArea;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.function.Function;

public class PrimaryArea extends RecordFile {
    @Getter
    OverflowArea overflowArea;
    public PrimaryArea(String fileName) throws IOException {
        super(fileName);
        overflowArea = new OverflowArea(fileName+".overflow");
    }

    public Record findRecordAtBlock(int block, int key) throws IOException {
        return findRecordAtBlock(block, key, (record -> record));
    }
    public Record findRecordAtBlock(int block, int key, Function<Record, Record> action) throws IOException {
        readBlock(block);
        for(int i=0; i<blockCapacity; i++){
            Record tmp = getFromBlock(i);
                if (tmp.getKey() == key) {
                    Record newRec = action.apply(tmp);
                    if(!tmp.equals(newRec)){
                       insertAtBlock(newRec, i);
                       writeBlock(block);
                    }
                    return tmp;
                }
                if (i < blockCapacity - 1) {
                    if (getFromBlock(i + 1).getKey() > key) {
                        if(tmp.hasOverflowArea())
                            return overflowArea.search(key, tmp, action);
                    }
                }
                else{
                    if(tmp.hasOverflowArea()){
                        return overflowArea.search(key, tmp, action);
                        }
                }
        }
        return null;

    }
    public void close() throws IOException {
        super.close();
        overflowArea.close();

    }

    public void insertRecordAtBlock(int block, Record record) throws IOException {
        readBlock(block);
        for(int i=0; i<blockCapacity; i++){
            Record tmp = getFromBlock(i);
            if(tmp.empty()){
                insertAtBlock(record,i);
                break;
            } else if (i<blockCapacity-1) {
                Record next = getFromBlock(i+1);
                if(!next.empty() && next.getKey() > record.getKey()){
                    int pos = overflowArea.insert(tmp, record);
                    tmp.setOverflowAreaIndex(pos);
                    insertAtBlock(tmp,i);
                    break;
                }
            }
            else{
                int pos = overflowArea.insert(tmp, record);
                tmp.setOverflowAreaIndex(pos);
                insertAtBlock(tmp,i);
                break;
            }
        }
        goToBlock(block);
        writeBlock();
    }
}
