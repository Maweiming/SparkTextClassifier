import com.maweiming.spark.mllib.classifier.DataFactory;
import com.maweiming.spark.mllib.classifier.NaiveBayesTest;
import com.maweiming.spark.mllib.classifier.NaiveBayesTrain;

import java.io.IOException;

/**
 * Created by Coder-Ma on 2017/6/26.
 */
public class Test {

    public static void main(String[] args) throws IOException {

        DataFactory.main(null);
        NaiveBayesTrain.main(null);
        NaiveBayesTest.main(null);

    }

}
