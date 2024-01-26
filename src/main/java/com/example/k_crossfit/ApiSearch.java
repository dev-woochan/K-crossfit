package com.example.k_crossfit;

import static android.system.Os.connect;

import android.util.Log;

import java.io.*;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;


public class ApiSearch extends Thread{
    String clientId;
    String clientSecret;
    String apiURL;
    Map<String, String> requestHeaders;
    String text;
    String responseBody;
    //생성자
    public ApiSearch(String text){
         this.text = text;
         this.clientId = "GS8szfeKBvYAPQOGXQFc";
         this.clientSecret = "n_9rQDPQvs";

        try {
            text = URLEncoder.encode("강원도 크로스핏", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }
        // URL을 만들어서 검색을 해준다  query 뒤에 UTF-8 인코딩된 text가 들어가고 &display=표시할검색결과수 &total 검색할 최대 갯수 !!
        //지역검색의 경우에는 최댓값이 5이기때문에 5개만 불러올수 있다.
        apiURL = "https://openapi.naver.com/v1/search/local?query="+text+"&display=5&total=5"; //JSON
        //검색시 헤더
        this.requestHeaders = new HashMap<>();
        // 클라이언트 계정
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
    }
    public void run() {
        responseBody = get(apiURL,requestHeaders);
        Log.d("searchThread", "run: "+responseBody);
    }

    public String getJson(){
        return responseBody;
    }

    public static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }
    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}

