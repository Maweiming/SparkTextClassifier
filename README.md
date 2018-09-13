# SparkTextClassifier
    使用Spark NaiveBayes 实现中文文本分类
    测试数据是我们自己用爬虫抓取的，自己练手也可以用搜狗实验室的数据集（http://www.sogou.com/labs/resource/list_news.php）
    朴素贝叶斯分类效果不是很好 数据量大的时候准确率差不多80%，有条件可以试试tensorflow用CNN+RNN+word2vec 我们线上的准确率98% 
 
#### 0、DataFactory.java
    0、读取 classPath -> data -> NewsData 目录下的数据 （通过文件夹分类） 
    1、读取 classPath -> data -> stopWord.txt文件
    2、移除停用词
    3、把数据分词
    4、分割数据(分为测试数据和训练数据)
    5、写入数据到 classPath -> data 目录下的 data-test.txt 和 data-train.txt 文件中
    6、保存标签数据到 classPath -> models ->  labels.txt 文件中
        
#### 1、NaiveBayesTrain.java
    0、读取 classPath -> data -> data-train.txt 文件
    1、训练模型...
    2、保存模型到 classPath -> models -> category-4 目录
    3、保存tf文件到  classPath -> models -> tf 目录
    4、保存idf文件到  classPath -> models -> idf 目录
        
#### 2、NaiveBayesTest.java
    0、加载模型 
    1、加载tf、idf
    2、读取 classPath -> data -> data-test.txt 文件
    3、对数据进行打分
    
### 目录结构
    -resources
        --data
            ---NewsData             语料目录
            ---data-test.txt        测试数据
            ---data-train.txt       训练数据
            ---stopWord.txt         停用词
        ---models
            ---category-4      模型目录
            ---idf             idf文件
            ---tf              tf文件
            ---labels.txt      标签数据
               
               
### 测试集训练结果
```
=======================================================
Summary
-------------------------------------------------------
Correctly Classified Instances          :      786	   98.7437%
Incorrectly Classified Instances        :       10	    1.2563%
Total Classified Instances              :      796
===================================
```

