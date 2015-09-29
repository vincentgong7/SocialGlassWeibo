/*
 * This example shows how to use Java to build http connection and request
 * the ltp-cloud service for perform full-stack Chinese language analysis
 * and get results in specified formats
 */
package mt.weibo.crawl.general.dataprocess.semantic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SimpleAPI {
    public static void main(String[] args) throws IOException {
//        if (args.length < 1 || !(args[0].equals("xml") 
//                                || args[0].equals("json") 
//                                || args[0].equals("conll"))) {
//            System.out.println("Usage: java SimpleAPI [xml/json/conll]");
//            return;
//        }

        String api_key = "l4a6G1D2wb1oxPtMwd4FRp5UPnjAIKLAKMPlAcPQ";
        String pattern = "all";
//      String format  = args[0];
        String format  = "json";
        String text    = "从18岁开始就吊儿郎当浪在外，自己的韶华时光都不在上海，感觉自己一点不像上海人，明天又要挥别这生我养我的地方，心里说不出的怪滋味儿，想有个家,!!想养条狗。";
        text = URLEncoder.encode(text, "utf-8");
        
        URL url     = new URL("http://ltpapi.voicecloud.cn/analysis/?"
                              + "api_key=" + api_key + "&"
                              + "text="    + text    + "&"
                              + "format="  + format  + "&"
                              + "pattern=" + pattern);
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader innet = new BufferedReader(new InputStreamReader(
                                conn.getInputStream(),
                                "utf-8"));
        String line;
        while ((line = innet.readLine())!= null) {
            System.out.println(line);
        }
        innet.close();
    }
    
}
