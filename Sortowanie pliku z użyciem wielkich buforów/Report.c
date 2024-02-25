//
// Created by user on 02.11.23.
//
#include "Report.h"

report NewReport(){
    report rep;
    rep.readNo=0;
    rep.writeNo=0;
    rep.sortNo=0;
    rep.cycles=0;
    return rep;
}

report ReportSum(report a, report b){
    a.readNo += b.readNo;
    a.writeNo += b.writeNo;
    a.sortNo += b.sortNo;
    a.cycles += b.cycles;
    return a;
};

void PrintReport(report rep){
    printf("Read no: %d, write no: %d, sort no: %d, stage2 cycles: %d\n", rep.readNo, rep.writeNo, rep.sortNo, rep.cycles);
}