package com.kuang.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {

    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("php").forEach(System.out::println);
    }

    /**
     * 封装
     * @param keywords
     * @return
     * @throws IOException
     */
    public List<String> parseJD(String keywords) throws IOException {
        // 前提 联网

        String url = "https://search.jd.com/Search?keyword=" + keywords + "&enc=utf-8&wq=ja&pvid=5bab0396a85a4d3a91011eec440f61f0";
        // 解析网页。（Jsoup 返回 Document 就是浏览器返回的 Document 对象）
        Document document = Jsoup.parse(new URL(url), 30000);
        // 所以你在js中可以使用的方法，这里都能用
        Element element= document.getElementById("navitems-group1");
        //System.out.println(element.html());

        // 获取所有的li标签
        Elements elements = element.getElementsByTag("li");

        ArrayList<String> list = new ArrayList<>();
        // 获取元素中的内容
        for (Element ele : elements) {
            String href = ele.getElementsByTag("a").eq(0).attr("href");
            list.add(href);
        }

        return list;
    }
}
