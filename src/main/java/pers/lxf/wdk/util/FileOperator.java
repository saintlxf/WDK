package pers.lxf.wdk.util;

import pers.lxf.wdk.beans.DBTableDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class FileOperator {
    private File operateFile;
    public FileOperator(File file){
        this.operateFile = file;
    }
    public void writeFile(DBTableDefinition tableDefinition,Function<DBTableDefinition, List<String>> f){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(operateFile));
            List<String> stringList = f.apply(tableDefinition);
            for (String str :
                    stringList) {
                bw.write(str+"\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
