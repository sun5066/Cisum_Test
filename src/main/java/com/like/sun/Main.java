package com.like.sun;


import com.like.sun.model.YoutubeVO;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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

        String searchJson = "";
        try {
            searchJson = search("이하이 홀로");
        } catch (IOException exception) {
            System.out.println("아마도 트래픽이 초과된듯 싶습니다.");
            exception.printStackTrace();
            return;
        }

        List<YoutubeVO> youtubeList = getYouTubeData(searchJson);
        if (!youtubeList(youtubeList)) {
            System.out.println("데이터가 없습니다.");
        }
    }

    private static List<YoutubeVO> getYouTubeData(String stringJson) {
        JSONParser jsonParser = new JSONParser();
        Object object = null;
        try {
            object = jsonParser.parse(stringJson);

            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");

            List<YoutubeVO> youtubeList = new ArrayList<YoutubeVO>();
            JSONObject tempObject = null;
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
            return youtubeList;
        } catch (ParseException e) {
            System.out.println("JSON 형변환 중 예외발생! JSON 형식이 아닌것같습니다.");
        }
        return null;
    }

    private static void getDuration(String stringJson) {
        JSONParser jsonParser = new JSONParser();
        Object object = null;
        try {  //items > contentDetails > duration
            object = jsonParser.parse(stringJson);

            JSONObject jsonObject = (JSONObject) object;
            JSONArray jsonArray = (JSONArray) jsonObject.get("items");
            JSONObject tempObject = null;
            for (int i = 0; i < jsonArray.size(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);
                tempObject = (JSONObject) jsonObject.get("contentDetails");
                System.out.println(
                        tempObject.get("duration")
                        .toString()
                        .replace("PT", "")
                        .replace("H", "시간")
                        .replace("M", "분")
                        .replace("S", "초")
                );
            }

        } catch (ParseException e) {
            System.out.println("JSON 형변환 중 예외발생! JSON 형식이 아닌것같습니다.");
        }
    }

    private static boolean youtubeList(List<YoutubeVO> youtubeList) throws IOException {
        if (youtubeList == null) {
            return false;
        }
        for (YoutubeVO ytVO : youtubeList) {
            System.out.println(String.format("title\t: %s", ytVO.getTitle()));
            System.out.println(String.format("url\t\t: %s", ytVO.getUrl()));
            System.out.println(String.format("videoID\t: %s", ytVO.getVideoId()));
            System.out.println(String.format("image\t: https://i.ytimg.com/vi/%s/maxresdefault.jpg", ytVO.getVideoId()));
            getDuration(video(ytVO.getVideoId()));
            System.out.println();
        }
        return true;
    }

    private static String search(String search) throws IOException {
        String apiurl = "https://www.googleapis.com/youtube/v3/search";
        apiurl += "?key=AIzaSyAGWOYDPwcoSSkC7ck2eLZNulFRHLx_VMo";
        apiurl += "&part=snippet&type=video&maxResults=20&videoEmbeddable=true";
        apiurl += "&q=" + URLEncoder.encode(search, "UTF-8");

        return getString(apiurl);
    }

    private static String video(String id) throws IOException {
        String apiurl = "https://www.googleapis.com/youtube/v3/videos";
        apiurl += "?id=" + id;
        apiurl += "&part=contentDetails";
        apiurl += "&key=AIzaSyAGWOYDPwcoSSkC7ck2eLZNulFRHLx_VMo";

        return getString(apiurl);
    }

    private static String getString(String apiurl) throws IOException {
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
}