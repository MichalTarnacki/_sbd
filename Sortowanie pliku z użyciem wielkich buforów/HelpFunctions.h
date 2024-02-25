//
// Created by user on 28.10.23.
//

#ifndef PROJ_HELPFUNCTIONS_H
#define PROJ_HELPFUNCTIONS_H

#include "SequentialFileAndBuffer.h"
#include <stdlib.h>

int GenerateFile(sequential_file *file, unsigned records_num, void * generateFunction());
void PrintRecord(const void * rec, int *no);
void PrintFile(sequential_file* file);

#endif //PROJ_HELPFUNCTIONS_H
