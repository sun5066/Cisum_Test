package com.like.sun;


import com.like.sun.model.YoutubeVO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

//        System.out.println("검색할 동영상 제목입력 : ");
        String stringJson = "";
        try {
            stringJson = search("이하이 홀로");
        } catch (IOException exception) {
            System.out.println("아마도 트래픽이 초과된듯 싶습니다.");
            exception.printStackTrace();
            return;
        }

        JSONParser jsonParser = new JSONParser();
        Object object = null;
        try {
            object = jsonParser.parse(stringJson);

            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");
            JSONObject tempObject = null;
            List<YoutubeVO> youtubeList = new ArrayList<YoutubeVO>();
            YoutubeVO youtubeVO = null;
            for (int i = 0; i < 3; i++) {
                youtubeVO = new YoutubeVO();
                tempObject = (JSONObject) jsonArray.get(i);
                jsonObject = (JSONObject) tempObject.get("snippet");
                youtubeVO.setTitle(jsonObject.get("title").toString());

                jsonObject = (JSONObject) tempObject.get("id");
                youtubeVO.setVideoId(jsonObject.get("videoId").toString());
                youtubeVO.setUrl(String.format("https://www.youtube.com/watch?v=%s", youtubeVO.getVideoId()));
                youtubeList.add(youtubeVO);
            }

            for (YoutubeVO ytVO : youtubeList) {
                System.out.println(String.format("title\t: %s", ytVO.getTitle()));
                System.out.println(String.format("url\t\t: %s", ytVO.getUrl()));
                System.out.println(String.format("videoID\t: %s", ytVO.getVideoId()));
                System.out.println(String.format("image\t: https://i.ytimg.com/vi/%s/maxresdefault.jpg", ytVO.getVideoId()));
                System.out.println();
            }
        } catch (ParseException e) {
            System.out.println("JSON 형변환 중 예외발생! JSON 형식이 아닌것같습니다.");
        }
    }

    private static String search(String search) throws IOException {
        String apiurl = "https://www.googleapis.com/youtube/v3/search";
        apiurl += "?key=AIzaSyAGWOYDPwcoSSkC7ck2eLZNulFRHLx_VMo";
        apiurl += "&part=snippet&type=video&maxResults=20&videoEmbeddable=true";
        apiurl += "&q=" + URLEncoder.encode(search, "UTF-8");

        URL url = new URL(apiurl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();
        return response.toString();
    }

//    private static String video(String url) {
//
//    }
}