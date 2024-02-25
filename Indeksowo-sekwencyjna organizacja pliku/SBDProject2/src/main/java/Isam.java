import Entries.Index;
import Entries.Record;
import Files.PrimaryArea;
import Files.IndexFile;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Isam{
    public PrimaryArea file;
    public IndexFile indexFile;
    public String filename;
    @Setter
    double alfa = 0.5;
    @Setter
    double VN = 0.2;

    public Isam(String filename) throws IOException {
        this.filename = filename;
        this.file = new PrimaryArea(filename);
        this.indexFile = new IndexFile(filename + ".idx");
        initFiles();
    }

    public void resetNoOfOperations(){
        file.resetNoOfOperations();
        file.getOverflowArea().resetNoOfOperations();
        indexFile.resetNoOfOperations();
    }
    public void printNoOfOperations(){
        System.out.println("Number of operations on:");
        System.out.println("Primary area file -> read: " + file.getNoOfReads() + ", write:" + file.getNoOfWrites());
        System.out.println("Overflow area file -> read: " + file.getOverflowArea().getNoOfReads() + ", write:" + file.getOverflowArea().getNoOfWrites());
        System.out.println("Index file -> read: " + indexFile.getNoOfReads() + ", write:" + indexFile.getNoOfWrites());
    }
    public void printNoOfOperationsShort() {
        System.out.print(file.getNoOfReads()+ file.getNoOfWrites()
                            + file.getOverflowArea().getNoOfReads() + file.getOverflowArea().getNoOfWrites()
                            + indexFile.getNoOfReads() + indexFile.getNoOfWrites() + " ");
    }

    private void initFiles() throws IOException {
        if(file.empty()){
            file.writeNth(0,new Record(0, (char) 0, (char) 0));
            file.getOverflowArea().writeNth(0,new Record(0, (char) 0, (char) 0));
        }
        if(this.indexFile.empty()){
            indexFile.clearBlock();
            int j=0;
            for(int i=0; i<file.getNoOfBlocks(); i++){
                file.readBlock();
                int key = file.getFromBlock(0).getKey();
                indexFile.insertAtBlock(new Index(key,i), j++);
                if(j>= indexFile.getBlockCapacity()) {
                    indexFile.writeBlock();
                    j=0;
                    indexFile.clearBlock();
                }
            }
            if(j!=0)
                indexFile.writeBlock();
        }
    }

    public int countRecords() throws IOException {
        AtomicInteger counter = new AtomicInteger();
        forAllRecords((rec)->{
            if(!rec.empty() && !rec.isDeleted())
                counter.set(counter.get()+1);
            return null;
        });
        return counter.intValue();
    }

    public void printAll() throws IOException {
        forAllRecords((rec)->{System.out.println(rec);
            return null;
        });
    }

    public void forAllRecords(Function<Record, Void> func) throws IOException {
        forAllRecords(file, func);
    }
    private void forAllRecords(PrimaryArea file, Function<Record, Void> func) throws IOException {
        for(int i=0; i<file.getNumberOfEntries(); i++){
            Record rec = (Record) file.getNth(i);
            if(rec.empty())
                continue;
            func.apply(rec);
            while(rec.hasOverflowArea()){
                rec = (Record) file.getOverflowArea().getNth(rec.getOverflowAreaIndex());
                func.apply(rec);
            }
        }
    }
    public void insertRecord(Record record) throws IOException {
        insertRecord(record, indexFile, file);
    }
    private boolean insertRecord(Record record, IndexFile indexFile, PrimaryArea file) throws IOException {
        Record rec = null;
        int block = indexFile.findBlock(record.getKey());
        if(block>=0){
            rec = file.findRecordAtBlock(block, record.getKey());
        }

        if (rec == null) {
            file.insertRecordAtBlock(block, record);
            if(file.getOverflowArea().full()){
                reorganize();
            }
            return true;
        }
        if(rec.isDeleted()){
            record.setOverflowAreaIndex(rec.getOverflowAreaIndex());
            replaceRecord(indexFile, file, record.getKey(), record);
            return true;
        }
        System.out.println("Key already exists: " + record.getKey());
        return false;
    }

    private void replaceRecord(IndexFile indexFile, PrimaryArea file, int key, Record rec) throws IOException {
        Record found;
        if(key == rec.getKey()) {
            found = findRecord(indexFile, file, key, record ->{rec.setOverflowAreaIndex(record.getOverflowAreaIndex()); return rec;});
        }
        else {
            found = findRecord(indexFile, file,  key, record -> {Record tmp = new Record(record); tmp.setDeleted(true); return tmp;});
            if(found!=null)
                if(!insertRecord(rec, indexFile, file)){
                    System.out.println("changes reversed");
                    findRecord(indexFile, file, key, record -> found);
                };

        }
        if(found==null){
            System.out.println("Cannot replace, record given key not found: " + key);
        }
    }

    private void removeRecord(IndexFile indexFile, PrimaryArea file, int key) throws IOException {
        Record found = findRecord(indexFile, file,  key, record -> {Record tmp = new Record(record); tmp.setDeleted(true); return tmp;});
        if(found==null){
            System.out.println("Cannot remove, record given key not found: " + key);
        }
    }

    public void removeRecord(int key) throws IOException {
        removeRecord(indexFile, file, key);
    }


    public void replaceRecord(int key, Record rec) throws IOException {
        replaceRecord(indexFile, file, key, rec);
    }


    private Record findRecord(IndexFile indexFile, PrimaryArea file, int key, Function<Record, Record> action) throws IOException {
        int page = indexFile.findBlock(key);
        if (page < 0) {
            return null;
        }
        return file.findRecordAtBlock(page, key, action);
    }
    public Record findRecord(int key, Function<Record, Record> action) throws IOException {
       return findRecord(indexFile, file,  key, action);
    }
    public Record findRecord(int key) throws IOException {
        return findRecord(indexFile, file,  key, record -> record);
    }
    public void reorganize() throws IOException {
        reorganize(new PrimaryArea("tmp"), new IndexFile("tmp.idx"),
                new AtomicInteger(),  new AtomicInteger());
    }

    private void reorganize(PrimaryArea newFile, IndexFile newIndexFile,
                            AtomicInteger currNoOfRecords, AtomicInteger currNoOfIndexes ) throws IOException {
        newIndexFile.insertAtBlock(new Index(0,0), currNoOfIndexes.get());
        currNoOfIndexes.set(currNoOfIndexes.get()+1);
        forAllRecords((rec)->{
            try {
                if(!rec.isDeleted() && !rec.empty()) {
                    Record tmp = new Record(rec);
                    tmp.setOverflowAreaIndex(-1);
                    if(currNoOfRecords.get() == Math.ceil(alfa* newFile.getBlockCapacity())){
                        Index index = new Index(tmp.getKey(), newFile.getCurrentBlock()+1);
                        newIndexFile.insertAtBlock(index, currNoOfIndexes.get());
                        currNoOfIndexes.set(currNoOfIndexes.get()+1);
                        if(currNoOfIndexes.get() == newIndexFile.getBlockCapacity()){
                            newIndexFile.writeBlock();
                            currNoOfIndexes.set(0);
                            newIndexFile.clearBlock();
                        }
                        newFile.writeBlock();
                        currNoOfRecords.set(0);
                        newFile.clearBlock();
                    }
                    newFile.insertAtBlock(tmp, currNoOfRecords.get());
                    currNoOfRecords.set(currNoOfRecords.get()+1);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
        if(currNoOfRecords.get()!=0)
            newFile.writeBlock();
        if(currNoOfIndexes.get()!=0)
            newIndexFile.writeBlock();

        replaceFiles(newFile, newIndexFile);
    }

    private void replaceFiles(PrimaryArea newFile, IndexFile newIndexFile) throws IOException {
        newFile.getOverflowArea().insertAtBlock(new Record(0, (char) 0, (char) 0),0);
        for(int i=0; i<Math.ceil(newFile.getNoOfBlocks()*VN); i++){
            newFile.getOverflowArea().writeBlock();
            newFile.getOverflowArea().clearBlock();
        }
        newFile.close();
        newIndexFile.close();
        close();
        Files.move( Paths.get("tmp"), Paths.get(filename),REPLACE_EXISTING);
        Files.move(Paths.get("tmp.idx"), Paths.get(filename+".idx"),  REPLACE_EXISTING);
        Files.move( Paths.get("tmp.overflow"),Paths.get(filename + ".overflow"), REPLACE_EXISTING);

        PrimaryArea tmp = file;
        IndexFile tmp2 = indexFile;

        file = new PrimaryArea(filename);
        indexFile = new IndexFile(filename+".idx");
        setOperations(tmp, newFile, tmp2, newIndexFile);
    }

    private void setOperations(PrimaryArea oldFile, PrimaryArea newFile, IndexFile oldIndex, IndexFile newIndex){

        file.setNoOfReads(oldFile.getNoOfReads() + newFile.getNoOfReads());
        file.setNoOfWrites(oldFile.getNoOfWrites() + newFile.getNoOfWrites());
        file.getOverflowArea().setNoOfReads(oldFile.getOverflowArea().getNoOfReads() + newFile.getOverflowArea().getNoOfReads());
        file.getOverflowArea().setNoOfWrites(oldFile.getOverflowArea().getNoOfWrites() + newFile.getOverflowArea().getNoOfWrites());
        indexFile.setNoOfReads(oldIndex.getNoOfReads() + newIndex.getNoOfReads());
        indexFile.setNoOfWrites(oldIndex.getNoOfWrites() + newIndex.getNoOfWrites());
    }

    public void close() throws IOException {
        file.close();
        indexFile.close();
    }

    public void printFiles(){
        System.out.println("Index file");
        System.out.println(indexFile);
        System.out.println("Primary area");
        System.out.println(file);
        System.out.println("Overflow area");
        System.out.println(file.getOverflowArea());
    }
}