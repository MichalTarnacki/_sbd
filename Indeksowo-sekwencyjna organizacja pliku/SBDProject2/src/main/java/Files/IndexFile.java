package Files;

import Entries.Index;
import lombok.SneakyThrows;

import java.io.IOException;

public class IndexFile extends File {

    Index[] indexTable = null;
    int noOfIndexes=0;
    public IndexFile(String fileName) throws IOException {
        super(fileName, Index.sizeOf());
    }
    @Override
    public Index getFromBlock(int n){
        return new Index(getBytesFromBlock(n));
    }
    @Override
    public void removeFromBlock(int n) {
        super.removeFromBlock(n, new Index().toByte());
    }

    public void loadToRam() throws IOException {
        file.seek(0);
        int j=0;
        indexTable = new Index[getNoOfBlocks()*blockCapacity];
        for(int i=0; i<getNoOfBlocks(); i++){
            readBlock();
            for(int l=0; l<blockCapacity; l++){
                indexTable[j]=getFromBlock(l);
                if(!indexTable[j++].empty())
                    noOfIndexes++;
            }
        }
    }

    //Działa
    public int findBlock(int key) throws IOException { //bisection

        if(indexTable==null){
            loadToRam();
        }
        int startIdx = 0;
        int endIdx = noOfIndexes-1;
        while (startIdx<=endIdx) {
            int mid = (endIdx+startIdx)/2;
            Index index = indexTable[mid];
            int nextKey = mid < noOfIndexes-1 ? indexTable[mid + 1].getKey(): Integer.MAX_VALUE;
            if (index.getKey() <= key && key < nextKey) {
                return index.getPageNo();
            } else if (key >= nextKey) {
                startIdx = mid+1;
            }
            else {
               endIdx = mid-1;
            }
        }
        return -1;
    }
    @SneakyThrows
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i = 0; i< getNumberOfEntries(); i++){
            Index idx = (Index) getNth(i);
            s.append(idx.toString()).append("\n");
        }
        return s.toString();
    }
}
