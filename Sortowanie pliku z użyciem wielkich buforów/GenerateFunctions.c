//
// Created by user on 28.10.23.
//

#include "GenerateFunctions.h"

void *GenerateRecord(RADIUS_TYPE radius, HEIGHT_TYPE height){
    void *ptr = (void*)malloc(RECORD_SIZE);
    ID_TYPE capacity = RecCapacity(radius, height);
    memcpy(ptr+ID_OFFSET, &capacity, ID_SIZE);
    memcpy(ptr+RADIUS_OFFSET, &radius, RADIUS_SIZE);
    memcpy(ptr+HEIGHT_OFFSET, &height, HEIGHT_SIZE);
    return ptr;
}


void *GenerateRandomRecord(){
    // ~0 - 1, 1 less than max value because of heap
    RADIUS_TYPE r = (RADIUS_TYPE) random()%((RADIUS_TYPE)~0 - 2) + 1;
    HEIGHT_TYPE h = (HEIGHT_TYPE) random()%((HEIGHT_TYPE)~0 - 2) + 1;
    return GenerateRecord(r,h);
}


void *ManualGenerateRecord(){
    RADIUS_TYPE r;
    HEIGHT_TYPE h;
    printf("Enter radius \n");
    scanf(RADIUS_SCANF, &r);
    printf("Enter height \n");
    scanf(HEIGHT_SCANF, &h);
    return GenerateRecord(r, h);
}