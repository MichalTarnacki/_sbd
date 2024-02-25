//
// Created by user on 28.10.23.
//
#include "SequentialFileAndBuffer.h"
#include "HelpFunctions.h"

size_t BlockRead(sequential_file *file){
    size_t ret = fread(file->buff->startLoc, RECORD_SIZE, BUFFER_CAPACITY, file->filePtr);
    file->buff->currentPos = 0;
    file->buff->noOfRec=ret;
    if(ret)
        file->buff->blockReadNo++;
    return ret;
}

void* ReadOneRecord(sequential_file *file){
    if(file->buff->currentPos==BUFFER_CAPACITY)
        BlockRead(file);
    if(file->buff->noOfRec<BUFFER_CAPACITY&&file->buff->currentPos==file->buff->noOfRec)
        return NULL;
    file->buff->currentPos++;
    return  file->buff->startLoc+(file->buff->currentPos-1)*RECORD_SIZE;
}

size_t ForceBlockWrite(sequential_file *file){
    size_t ret = 0;
    if(file->buff->noOfRec){
        ret = fwrite(file->buff->startLoc, RECORD_SIZE, file->buff->noOfRec, file->filePtr);
        file->buff->currentPos = 0;
        file->buff->noOfRec = 0;
        file->buff->blockWriteNo++;
    }
    return ret;
}

int BlockPutRecordAt(sequential_file*blockPtr, const void *record, unsigned position){
    if(position >= BUFFER_CAPACITY)
        return 0;
    memcpy(blockPtr->buff->startLoc + position * RECORD_SIZE, record, RECORD_SIZE);
    return 1;
}

void BlockInsertRecord(sequential_file *file, const void *rec){
    BlockPutRecordAt(file, rec, file->buff->currentPos);
    file->buff->currentPos++;
    file->buff->noOfRec++;
    //If sequential_file is full
    if(file->buff->noOfRec==BUFFER_CAPACITY)
        ForceBlockWrite(file);
}


void* InitFile(char *file, void*bufferLoc, int mode){
    sequential_file *db = (void*)malloc(sizeof(sequential_file));
    if(!db)
        return NULL;
    db->filePtr = fopen(file, mode == DO_OVERWRITE ? "w+" : "r+");
    if(!db->filePtr){
        free(db);
        return NULL;
    }
    db->buff = bufferLoc;
    db->name = (char*) malloc(strlen(file)+1);
    strcpy(db->name, file);
    return db;
}

void CloseFile(sequential_file * db){
    fclose(db->filePtr);
    free(db->name);
    free(db);
}


void FillBufferFromFile(buffer *buff, sequential_file*file){
    file->buff=buff;
    BlockRead(file);
    file->buff=NULL;
}


buffer *AllocateBuffer(){
    buffer * buff = (void*)malloc(sizeof(buffer));
    buff->startLoc = (void *) malloc(BUFFER_DATA_SIZE);
    buff->noOfRec = 0;
    buff->currentPos=0;
    buff->blockWriteNo = 0;
    buff->blockReadNo = 0;
    return buff;
}

void *NewFileBuffer(sequential_file*file){
    file->buff=AllocateBuffer();
}

void RemoveBuffer(buffer*buff){
    free(buff->startLoc);
    free(buff);
}

void RemoveFileBuffer(sequential_file *file){
    RemoveBuffer(file->buff);
    file->buff=NULL;
}

void PrintFile2(sequential_file *file){
    fseek(file->filePtr, 0L, SEEK_SET);
    file->buff->currentPos=0;
    file->buff->noOfRec=0;
    int i=0;
    printf("%s\n", file->name);
    while (1) {
        int rec = BlockRead(file);
        for(int j=0; j<rec; j++) {
            PrintRecord(file->buff->startLoc + j * RECORD_SIZE, &i);
        }

        if (rec<BUFFER_CAPACITY)
            break;
    }
    printf("\n");
}