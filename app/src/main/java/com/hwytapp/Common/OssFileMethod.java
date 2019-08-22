package com.hwytapp.Common;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.hwytapp.Interface.CallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

public class OssFileMethod {

    private OSS oss;
    private String bucketName   = "hwytyk";
    private String AccessKey    = "LTAIGXPe36BXGeFh";
    private String SecretKey    = "rmhfIOYmv44ncas0sIBdrAkqMTQtlp";

    public  OssFileMethod(Context context)
    {
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";

        //推荐使用OSSAuthCredentialsProvider。token过期可以及时更新
        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {

            @Override
            public String signContent(String content) {

                // 以下是用本地算法进行的演示
                String signedString = OSSUtils.sign(AccessKey, SecretKey, content);
                return signedString;
            }
        };
        this.oss = new OSSClient(context, endpoint, credentialProvider);
    }

    public  void uploadAsyncFile(String uploadFilePath, String localFilePath, final CallBack cb)
    {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucketName, uploadFilePath, localFilePath);

        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                DecimalFormat df = new DecimalFormat("0.00");
                try
                {

                    String progress = df.format((float)currentSize/(float)totalSize*100)+"%";
                    cb.OnUploading(progress);

                }catch (Exception e)
                {}

            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");

                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());

                try {
                    cb.OnSuccess("");
                }catch (Exception e)
                {}
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public void downAsyncFile(String ossFilePath, final  String localFilePath, final String cbID, final CallBack cb)
    {
        GetObjectRequest get = new GetObjectRequest(bucketName, ossFilePath);
        //设置下载进度回调
        get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
            @Override
            public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                OSSLog.logDebug("getobj_progress: " + currentSize+"  total_size: " + totalSize, false);
                DecimalFormat df = new DecimalFormat("0.00");
                try
                {
                    String progress = df.format((float)currentSize/(float)totalSize*100)+"%";
                    cb.OnUploading(progress);

                }catch (Exception e)
                {}

            }
        });
        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result)  {
                // 请求成功
                InputStream inputStream = result.getObjectContent();
                File file = new File(localFilePath);
                byte[] buffer = new byte[2048];
                int len;
                try {
                    OutputStream os = new FileOutputStream(file);
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 处理下载的数据
                        os.write(buffer, 0, len);
                    }
                    os.close();
                    inputStream.close();

                    cb.OnSuccess(cbID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
    }

    public void deleteOssFile(String ossFilePath,final  String apkID ,final CallBack cb)
    {
        // 创建删除请求
        DeleteObjectRequest delete = new DeleteObjectRequest(bucketName, ossFilePath);
        // 异步删除
        OSSAsyncTask deleteTask = oss.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {

            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                Log.d("asyncCopyAndDelObject", "success!");
                try {
                    cb.OnSuccess(apkID);
                }catch (Exception e)
                {}

            }
            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }

        });
    }
}
