package com.wst;

import okhttp3.*;
import org.json.JSONObject;

import java.io.File;

public class FileUploader {

    /**
     * 上传文件到指定的 URL，并提取响应中的 data 字段
     * @param file 要上传的文件对象
     * @param url 上传的目标 URL
     * @return 响应中 data 字段的值（即链接）
     * @throws Exception 如果发生错误
     */
    public static String uploadFile(File file, String url) throws Exception {
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + file.getAbsolutePath());
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/png"));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("请求失败，状态码: " + response.code());
            }

            // 解析 JSON 响应
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // 提取 data 字段
            return jsonObject.optString("data", "链接未找到");
        }
    }

    public static void main(String[] args) {
        String a="38元";
        a=a.replaceAll("[^0-9.]", "");
        double b=Double.parseDouble(a);
        System.out.println(b);
    }
}



