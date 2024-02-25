//
// Created by user on 28.10.23.
//

#ifndef PROJ_RECORDTYPE_H
#define PROJ_RECORDTYPE_H

#define ID_TYPE double
#define ID_SIZE sizeof(ID_TYPE)
#define ID_OFFSET 0

#define RADIUS_TYPE unsigned char
#define RADIUS_SIZE sizeof(RADIUS_TYPE)
#define RADIUS_OFFSET (ID_OFFSET+ID_SIZE)
#define RADIUS_SCANF "%hhi"

#define HEIGHT_TYPE unsigned char
#define HEIGHT_SIZE sizeof(HEIGHT_TYPE)
#define HEIGHT_OFFSET (RADIUS_OFFSET+RADIUS_SIZE)
#define HEIGHT_SCANF "%hhi"

#define RECORD_SIZE (HEIGHT_OFFSET+HEIGHT_SIZE)

#include <string.h>
#include <stdlib.h>
#include <math.h>

ID_TYPE RecCapacity(RADIUS_TYPE radius, HEIGHT_TYPE height);
ID_TYPE CmpRec(const void *a, const void *b);
void SwapRec(const void *a, const void *b);


#endif //PROJ_RECORDTYPE_H
