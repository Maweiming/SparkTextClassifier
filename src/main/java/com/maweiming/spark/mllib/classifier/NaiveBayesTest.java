package com.maweiming.spark.mllib.classifier;

import com.alibaba.fastjson.JSON;
import com.maweiming.spark.mllib.dto.Result;
import com.maweiming.spark.mllib.utils.AnsjUtils;
import com.maweiming.spark.mllib.utils.FileUtils;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 3、the third step
 * Created by Coder-Ma on 2017/6/26.
 */
public class NaiveBayesTest {

    private static HashingTF hashingTF;

    private static IDFModel idfModel;

    private static NaiveBayesModel model;

    private static Map<Integer,String> labels = new HashMap<>();

    static {
        boolean error = false;
        if (!new File(DataFactory.DATA_TEST_PATH).exists()) {
            new Exception(DataFactory.DATA_TEST_PATH + " is not exists").printStackTrace();
            error = true;
        }
        if (!new File(DataFactory.MODEL_PATH).exists()) {
            new Exception(DataFactory.MODEL_PATH + " is not exists").printStackTrace();
            error = true;
        }
        if (!new File(DataFactory.TF_PATH).exists()) {
            new Exception(DataFactory.TF_PATH + " is not exists").printStackTrace();
            error = true;
        }
        if (!new File(DataFactory.IDF_PATH).exists()) {
            new Exception(DataFactory.IDF_PATH + " is not exists").printStackTrace();
            error = true;
        }
        if (!new File(DataFactory.LABEL_PATH).exists()) {
            new Exception(DataFactory.LABEL_PATH + " is not exists").printStackTrace();
            error = true;
        }
        if (error) {
            System.exit(0);
        }

        String labelsData = FileUtils.readFile(DataFactory.LABEL_PATH);

        labels = JSON.parseObject(labelsData,Map.class);
    }

    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder().appName("NaiveBayes").master("local")
                .getOrCreate();

        //load tf file
        hashingTF = HashingTF.load(DataFactory.TF_PATH);
        //load idf file
        idfModel = IDFModel.load(DataFactory.IDF_PATH);
        //load model
        model = NaiveBayesModel.load(spark.sparkContext(), DataFactory.MODEL_PATH);

        //batch test
        batchTestModel(spark, DataFactory.DATA_TEST_PATH);

        //test a single
        testModel(spark,"自2008年首款 Andriod 系统推出以来，Google 一直提倡与App 开发者，设备制造商，及广大用户共享 Android 平台的预览版，并期望收到技术方面的反馈。\n" +
                "今日， Google 发布首个 Android O 开发者预览版。 虽然作为早期预览版来说，很多新特性未正式加入其中，在稳定性与性能方面也需要更多改进，但这仅仅是一个开端。\n" +
                "在接下来的几个月中，Google 将发布开发者预览版的更新，更多详情将在今年 5 月的 Google I/O 大会上揭晓。同时，Google 也期待收到开发者关于新特性的反馈，也希望有更多开发者在这新的操作系统中对 App 进行测试。");

    }

    public static void batchTestModel(SparkSession sparkSession, String testPath) {

        Dataset<Row> test = sparkSession.read().json(testPath);
        //word frequency count
        Dataset<Row> featurizedData = hashingTF.transform(test);
        //count tf-idf
        Dataset<Row> rescaledData = idfModel.transform(featurizedData);

        List<Row> rowList = rescaledData.select("category", "features").javaRDD().collect();

        List<Result> dataResults = new ArrayList<>();
        for (Row row : rowList) {
            Double category = row.getAs("category");
            SparseVector sparseVector = row.getAs("features");
            Vector features = Vectors.dense(sparseVector.toArray());
            double predict = model.predict(features);
            dataResults.add(new Result(category, predict));
        }

        Integer successNum = 0;
        Integer errorNum = 0;

        for (Result result : dataResults) {
            if (result.isCorrect()) {
                successNum++;
            } else {
                errorNum++;
            }
        }

        DecimalFormat df = new DecimalFormat("######0.0000");
        Double result = (Double.valueOf(successNum) / Double.valueOf(dataResults.size())) * 100;

        System.out.println("batch test");
        System.out.println("=======================================================");
        System.out.println("Summary");
        System.out.println("-------------------------------------------------------");
        System.out.println(String.format("Correctly Classified Instances          :      %s\t   %s%%",successNum,df.format(result)));
        System.out.println(String.format("Incorrectly Classified Instances        :       %s\t    %s%%",errorNum,df.format(100-result)));
        System.out.println(String.format("Total Classified Instances              :      %s",dataResults.size()));
        System.out.println("===================================");

    }

    public static void testModel(SparkSession sparkSession, String content){
        List<Row> data = Arrays.asList(
                RowFactory.create(AnsjUtils.participle(content))
        );
        StructType schema = new StructType(new StructField[]{
                new StructField("text", new ArrayType(DataTypes.StringType, false), false, Metadata.empty())
        });

        Dataset<Row> testData = sparkSession.createDataFrame(data, schema);
        //word frequency count
        Dataset<Row> transform = hashingTF.transform(testData);
        //count tf-idf
        Dataset<Row> rescaledData = idfModel.transform(transform);

        Row row =rescaledData.select("features").first();
        SparseVector sparseVector = row.getAs("features");
        Vector features = Vectors.dense(sparseVector.toArray());
        Double predict = model.predict(features);

        System.out.println("test a single");
        System.out.println("=======================================================");
        System.out.println("Result");
        System.out.println("-------------------------------------------------------");
        System.out.println(labels.get(predict.intValue()));
        System.out.println("===================================");
    }


}
