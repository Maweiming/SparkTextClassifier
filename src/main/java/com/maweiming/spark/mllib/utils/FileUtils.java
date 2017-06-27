package com.maweiming.spark.mllib.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * file utils
 * Created by Coder-Ma on 2017/6/26.
 */
public class FileUtils {

    public static void main(String[] args) {
        System.out.println(getClassPath());
    }

    /**
     * get project path
     * @return
     */
    public static String getClassPath(){
        String classPath = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        return new File(classPath).getPath();
    }

    /**
     * delete file
     * @param filePath filePath
     */
    public static void deleteFile(String filePath){
        File file = new File(filePath);
        file.delete();
    }

    /**
     * read file
     * @param filePath filePath
     * @return result
     */
    public static String readFile(String filePath) {
        File file = new File(filePath);
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
            int fileLen = (int) file.length();
            char[] chars = new char[fileLen];
            try {
                reader.read(chars);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String text = String.valueOf(chars);
            return text;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * writer file-append content
     * @param filePath filePath
     * @param content append content
     */
    public static void appendText(String filePath, String content) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * writer file
     * @param filePath filePath
     * @param content file content
     */
    public static void writer(String filePath, String content) {
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * read file（line）line read
     * @param fileRead callback function
     * @param filePath filePath
     * @param charsetName charsetName
     * @return result
     */
    public static List<String> readLine(FileReadFunction fileRead, String filePath, String charsetName) {
        List<String> textList = new ArrayList<>();
        try {
            FileInputStream in = new FileInputStream(filePath);
            InputStreamReader inReader = new InputStreamReader(in, charsetName);
            BufferedReader bufReader = new BufferedReader(inReader);
            String line = null;
            while ((line = bufReader.readLine()) != null) {
                String text = fileRead.readLine(line);
                if (text != null) {
                    textList.add(text);
                }
            }
            bufReader.close();
            inReader.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return textList;
    }

    /**
     * read file（line）line read
     * @param fileRead callback function
     * @param filePath filePath
     * @return result
     */
    public static List<String> readLine(FileReadFunction fileRead, String filePath) {
        return readLine(fileRead, filePath, "UTF-8");
    }

}
