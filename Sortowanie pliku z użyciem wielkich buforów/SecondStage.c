//
// Created by user on 28.10.23.
//

#include "SecondStage.h"

void LoadFilesIntoBuffersAndHeap(void **files, void *heap, int *whichBuffer, int filesRead) {
    for(int i=0; i<filesRead; i++){
        sequential_file *fil = files[i];
        BlockRead(fil);
        //load all first records from buffers into array
        void* rec = ReadOneRecord(fil);
        memcpy(heap + (unsigned long) i * RECORD_SIZE, rec, RECORD_SIZE);
        whichBuffer[i] = i;
    }
}

int GreaterExist(void **buffers, int filesRead, void *heap, void **files) {
    for (int i = 0; i < filesRead; i++) {
        buffer *tmp = buffers[i];
        if(tmp->currentPos==BUFFER_CAPACITY)
            BlockRead(files[i]);
        if (!(tmp->noOfRec<BUFFER_CAPACITY&&tmp->currentPos==tmp->noOfRec)) {
            if ((CmpRec(tmp->startLoc + (tmp->currentPos)*RECORD_SIZE, heap)>=0))
                return i;
        }
    }
    return -1;
}

void OverwriteHeapTopByNewRecord(void **buffers, int *whichBuffer, void **files, int filesRead, void *heap, void *maxRec,
                            sequential_file *resultFile) {
    while (1) {
        buffer *tmp = buffers[whichBuffer[0]];
        if (tmp->currentPos==BUFFER_CAPACITY)
            BlockRead(files[whichBuffer[0]]);
        else if (tmp->noOfRec<BUFFER_CAPACITY&&tmp->currentPos==tmp->noOfRec) {
            memcpy(heap, maxRec, RECORD_SIZE);
            return;
        }
        else if (CmpRec(tmp->startLoc + (tmp->currentPos) * RECORD_SIZE, heap) < 0) {
            memcpy(heap, maxRec, RECORD_SIZE);
            int idx = GreaterExist(buffers, filesRead, heap, files);
            if (idx != -1)
                return;
            else {
                for(int i=0; i<filesRead; i++){
                    memcpy(heap, maxRec, RECORD_SIZE);
                    Heapify(heap, whichBuffer, filesRead, 0);
                    if(!CmpRec(heap, maxRec)) {
                        break;
                    }
                    BlockInsertRecord(resultFile, heap);
                }
                for (int i = 0; i < filesRead; i++) {
                    sequential_file *fil = files[i];
                    void *tmpRec = ReadOneRecord(fil);
                    //load all first records from buffers into array
                    memcpy(heap + (unsigned long) i * RECORD_SIZE, tmpRec ? tmpRec : maxRec, RECORD_SIZE);
                    whichBuffer[i] = i;
                }
                BuildHeap(heap, whichBuffer, filesRead);
                return;

            }
        }
        else {
            void*tmpRec = ReadOneRecord(files[whichBuffer[0]]);
            memcpy(heap, tmpRec, RECORD_SIZE);
            return;
        }
    }
}

sequential_file* MergeFiles(void**files, void **buffers, int filesRead){
    void* heap = (void*)malloc((N_BUFFERS-1)*RECORD_SIZE);
    int * whichBuffer = (int*)malloc((N_BUFFERS - 1) * sizeof (int));
    void* maxRec = GenerateRecord((RADIUS_TYPE) ~0, (HEIGHT_TYPE) ~0);
    sequential_file * resultFile = InitFile(TEMP_FILE, buffers[N_BUFFERS - 1], DO_OVERWRITE);

    LoadFilesIntoBuffersAndHeap(files, heap, whichBuffer, filesRead);
    //and make heap from it
    BuildHeap(heap, whichBuffer, filesRead);
    while(1) {
        BlockInsertRecord(resultFile, heap);
        OverwriteHeapTopByNewRecord(buffers, whichBuffer, files, filesRead, heap, maxRec, resultFile);
        Heapify(heap, whichBuffer, filesRead, 0);
        if(!CmpRec(heap, maxRec))
            break;
    }
    // Flush whats left in buffer
    ForceBlockWrite(resultFile);
    free(maxRec);
    free(heap);
    free(whichBuffer);
    return resultFile;
}

int LoadFiles(DIR *d, void **files, void **buffers) {
    rewinddir(d);
    int filesRead=0;
    //open first n-1 files
    struct dirent *dir;
    while ((dir = readdir(d)) != NULL && filesRead<N_BUFFERS-1) {
        if (dir->d_type == DT_REG) {
            char* folder = (char*) malloc(strlen(dir->d_name)+sizeof(TEMP_FOLDER));
            strcpy(folder, TEMP_FOLDER);
            buffer*tmp = buffers[filesRead];
            tmp->currentPos=0;
            tmp->noOfRec=0;
            files[filesRead] = InitFile(strcat(folder, dir->d_name), tmp, DONT_OVERWRITE);
            if(!files[filesRead])
                continue;
            filesRead++;
            free(folder);
        }
    }
    return filesRead;
}

void RemoveMergedFiles(void** files, int filesRead, char* tmpFilename){
    sequential_file *tmp;
    for(int j=0; j<filesRead; j++){
        tmp=files[j];
        strcpy(tmpFilename, tmp->name);
        CloseFile(files[j]);
        unlink(tmpFilename);
    }
}


void DistributeIntoTapes(void **files, void **buffers, sequential_file* result){
    //Reset result file
    fseek(result->filePtr, 0L, SEEK_SET);
    result->buff->currentPos=0;
    result->buff->noOfRec=0;

    for(int i=0; i<N_BUFFERS-1; i++)
        files[i]=NULL;

    //Distribute to tapes
    int runNo=0;
    void* lastRecord = GenerateRecord((RADIUS_TYPE)0, (HEIGHT_TYPE)0);
    BlockRead(result);

    while(1){
        void *tmpRec = ReadOneRecord(result);
        if(!tmpRec)
            break;

        if(lastRecord)
            if (!(CmpRec(tmpRec, lastRecord) >= 0)) {
                runNo++;
            }

        //if file does not exist
        if(!files[runNo%(N_BUFFERS-1)]){
            void* filename = (void*)malloc(FILENAME_SIZE);
            sprintf(filename, FILENAME_FORMAT, runNo);
            files[runNo%(N_BUFFERS-1)]= InitFile(filename, buffers[runNo%(N_BUFFERS-1)], DO_OVERWRITE);
            free(filename);
        }


        BlockInsertRecord(files[runNo % (N_BUFFERS - 1)], tmpRec);
        memcpy(lastRecord, tmpRec, RECORD_SIZE);

    }
    free(lastRecord);

    for(int i=0; i<N_BUFFERS-1; i++) {
        if(files[i]) {
            ForceBlockWrite(files[i]);
            CloseFile(files[i]);
        }
    }

    CloseFile(result);
    unlink(TEMP_FILE);
}

void ResetNBuffers(void** buffers){
    for(int i=0; i<N_BUFFERS; i++){
        buffer *tmp = buffers[i];
        tmp->currentPos=0;
        tmp->noOfRec=0;
    }
}


report SecondStage(int mode){
    report rep = NewReport();
    // allocate memory for N_BUFFERS files and N_BUFFERS buffers
    void **files = (void *) malloc(N_BUFFERS*sizeof (sequential_file*));
    void **buffers = (void**) malloc(N_BUFFERS * sizeof (buffer*));
    for(int i=0; i<N_BUFFERS; i++)
        buffers[i] = AllocateBuffer();

    char* tmpFilename = (char*) malloc(sizeof(TEMP_FOLDER) + 256);
    DIR *d = opendir(TEMP_FOLDER);

    int cycles = 0;
    while (1) {
        ResetNBuffers(buffers);
        sequential_file *result;
        // 5. Repeat step 4 for subsequent runs from the filePtr until you reach the end of the filePtr
        int filesRead = LoadFiles(d, files, buffers);
        //4. Merge the first n-1 runs using the n-th buffer to create the output run, and write the output run to disk.
        if(filesRead>1)
            result = MergeFiles(files,buffers, filesRead);
        //6. Repeat steps 4 and 5 until only one run remains
        else {
            strcpy(tmpFilename, ((sequential_file*) files[0])->name);
            CloseFile(files[0]);
            break;
        }
        ResetNBuffers(buffers);
        if(mode==DEBUG) {
            PrintFile2(result);
            printf("Press enter to continue\n");
            getchar();
        }
        RemoveMergedFiles(files, filesRead, tmpFilename);
        DistributeIntoTapes(files,buffers,result);
        cycles++;
    }
    rename(tmpFilename, NEW_FILENAME);
    rep.cycles+=cycles;
    closedir(d);

    for(int l=0; l<N_BUFFERS; l++) {
        buffer *tmp = buffers[l];
        rep.writeNo+=tmp->blockWriteNo;
        rep.readNo+=tmp->blockReadNo;
        RemoveBuffer(buffers[l]);
    }
    free(tmpFilename);
    free(buffers);
    free(files);
    printf("Sort successful\n");
    return rep;
}
