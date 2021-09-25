package com.qiqi.builder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class VisitorsListEntity implements Parcelable {

    private ArrayList<VisitorsEntity> entityArrayList;
    private ArrayList<VisitorsEntity> singerEntityArrayList;
    private int statusCode;

    private int errorCode;

    private boolean isHavePage;

    private int total;

    public boolean isHavePage() {
        return isHavePage;
    }

    public void setHavePage(boolean havePage) {
        isHavePage = havePage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public ArrayList<VisitorsEntity> getEntityArrayList() {
        return entityArrayList;
    }

    public void setEntityArrayList(ArrayList<VisitorsEntity> entityArrayList) {
        this.entityArrayList = entityArrayList;
    }

    public ArrayList<VisitorsEntity> getSingerEntityArrayList() {
        return singerEntityArrayList;
    }

    public void setSingerEntityArrayList(ArrayList<VisitorsEntity> singerEntityArrayList) {
        this.singerEntityArrayList = singerEntityArrayList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
