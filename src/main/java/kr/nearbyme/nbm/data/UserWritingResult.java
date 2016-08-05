package kr.nearbyme.nbm.data;

/**
 * Created by CHOIMOONYOUNG on 2016. 5. 25..
 */
public class UserWritingResult {
    public int param_sort;
    public MyReview writing;

    public int getParam_sort() {
        return param_sort;
    }

    public void setParam_sort(int param_sort) {
        this.param_sort = param_sort;
    }

    public MyReview getWriting() {
        return writing;
    }

    public void setWriting(MyReview writing) {
        this.writing = writing;
    }
}
