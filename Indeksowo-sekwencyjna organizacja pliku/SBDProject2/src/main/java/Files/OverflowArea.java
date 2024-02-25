package Files;

import Entries.Record;
import java.io.IOException;
import java.util.function.Function;

public class OverflowArea extends RecordFile {
    int lastRecIndex = -1;
    public OverflowArea(String fileName) throws IOException {
        super(fileName);
    }

    public Record search(int key, Record record) throws IOException{
        return search(key, record, record1 -> record1);
    }

    public Record search(int key, Record record, Function<Record, Record> action) throws IOException {
        Record tmp = record;
        do {
            int pos = tmp.getOverflowAreaIndex();
            tmp = (Record) getNth(pos);
            if (tmp.getKey() == key) {

                Record newRec = action.apply(tmp);
                if(!tmp.equals(newRec)){
                    writeNth(pos, newRec);
                }

                return tmp;
            }
        }
        while (tmp.hasOverflowArea());
        return null;
    }

    public int insert(Record record, Record newRecord) throws IOException {

        if (!record.hasOverflowArea()) {
            writeLast(newRecord);
            return getLastElementPos();
        }

        Record prev = record;
        Record next = (Record) getNth(prev.getOverflowAreaIndex());
        if(newRecord.getKey()<next.getKey()){
            newRecord.setOverflowAreaIndex(prev.getOverflowAreaIndex());
            writeLast(newRecord);
            return getLastElementPos();
        }

        int prevIndex=record.getOverflowAreaIndex();
        while(true) {
            if(newRecord.getKey() >= next.getKey()) {
                if (next.hasOverflowArea()) {
                    prevIndex = prev.getOverflowAreaIndex();
                    prev = next;
                    next = (Record) getNth(next.getOverflowAreaIndex());
                } else {
                    writeLast(newRecord);
                    next.setOverflowAreaIndex(getLastElementPos());
                    writeNth(prev.getOverflowAreaIndex(), next);
                    return record.getOverflowAreaIndex();
                }
            }
            else {
                newRecord.setOverflowAreaIndex(prev.getOverflowAreaIndex());
                writeLast(newRecord);
                prev.setOverflowAreaIndex(getLastElementPos());
                writeNth(prevIndex, prev);

                return record.getOverflowAreaIndex();
            }
        }
    }
    public void writeLast(Record newRecord) throws IOException {
        writeNth(getLastElementPos()+1, newRecord);
        lastRecIndex++;
    }
    @Override
    public int getLastElementPos() throws IOException {
        if(lastRecIndex<0)
            lastRecIndex = super.getLastElementPos();
        return lastRecIndex;
    }
    public boolean full() throws IOException {
        return getLastElementPos() == blockCapacity*getNoOfBlocks()-1;
    }
}
