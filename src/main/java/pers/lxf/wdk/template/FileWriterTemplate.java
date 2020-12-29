package pers.lxf.wdk.template;

import pers.lxf.wdk.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 写文件模板类
 */
public class FileWriterTemplate {
    private String path;
    private String fileName;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileWriterTemplate(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public FileWriterTemplate() {
    }

    public void writeFile(String str){
        if(path==null||path.trim().length()==0||fileName==null||fileName.trim().length()==0){
            return ;
        }
        File writeFile = FileUtil.createFileWithString(path,fileName);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(writeFile));
            bw.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
