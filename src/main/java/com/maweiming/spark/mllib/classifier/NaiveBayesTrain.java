package com.maweiming.spark.mllib.classifier;

import com.maweiming.spark.mllib.utils.FileUtils;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.mllib.classification.NaiveBayes;
import org.apache.spark.mllib.classification.NaiveBayesModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

/**
 * 2、The second step
 * Created by Coder-Ma on 2017/6/26.
 */
public class NaiveBayesTrain {

    static {
        FileUtils.deleteFile(DataFactory.MODEL_PATH);
        FileUtils.deleteFile(DataFactory.TF_PATH);
        FileUtils.deleteFile(DataFactory.IDF_PATH);
    }

    public static void main(String[] args) throws IOException {
        if(!new File(DataFactory.DATA_TRAIN_PATH).exists()){
            new Exception(DataFactory.DATA_TRAIN_PATH+" is not exists").printStackTrace();
            return;
        }

        SparkSession spark = SparkSession.builder().appName("NaiveBayes").master("local")
                .config("spark.driver.memory", "1073741824")
                .config("spark.testing.memory", "10073741824")
                .getOrCreate();

        Dataset<Row> train = spark.read().json(DataFactory.DATA_TRAIN_PATH);

        //word frequency count
        HashingTF hashingTF = new HashingTF().setNumFeatures(500000).setInputCol("text").setOutputCol("rawFeatures");
        Dataset<Row> featurizedData  = hashingTF.transform(train);

        //count tf-idf
        IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        IDFModel idfModel = idf.fit(featurizedData);
        Dataset<Row> rescaledData = idfModel.transform(featurizedData);

        JavaRDD<LabeledPoint> trainDataRdd = rescaledData.select("category", "features").javaRDD().map(v1 -> {
            Double category = v1.getAs("category");
            SparseVector features = v1.getAs("features");
            Vector featuresVector = Vectors.dense(features.toArray());
            return new LabeledPoint(Double.valueOf(category),featuresVector);
        });

        System.out.println("Start training...");
        NaiveBayesModel model  = NaiveBayes.train(trainDataRdd.rdd());
        model.save(spark.sparkContext(),DataFactory.MODEL_PATH);//save model
        hashingTF.save(DataFactory.TF_PATH);//save tf
        idfModel.save(DataFactory.IDF_PATH);//save idf

        System.out.println("train successfully !");
        System.out.println("=======================================================");
        System.out.println("modelPath:"+DataFactory.MODEL_PATH);
        System.out.println("tfPath:"+DataFactory.TF_PATH);
        System.out.println("idfPath:"+DataFactory.IDF_PATH);
        System.out.println("=======================================================");
    }

}
