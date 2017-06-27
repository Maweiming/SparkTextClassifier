package com.maweiming.spark.mllib.dto;

/**
 * Created by Coder-Ma on 2017/6/26.
 */
public class Result {

    private Double category;//text category

    private Double predict;//predict category

    public boolean isCorrect(){
        return category.equals(predict);
    }

    public Result() {
    }

    public Result(Double category, Double predict) {
        this.category = category;
        this.predict = predict;
    }

    public Double getCategory() {
        return category;
    }

    public void setCategory(Double category) {
        this.category = category;
    }

    public Double getPredict() {
        return predict;
    }

    public void setPredict(Double predict) {
        this.predict = predict;
    }
}
