package com.maweiming.spark.mllib.utils;

import java.io.Serializable;

/**
 * Created by Coder-Ma on 2017/6/26.
 */
public interface FileReadFunction extends Serializable {

    String readLine(String line);

}
