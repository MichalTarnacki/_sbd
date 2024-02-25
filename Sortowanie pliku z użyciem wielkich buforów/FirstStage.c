//
// Created by user on 28.10.23.
//

#include "FirstStage.h"

void LoadNBuffers(void **buffers, sequential_file *file) {
    for(int i=0; i<N_BUFFERS; i++) {
        buffer *buff = buffers[i];
        FillBufferFromFile(buff, file);
    }
}

void SortNBuffers(void **buffers) {
    int recNum = 0;
    for (int z = 0; z < N_BUFFERS; z++)
        recNum += ((buffer *) buffers[z])->noOfRec;
    BuffSort(buffers, 0, recNum-1);
}
void SaveNBuffersAsRun(void **buffers, sequential_file *file) {
    //Crete filePtr with run no
    for(int i=0; i<N_BUFFERS; i++){
        file->buff = buffers[i];
        ForceBlockWrite(file);
    }
}

report FirstStage(sequential_file*file, int mode){
    report rep = NewReport();
    fseek(file->filePtr, 0L, SEEK_SET);
    void **buffers = (void*) malloc(N_BUFFERS * sizeof (buffer*));
    for(int i=0; i<N_BUFFERS; i++)
        buffers[i] = AllocateBuffer();

    void **files = (void *) malloc((N_BUFFERS-1)*sizeof (sequential_file*));
    for(int i=0; i<N_BUFFERS-1; i++)
        files[i]=NULL;

    //Repeat steps 1. i 2. until you reach the end of filePtr.
    RUN_FORMAT runNo=0;

    while (!feof(file->filePtr)) {

        //1.Read the first nb records from the filePtr into the buffers
        LoadNBuffers(buffers, file);
        //and sort them using an efficient in-memory sorting method (QuickSort, HeapSort, …), creating a run of length nb.
        SortNBuffers(buffers);
        if(!files[runNo%(N_BUFFERS-1)]){
            void* filename = (void*)malloc(FILENAME_SIZE);
            sprintf(filename, FILENAME_FORMAT, runNo);
            files[runNo%(N_BUFFERS-1)]= InitFile(filename, NULL, DO_OVERWRITE);
            free(filename);
        }
        //2.Write the run on the disk
        SaveNBuffersAsRun(buffers, files[runNo % (N_BUFFERS-1)]);
        runNo++;
    }

    for(int i=0; i<N_BUFFERS-1; i++) {
        if(files[i]) {
            CloseFile(files[i]);
        }

    }
    for(int i=0; i<N_BUFFERS; i++) {
        buffer *tmp = buffers[i];
        rep.readNo += tmp->blockReadNo;
        rep.writeNo += tmp->blockWriteNo;

        RemoveBuffer(buffers[i]);
    }
    rep.sortNo += runNo;

    free(files);
    free(buffers);

    return rep;
}