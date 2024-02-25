//
// Created by user on 30.10.23.
//

#ifndef PROJ_HEAPFORBUFFERS_H
#define PROJ_HEAPFORBUFFERS_H
#include "RecordType.h"

void Heapify(void *heap, int*which_buffer, int n, int i);
void BuildHeap(void *heap, int*which_buffer, int n);

#endif //PROJ_HEAPFORBUFFERS_H
