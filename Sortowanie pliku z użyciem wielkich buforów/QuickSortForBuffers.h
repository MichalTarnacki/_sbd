//
// Created by user on 28.10.23.
//

#ifndef PROJ_QUICKSORTFORBUFFERS_H
#define PROJ_QUICKSORTFORBUFFERS_H

#include <stdlib.h>
#include "SequentialFileAndBuffer.h"

int Split(void **buffers, const int l, const int r);
void BuffSort(void **buffers, const int l, const int r);
void* TranslateLocation(void **buffers, int idx);
#endif //PROJ_QUICKSORTFORBUFFERS_H
