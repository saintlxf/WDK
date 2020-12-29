package pers.lxf.wdk.util;

import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {
    /**
     * 检查目录是否存在，不存在时创建目录
     * @param dirPath 目录字符串
     */
    public static void createDirWithCheck(String dirPath){
        File dir = new File(dirPath);
        createDirWithCheck(dir);
    }

    public static void createDirWithCheck(File dirFile){
        if(dirFile.exists()&&dirFile.isDirectory()){
            return ;
        }
        dirFile.mkdirs();
    }

    public static void createFileWithDeleteFile(File file){
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static File createFileWithString(String path,String fileName){
        createDirWithCheck(path);
        File file = new File(path+fileName);
        createFileWithDeleteFile(file);
        return file;
    }

    private static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void copyDirAllFiles(File sourceDirFile,File destDirFile){
        if(sourceDirFile==null||!sourceDirFile.exists()||!sourceDirFile.isDirectory()){
            return ;
        }
        if(destDirFile==null){
            return ;
        }
        createDirWithCheck(destDirFile);
        File[] sourceFiles = sourceDirFile.listFiles();
        for (File file :
                sourceFiles) {
            copyFileUsingFileChannels(file,new File(destDirFile.getPath(),file.getName()));
        }
    }
    public static void copyDirAllFiles(String sourceDir,String destDir){
        File sourceDirFile = new File(sourceDir);
        File destDirFile = new File(destDir);
        copyDirAllFiles(sourceDirFile,destDirFile);
    }

    public void readFile(String filePath){
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            String line = null;
            while((line = bufferedReader.readLine())!=null){
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(File file, List<String> contentList){
        String content = contentList.stream().collect(Collectors.joining("\r\n"));
        writeFile(file,content);
    }

    /**
     * 写文件
     * @param file 文件对象
     * @param content 写入内容
     */
    public static void writeFile(File file,String content){
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
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
    public static File getResourceFile(String fileOrDir){
        File result = null;
        try{
            result = ResourceUtils.getFile(fileOrDir);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return result;
    }
    public static void main(String[] args) {
        File abc = new File("d:/idea/work/");
        System.out.println(abc.getPath());
    }
}
