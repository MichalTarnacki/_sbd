//
// Created by user on 28.10.23.
//
#include "RecordType.h"

ID_TYPE RecCapacity(RADIUS_TYPE radius, HEIGHT_TYPE height){
    return (pow(radius,2)*height)*M_PI;
}

ID_TYPE CmpRec(const void *a, const void *b){
    return *(ID_TYPE*)(a+ID_OFFSET) - *(ID_TYPE*)(b+ID_OFFSET);
}

void SwapRec(const void *a, const void *b){
    if( !memcmp(a+ID_OFFSET,b+ID_OFFSET,ID_SIZE))
        return;
    void *tmp = (void *) malloc(RECORD_SIZE);
    memcpy((void *) tmp, (void *) a, RECORD_SIZE);
    memcpy((void *) a, (void *) b, RECORD_SIZE);
    memcpy((void *) b, tmp, RECORD_SIZE);
    free(tmp);
}