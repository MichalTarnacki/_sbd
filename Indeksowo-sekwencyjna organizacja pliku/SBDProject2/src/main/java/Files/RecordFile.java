package Files;

import Entries.Record;
import Files.File;
import lombok.SneakyThrows;

import java.io.IOException;

public class RecordFile extends File {
    public RecordFile(String fileName) throws IOException {
        super(fileName, Record.sizeOf());
    }
    @Override
    public void removeFromBlock(int n)  {
        super.removeFromBlock(n, new Record().toByte());
    }
    @Override
    public Record getFromBlock(int n){
        return new Record(getBytesFromBlock(n));
    }

    @SneakyThrows
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i< getNumberOfEntries(); i++){
            Record rec = (Record) getNth(i);
            s.append(i).append(":").append(!rec.empty() ? rec.toString(): "empty").append("\n");
        }
        return s.toString();
    }
}
