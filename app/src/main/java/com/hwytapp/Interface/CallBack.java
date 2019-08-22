package com.hwytapp.Interface;

public interface CallBack {

     void OnSuccess(String param) throws Exception;

     void OnFail(String param) throws Exception;

     void OnUploading(String param) throws Exception;
}
