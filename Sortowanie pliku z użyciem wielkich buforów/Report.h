//
// Created by user on 02.11.23.
//

#ifndef PROJ_REPORT_H
#define PROJ_REPORT_H
#include <stdio.h>
typedef struct report_ {
    int readNo;
    int writeNo;
    int sortNo;
    int cycles;
} report;

report NewReport();
report ReportSum(report a, report b);
void PrintReport(report rep);
#endif //PROJ_REPORT_H
