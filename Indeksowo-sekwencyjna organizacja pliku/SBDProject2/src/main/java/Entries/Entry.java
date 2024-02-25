package Entries;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public abstract class Entry {
    int key = Integer.MAX_VALUE;
    public Entry(int key){
        this.key=key;
    }
    public Entry(){}

    public abstract byte[] toByte();

    public boolean empty(){
        return key==Integer.MAX_VALUE;
    }

    public int getKey(){
        return key & Integer.MAX_VALUE;
    }
    public void setKey(int key){
        int tmp = key & Integer.MAX_VALUE;
        this.key = key | tmp;
    }
    public void setDeleted(boolean deleted){
        if(deleted)
            key = key | Integer.MIN_VALUE;
        else
            key = key & Integer.MAX_VALUE;
    }
    public boolean isDeleted(){
        return (key & Integer.MIN_VALUE) == Integer.MIN_VALUE;
    }
}
