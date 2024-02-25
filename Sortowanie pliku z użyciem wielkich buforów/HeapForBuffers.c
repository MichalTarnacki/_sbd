//
// Created by user on 30.10.23.
//

#include "HeapForBuffers.h"

void Heapify(void *heap, int*which_buffer, int n, int i){
    int smallest = i;
    int l = 2 * i + 1;
    int r = 2 * i + 2;

    if (l < n)
        if(CmpRec(heap + (unsigned long) l * RECORD_SIZE, heap + (unsigned long) smallest * RECORD_SIZE) < 0)
            smallest = l;

    if (r < n)
        if(CmpRec(heap + (unsigned long) r * RECORD_SIZE, heap + (unsigned long) smallest * RECORD_SIZE) < 0)
            smallest = r;

    if (smallest != i) {
        int tmp = which_buffer[i];
        which_buffer[i] = which_buffer[smallest];
        which_buffer[smallest] = tmp;
        SwapRec(heap + (unsigned long) i * RECORD_SIZE, heap + (unsigned long) smallest * RECORD_SIZE);

        Heapify(heap, which_buffer , n, smallest);
    }
}

void BuildHeap(void *heap, int*which_buffer, int n){
    int startIdx = (n / 2) - 1;

    for (int i = startIdx; i >= 0; i--)
        Heapify(heap, which_buffer ,n, i);
}