package com.maweiming.spark.mllib.classifier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maweiming.spark.mllib.utils.AnsjUtils;
import com.maweiming.spark.mllib.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 1、first step
 * data format
 * Created by Coder-Ma on 2017/6/12.
 */
public class DataFactory {

    public static final String CLASS_PATH = FileUtils.getClassPath();

    public static final String NEWS_DATA_PATH = CLASS_PATH+"\\data\\NewsData";

    public static final String DATA_TRAIN_PATH = CLASS_PATH+"\\data\\data-train.txt";

    public static final String DATA_TEST_PATH = CLASS_PATH+"\\data\\data-test.txt";

    public static final String MODELS = CLASS_PATH+"\\data\\models";

    public static final String MODEL_PATH = CLASS_PATH+"\\data\\models\\category-4";

    public static final String LABEL_PATH = CLASS_PATH+"\\data\\models\\labels.txt";

    public static final String TF_PATH = CLASS_PATH+"\\data\\models\\tf";

    public static final String IDF_PATH = CLASS_PATH+"\\data\\models\\idf";

    public static void main(String[] args) throws IOException {
        File file = new File(MODELS);
        if(!file.exists()){
            file.mkdirs();
        }
        FileUtils.deleteFile(DATA_TRAIN_PATH);
        FileUtils.deleteFile(DATA_TEST_PATH);
        FileUtils.deleteFile(LABEL_PATH);

        Double spiltRate = 0.8;//solit rate

        Map<Integer,String> labels = new HashMap<>();
        String[] dirNames = new File(NEWS_DATA_PATH).list();
        if(null==dirNames || dirNames.length==0){
            new Exception("data is null").printStackTrace();
            return;
        }
        Integer dirIndex = 0;
        for(String dirName:dirNames){
            dirIndex++;
            labels.put(dirIndex,dirName);

            String fileDirPath = String.format("%s\\%s",NEWS_DATA_PATH,dirName);
            String[] fileNames = new File(fileDirPath).list();
            int spilt = Double.valueOf(fileNames.length*spiltRate).intValue();
            for(int i=0;i<fileNames.length;i++){
                String fileName = fileNames[i];
                String text = FileUtils.readFile(String.format("%s\\%s",fileDirPath,fileName));
                text = text.replaceAll("\n","");
                text = text.replaceAll("\t","");
                text = text.replaceAll(" ","");
                text = text.replaceAll("\\u0000","");

                if(StringUtils.isBlank(text)){
                    continue;
                }

                JSONObject data = new JSONObject();
                data.put("text", AnsjUtils.participle(text));
                data.put("category",Double.valueOf(dirIndex));

                if(i>spilt){
                    //test data
                    FileUtils.appendText(DATA_TEST_PATH,data.toJSONString()+"\n");
                }else{
                    //train data
                    FileUtils.appendText(DATA_TRAIN_PATH,data.toJSONString()+"\n");
                }
            }

        }

        FileUtils.writer(LABEL_PATH, JSON.toJSONString(labels));//data labels

        System.out.println("Data processing successfully !");
        System.out.println("=======================================================");
        System.out.println("trainData:"+DATA_TRAIN_PATH);
        System.out.println("testData:"+DATA_TEST_PATH);
        System.out.println("labes:"+LABEL_PATH);
        System.out.println("=======================================================");

    }

}
