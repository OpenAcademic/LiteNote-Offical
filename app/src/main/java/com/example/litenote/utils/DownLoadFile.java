package com.example.litenote.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownLoadFile {
    public static void downfile(Context context,String url){
        String filesDirPath = Objects.requireNonNull(context.getExternalFilesDir("model")).getPath();
        OkHttpClient okHttpClient = new OkHttpClient();
        // 读取目录下的文件


        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
            }

            @Override
            public void onResponse(Call call, final Response response) { // 请求成功
                assert response.body() != null;
                String mediaType = response.body().contentType().toString();
                long length = response.body().contentLength();
                String desc = String.format("文件类型为%s，文件大小为%d", mediaType, length);
                // 回到主线程操纵界面
                String path = String.format("%s/%s",
                        Objects.requireNonNull(context.getExternalFilesDir("model")),
                        url.substring(url.lastIndexOf('/') + 1));

                // 下面从返回的输入流中读取字节数据并保存为本地文件
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(path)) {
                    byte[] buf = new byte[100 * 1024];
                    int sum=0, len=0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / length * 100);
                        String detail = String.format("文件保存在%s。已下载%d%%", path, progress);
                        // 回到主线程操纵界面
                        Log.d("down",detail);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
