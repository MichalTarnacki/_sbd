//
// Created by user on 28.10.23.
//

#ifndef PROJ_SEQUENTIALFILEANDBUFFER_H
#define PROJ_SEQUENTIALFILEANDBUFFER_H
#include "RecordType.h"
#define BUFFER_PTR_SIZE sizeof (buffer)
#define BUFFER_CAPACITY 10
#define BUFFER_DATA_SIZE BUFFER_CAPACITY*RECORD_SIZE

typedef struct buffer_{
    void*startLoc;
    int currentPos;
    int noOfRec;
    int blockReadNo;
    int blockWriteNo;
} buffer;

typedef struct sequential_file_{
    void*filePtr;
    char*name;
    buffer *buff;
} sequential_file;

#include <stdio.h>
#include "RecordType.h"
#include <string.h>

size_t BlockRead(sequential_file*blockPtr);
void* ReadOneRecord(sequential_file *file);
size_t ForceBlockWrite(sequential_file *file);
int BlockPutRecordAt(sequential_file*blockPtr, const void *record, unsigned position);
void BlockInsertRecord(sequential_file *file, const void *rec);

#define DO_OVERWRITE 0
#define DONT_OVERWRITE 1

void* InitFile(char *file, void*bufferLoc, int mode);
void CloseFile(sequential_file * db);

void FillBufferFromFile(buffer *buff, sequential_file*file);
buffer *AllocateBuffer();
void *NewFileBuffer(sequential_file*file);
void RemoveBuffer(buffer*buff);
void RemoveFileBuffer(sequential_file*file);

void PrintFile2(sequential_file *file);
#endif //PROJ_SEQUENTIALFILEANDBUFFER_H
