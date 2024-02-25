package Files;

import Entries.Entry;
import Entries.Index;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public abstract class File {
        @Getter
        java.io.RandomAccessFile file;
        @Getter
        final int blockCapacity = 4; //Records
        @Getter
        byte[] block;
        @Getter
        int bytesInBlock;
        int lastBlockRead = -1;
        int entrySize;
        @Getter
        @Setter
        int noOfReads=0;
        @Getter
        @Setter
        int noOfWrites=0;
        public File(String fileName, int entrySize) throws IOException {
            file = new java.io.RandomAccessFile(fileName, "rw");
            this.entrySize = entrySize;
            block = new byte[blockCapacity * entrySize];
            clearBlock();
        }
        public final int getNoOfBlocks() throws IOException {
            return (int) (file.length()/block.length);
        }
        public final int getCurrentBlock() throws IOException {
            return (int) (file.getFilePointer()/block.length);
        }
        public final void readBlock() throws IOException {
            lastBlockRead = getCurrentBlock();
            bytesInBlock = file.read(block);
            noOfReads++;
        }
        public final void readBlock(int n) throws IOException {
            if(n != lastBlockRead || lastBlockRead == -1) {
                goToBlock(n);
                readBlock();
            }
        }
        public final void writeBlock() throws IOException {
            file.write(block);
            noOfWrites++;
        }
        public final void writeBlock(int n) throws IOException {
            goToBlock(n);
            writeBlock();
        }


        public final void goToBlock(int n) throws IOException {
            file.seek((long) n * block.length);
        }

        public final void clearBlock(){
            for(int i=0; i<blockCapacity; i++){
                removeFromBlock(i);
            }
        }
        public final void insertAtBlock(Entry entry, int pos) {
            System.arraycopy(entry.toByte(), 0, block, pos*entrySize, entrySize);
        }

        public abstract void removeFromBlock(int n);
        protected final void removeFromBlock(int n, byte[] bytes) {
            System.arraycopy(bytes, 0, block, n*entrySize, entrySize);
        }
        public abstract Entry getFromBlock(int n);
        public final byte[] getBytesFromBlock(int n){
            byte[] tmp = new byte[entrySize];
            System.arraycopy(block, n*entrySize, tmp, 0, entrySize);
            return tmp;
        };
        public final Entry getNth(int n) throws IOException {
            int blockNo = n/blockCapacity;
            int position = n%blockCapacity;
            readBlock(blockNo);
            return getFromBlock(position);
        }
        public final void writeNth(int n, Entry entry) throws IOException {
            int blockNo = n/blockCapacity;
            int position = n%blockCapacity;
            if(blockNo>=getNoOfBlocks()){
                goToBlock(blockNo);
                clearBlock();
                insertAtBlock(entry, position);
                writeBlock();
                return;
            }
            readBlock(blockNo);
            insertAtBlock(entry, position);
            writeBlock(blockNo);
        }

        public final int getNumberOfEntries() throws IOException {
            return (int) (file.length()/entrySize);
        }

        public void close() throws IOException {
            file.close();
        }
        public boolean empty() throws IOException {
            return file.length()<=0;
        }
        public int getLastElementPos() throws IOException {
            int n = getNumberOfEntries()-1;
            while(getNth(n).empty()&&n>0){
                n--;
            }
            return n;
        }
        public final void resetNoOfOperations(){
            noOfReads=0;
            noOfWrites=0;
        }
}
