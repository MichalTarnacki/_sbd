//
// Created by user on 28.10.23.
//

#ifndef PROJ_GENERATEFUNCTIONS_H
#define PROJ_GENERATEFUNCTIONS_H

#include "RecordType.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

void *GenerateRecord(RADIUS_TYPE radius, HEIGHT_TYPE height);
void *GenerateRandomRecord();
void *ManualGenerateRecord();


#endif //PROJ_GENERATEFUNCTIONS_H
