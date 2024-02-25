//
// Created by user on 28.10.23.
//

#ifndef PROJ_SECONDSTAGE_H
#define PROJ_SECONDSTAGE_H

#include "Config.h"
#include "HelpFunctions.h"
#include <dirent.h>
#include "SequentialFileAndBuffer.h"
#include "FirstStage.h"
#include "HeapForBuffers.h"
#include "GenerateFunctions.h"
#include <unistd.h>
#include "StageMode.h"

report SecondStage(int mode);

#endif //PROJ_SECONDSTAGE_H
