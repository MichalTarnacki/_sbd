import Entries.Record;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static String input(String[] args, Scanner scanner, int i){
        if(args.length>0){
            if(i>=args.length)
                return null;
            return args[i];
        }
        else {
            return scanner.next();
        }
    }

    private static void printMenu(){
        System.out.println("Available commands");
        System.out.println(" open-file [filename] - creates if not exist and opens given file ");
        System.out.println(" print-files - prints all files");
        System.out.println(" print-all - prints all records ascending by key");
        System.out.println(" insert [key] [radius] [height] - inserts given record");
        System.out.println(" insert-random [count] - insert random count records");
        System.out.println(" remove-record [key] - removes record by given key");
        System.out.println(" update-record [key] [newKey] [newRadius] [newHeight] - updates record");
        System.out.println(" reorganize - reorganizes file");
        System.out.println(" toggle-print - turns on/of printing disk operations");
        System.out.println(" exit - exits program");
    }

    public static void main(String[] args) throws IOException {
        Isam is = null;
        Scanner scanner = new Scanner(System.in);
        String myString;
        boolean togglePrint=false;
        boolean togglePrintShort=false;
        String count = "";
        List<Record> insertedRandom = new ArrayList<>();
        int i=0;
        while (true){
            myString = input(args, scanner, i++);
            if(myString == null)
                return;
            if(is!=null && (togglePrint || togglePrintShort))
                is.resetNoOfOperations();
            switch (myString){
                case "open-file": {
                    insertedRandom = new ArrayList<>();
                    String filename = input(args, scanner, i++);
                    is = new Isam("files/"+filename);
                }
                break;
                case "print-files":
                    if(is!=null)
                        is.printFiles();
                    break;
                case "print-all":
                    if(is!=null)
                        is.printAll();
                    break;
                case "insert":
                    if(is!=null) {
                        String key = input(args, scanner, i++);
                        String radius = input(args, scanner, i++);
                        String height = input(args, scanner, i++);
                        if (key == null | radius == null | height == null)
                            return;
                        is.insertRecord(new Record(Integer.parseInt(key), (char) Integer.parseInt(radius), (char) Integer.parseInt(height)));
                    }
                    break;
                case "insert-random":
                    if(is!=null) {
                        count = input(args, scanner, i++);
                        if (count == null)
                            return;
                        Random rn = new Random(0);
                        for(int j=0; j<Integer.parseInt(count); j++) {
                            List<Integer> used = insertedRandom.stream().map(Record::getKey).toList();
                            int key;

                            do {
                                key = rn.nextInt(1, Integer.MAX_VALUE - 1);
                            }
                            while (used.contains(key));
                            Record rec = new Record(key, (char) rn.nextInt(1, 255), (char) rn.nextInt(1, 255));
                            insertedRandom.add(rec);
                            is.insertRecord(rec);
                        }
                    }
                    break;
                case "remove-random":
                    if(is!=null) {
                        count = input(args, scanner, i++);
                        if (count == null)
                            return;

                        Random rn = new Random(0);
                        Collections.shuffle(insertedRandom, new Random(0));
                        for(int j=0; j<Integer.parseInt(count); j++) {
                            int key;
                            key=insertedRandom.removeFirst().getKey();
                            is.removeRecord(key);
                        }
                    }
                    break;
                case "read-record": {
                    if(is!=null) {
                        String key = input(args, scanner, i++);
                        System.out.println(is.findRecord(Integer.parseInt(key)));
                    }
                    break;
                }
                case "read-record-random":
                    if(is!=null) {
                        count = input(args, scanner, i++);
                        if (count == null)
                            return;

                        Random rn = new Random(0);
                        Collections.shuffle(insertedRandom, new Random(0));
                        for(int j=0; j<Integer.parseInt(count); j++) {
                            int key;
                            key=insertedRandom.get(j).getKey();
                            is.findRecord(key);
                        }
                    }
                    break;
                case "remove-record": {
                    if(is!=null) {
                        String key = input(args, scanner, i++);
                        is.removeRecord(Integer.parseInt(key));
                    }
                    break;
                }
                case "update-record": {
                    if(is!=null) {
                        String key = input(args, scanner, i++);
                        String key2 = input(args, scanner, i++);
                        String radius = input(args, scanner, i++);
                        String height = input(args, scanner, i++);
                        if (key == null | radius == null | height == null)
                            return;
                        is.replaceRecord(Integer.parseInt(key), new Record(Integer.parseInt(key2), (char) Integer.parseInt(radius), (char) Integer.parseInt(height)));
                    }
                    break;
                }
                case "update-record-random-same-key":
                    if(is!=null) {
                        count = input(args, scanner, i++);
                        if (count == null)
                            return;
                        Random rn = new Random(0);
                        Collections.shuffle(insertedRandom, new Random(0));
                        for(int j=0; j<Integer.parseInt(count); j++) {
                            int key = insertedRandom.removeFirst().getKey();
                            Record rec = new Record(key,(char) rn.nextInt(1, 255), (char) rn.nextInt(1, 255));
                            insertedRandom.add(rec);
                            is.replaceRecord(key, rec);
                        }
                    }
                    break;
                case "update-record-random-new-key":
                    if(is!=null) {
                        count = input(args, scanner, i++);
                        if (count == null)
                            return;
                        Collections.shuffle(insertedRandom, new Random(0));
                        Random rn = new Random(0);
                        for(int j=0; j<Integer.parseInt(count); j++) {
                            if(insertedRandom.size()>0) {
                                int key = insertedRandom.getFirst().getKey();
                                List<Integer> used = insertedRandom.stream().map(Record::getKey).toList();
                                int newKey;

                                do {
                                    newKey = rn.nextInt(1, Integer.MAX_VALUE - 1);
                                }
                                while (used.contains(newKey));
                                insertedRandom.removeFirst();
                                Record rec = new Record(newKey,(char) rn.nextInt(1, 255), (char) rn.nextInt(1, 255));
                                insertedRandom.add(rec);
                                is.replaceRecord(key, rec);
                            }
                        }

                    }
                    break;
                case "reorganize": {
                    if(is!=null)
                        is.reorganize();
                }
                break;
                case "toggle-print": {
                    togglePrint=!togglePrint;
                }
                break;
                case "toggle-print-short": {
                    togglePrintShort=!togglePrintShort;
                }
                break;
                case "help":
                    printMenu();
                    break;
                case "set-a":
                    if(is!=null) {
                    String alfa = input(args, scanner, i++);
                    is.setAlfa(Double.parseDouble(alfa));
                }
                    break;
                case "set-vn":
                    if(is!=null) {
                    String vn = input(args, scanner, i++);
                    is.setVN(Double.parseDouble(vn));
                }
                    break;
                case "remove-files":
                    if(is!=null) {
                        long filesSize = is.file.getFile().length()+is.file.getOverflowArea().getFile().length()+is.indexFile.getFile().length();
                        long recordsSize = (long) insertedRandom.size() * Record.sizeOf();
                        DecimalFormat df = new DecimalFormat("0.00");
                        System.out.print(df.format((double)filesSize/recordsSize));
                       is.close();

                        new File(is.filename).delete();
                        new File(is.filename+".idx").delete();
                        new File(is.filename+".overflow").delete();
                        is=null;
                        System.out.println(" ");
                    }
                    break;
                case "exit":
                    break;

            }
            if(is!=null && togglePrint && !myString.equals("toggle-print"))
                is.printNoOfOperations();
            else if(is!=null && togglePrintShort && !myString.equals("toggle-print-short"))
                is.printNoOfOperationsShort();
        }
    }
}