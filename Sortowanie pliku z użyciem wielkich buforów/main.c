#include <time.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "SequentialFileAndBuffer.h"
#include "GenerateFunctions.h"
#include "HelpFunctions.h"
#include "Config.h"
#include "FirstStage.h"
#include "SecondStage.h"

void* CreateOption(void* file, char*cmd){
    if (file != NULL)
        CloseFile(file);
    char*token;
    if(!(token = strtok(NULL, " ")))
        return NULL;
    file = InitFile(token, NULL, DO_OVERWRITE);
    if(!file) {
        fprintf(stderr, "Cannot create\n");
        *cmd = '\0';
        strtok(cmd, " ");
    }
    return file;
}

void* OpenOption(void*file, char*cmd){
    if (file != NULL)
        CloseFile(file);
    char *token;
    if(!(token = strtok(NULL, " ")))
        return NULL;
    if(!strcmp(token, "new"))
        file = InitFile(NEW_FILENAME, NULL, DONT_OVERWRITE);
    else if(!strcmp(token, "custom")) {
        if(!(token = strtok(NULL, " ")))
            return NULL;
        file = InitFile(token, NULL, DONT_OVERWRITE);
    }
    if(!file) {
        fprintf(stderr, "Cannot open\n");
        *cmd = '\0';
        strtok(cmd, " ");
    }
    return file;
}


void HelpOption(char*cmd){

    printf("\nAvailable options:\n");
    printf(" create [filename] - creates and opens new database\n");
    printf(" open [new|custom filename] - opens existing database (newly created or by filename) \n");
    printf(" insert [n] [random/manual] - inerts n random or manually entered records \n");
    printf(" print - prints current database \n");
    printf(" sort [normal/debug] - sorts current db in selected mode \n");
    printf(" replace - replaces current db with newly created \n");
    printf(" exit - exits the program \n\n");
    *cmd = '\0';
    strtok(cmd, " ");

}


void InsertOption(sequential_file*file, char*cmd) {
    if(!file) {
        fprintf(stderr, "File not opened\n");
        *cmd = '\0';
        strtok(cmd, " ");
        return;
    }
    char *token;
    if(!(token = strtok(NULL, " ")))
        return;
    int recordsNum;
    if(!(recordsNum= atoi(token))){
        fprintf(stderr, "Wrong number\n");
        *cmd = '\0';
        strtok(cmd, " ");
        return;
    }

    if(!(token = strtok(NULL, " ")))
        return;

    if(!strcmp(token, "random"))
        GenerateFile(file, (unsigned int) recordsNum, GenerateRandomRecord);
    if(!strcmp(token, "manual"))
        GenerateFile(file, (unsigned int) recordsNum, ManualGenerateRecord);
}

void SortOption(sequential_file*file, char*cmd) {
    if(!file) {
        fprintf(stderr, "File not opened\n");
        *cmd = '\0';
        strtok(cmd, " ");
        return;
    }
    char *token;

    if(!(token = strtok(NULL, " ")))
        return;

    report rep = NewReport();
    if(!strcmp(token, "normal")) {
        rep = ReportSum(rep,FirstStage(file, NORMAL));
        rep = ReportSum(rep,SecondStage(NORMAL));
    }
    if(!strcmp(token, "debug")){
        rep = ReportSum(rep,FirstStage(file, DEBUG));
        rep = ReportSum(rep,SecondStage(DEBUG));
    }
    PrintReport(rep);
}

void* ReplaceOption(sequential_file*file) {
    char* tmp = (char*) malloc(strlen(file->name)+1);
    strcpy(tmp, file->name);
    if (file != NULL)
        CloseFile(file);
    unlink(tmp);
    rename(NEW_FILENAME, tmp);
    void * ret = InitFile(tmp, NULL, DONT_OVERWRITE);
    free(tmp);
    return ret;
}

int main() {
    srand((unsigned) time(NULL));
    sequential_file *db = NULL;
    char * token;
    char *option = (char*) malloc(256*sizeof (char));
    printf("Insert \"help\" for menu options\n");
    while (1){
        scanf("%255[^\n]*[\n\r]", option);
        int c;
        while ((c = getchar()) != '\n' && c != EOF);
        token = strtok(option, " ");
        while (1) {
             if (!strcmp(token, "create"))
                 db = CreateOption(db, option);
            else if (!strcmp(token, "open"))
                 db = OpenOption(db, option);
            else if (!strcmp(token, "insert"))
                 InsertOption(db, option);
            else if (!strcmp(token, "print"))
                if(db)
                    PrintFile(db);
                else
                    fprintf(stderr, "File not opened\n");
            else if (!strcmp(token, "sort"))
                 SortOption(db, option);
             else if (!strcmp(token, "help"))
                 HelpOption(option);
            else if (!strcmp(token, "replace"))
                 db = ReplaceOption(db);
             else if (!strcmp(token, "exit")){
                 free(option);
                 if(db)
                     CloseFile(db);
                 return 0;
             }
            token = strtok(NULL, " ");
            if(!token)
                break;
        }

    }
}
