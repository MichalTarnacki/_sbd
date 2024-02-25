cmake_minimum_required(VERSION 3.25)
project(proj C)

set(CMAKE_C_STANDARD 23)

add_executable(proj main.c GenerateFunctions.c GenerateFunctions.h RecordType.h SequentialFileAndBuffer.h SequentialFileAndBuffer.c HelpFunctions.c HelpFunctions.h Config.h RecordType.c QuickSortForBuffers.c QuickSortForBuffers.h FirstStage.c FirstStage.h SecondStage.c SecondStage.h HeapForBuffers.c HeapForBuffers.h Report.h Report.c StageMode.h)
target_link_libraries(proj m)

set(GCC_COVERAGE_COMPILE_FLAGS "-Wall -Wextra -Wconversion")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${GCC_COVERAGE_COMPILE_FLAGS}" )