package com.example.myapplication;

import android.os.Parcelable;

import java.io.Serializable;

public class Sms implements Serializable {
    private String _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" for have not read sms and "1" for have read sms

    private Integer _time;
    private String _folderName;

    public String getId(){
        return _id;
    }
    public String getAddress(){
        return _address;
    }
    public String getMsg(){
        return _msg;
    }
    public String getReadState(){
        return _readState;
    }
    public Integer getTime(){
        return _time;
    }
    public String getFolderName(){
        return _folderName;
    }


    public void setId(String id){
        _id = id;
    }
    public void setAddress(String address){
        _address = address;
    }
    public void setMsg(String msg){
        _msg = msg;
    }
    public void setReadState(String readState){
        _readState = readState;
    }
    public void setTime(Integer time){
        _time = time;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }


}
