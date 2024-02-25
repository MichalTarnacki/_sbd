//
// Created by user on 28.10.23.
//

#ifndef PROJ_FIRSTSTAGE_H
#define PROJ_FIRSTSTAGE_H

#include "QuickSortForBuffers.h"
#include "Config.h"
#include "HelpFunctions.h"
#include <sys/stat.h>

#define FILENAME_SIZE sizeof("tmp/4294967295.Run")
#define RUN_FORMAT int
#define FILENAME_FORMAT "tmp/%u.Run"
#include "Report.h"
#include "StageMode.h"

void LoadNBuffers(void **buffers, sequential_file *file);
void SortNBuffers(void **buffers);
void SaveNBuffersAsRun(void **buffers, sequential_file *file);
report FirstStage(sequential_file*file, int mode);

#endif //PROJ_FIRSTSTAGE_H
