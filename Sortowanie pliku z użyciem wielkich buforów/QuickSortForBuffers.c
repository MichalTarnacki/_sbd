//
// Created by user on 28.10.23.
//

#include "QuickSortForBuffers.h"

void* TranslateLocation(void **buffers, int idx){
    buffer *tmp = buffers[idx/(BUFFER_CAPACITY)];
    return tmp->startLoc + (idx % (BUFFER_CAPACITY)) * RECORD_SIZE;
}

//To modify
int Split(void **buffers, const int l, const int r){
    int idx = (l+r)/2;
    void* tmpRec = (void*)malloc(RECORD_SIZE);
    memcpy((void*)tmpRec, TranslateLocation(buffers, idx), RECORD_SIZE);
    SwapRec(TranslateLocation(buffers, idx), TranslateLocation(buffers, r));
    int pos = l;
    for(int i=l; i<r; i++){
        if(CmpRec(TranslateLocation(buffers, i), tmpRec) < 0){
            SwapRec(TranslateLocation(buffers, i), TranslateLocation(buffers, pos));
            pos +=1;
        }
    }
    free(tmpRec);
    SwapRec(TranslateLocation(buffers, pos), TranslateLocation(buffers, r));
    return pos;
};

void BuffSort(void **buffers, const int l, const int r){
    if(l>=r)
        return;
    int i = Split(buffers, l, r);
    BuffSort(buffers, l, i-1);
    BuffSort(buffers, i + 1, r);
}