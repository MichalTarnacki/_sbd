//
// Created by user on 28.10.23.
//

#include "HelpFunctions.h"

int GenerateFile(sequential_file *file, unsigned records_num, void * generateFunction()){
    NewFileBuffer(file);

    if(!records_num)
        return 0;
    while(records_num--){
        void *randomRecord = generateFunction();
        BlockInsertRecord(file, randomRecord);
        free(randomRecord);
    }
    ForceBlockWrite(file);


    RemoveFileBuffer(file);
    return 1;
}

void PrintRecord(const void * rec, int *no){
    ID_TYPE id = *((ID_TYPE*)(rec+ID_OFFSET));
    RADIUS_TYPE r = *((RADIUS_TYPE*)(rec+RADIUS_OFFSET));
    HEIGHT_TYPE h = *((HEIGHT_TYPE*)(rec+HEIGHT_OFFSET));
    printf("no.%d. Capacity:%.03f Radius:%hhu, Height:%hhu\n", (*no)++, id, r, h);
}

void PrintFile(sequential_file* file){
    NewFileBuffer(file);
    fseek(file->filePtr, 0L, SEEK_SET);
    int no=0;
    unsigned recordsRead;
    while (1){
        recordsRead = BlockRead(file);
        for(int i=0; i<recordsRead; i++){
            PrintRecord(file->buff->startLoc + i * RECORD_SIZE, &no);
        }
        if(recordsRead < BUFFER_CAPACITY) {
            RemoveFileBuffer(file);
            return;
        }
    }
}