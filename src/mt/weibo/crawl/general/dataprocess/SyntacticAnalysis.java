package mt.weibo.crawl.general.dataprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.weibo.common.ShortURL;

public class SyntacticAnalysis {

	public static void main(String[] args) {
		String text = "【最新#全国快递投诉排行# 你觉得靠谱吗？】按照国家邮政局的8月份申诉情况统计，德邦、速尔、国通三家占据投诉榜前三；其中，德邦的\"丢件损毁\"投诉量高居榜首；国通延误情况最严重。与此同时，表现最好的是DHL、京东和顺丰。网友：EMS不进前三不科学~你觉得呢？[思考]";
		SyntacticAnalysis sa = new SyntacticAnalysis();
		String result = sa.analyze(text);
		System.out.println(result);
	}

	private String analyze(String text) {
		hashTagAnalyze(text);
		urlTagAnalyze(text);
		atTagAnalyze(text);
		punctuationAnalyze(text);
		return "done";
	}

	private int hashTagAnalyze(String text) {
		// ...#xxx#...
		// "嘻嘻…今天拍#IPAD3##IPAD3#呀…[哈哈][抱抱][害羞]其實和之前的沒什麽分別呀…[挖鼻屎][挖鼻屎]我在:#星巴克 STARBUCKS (沙面店)#"
		// count: how many hashtags have been used
		String pattern = "#.*?#";
		int totalHashTag = getMatchCount(text, pattern);
		System.out.println(totalHashTag);
		return totalHashTag;
	}

	private int urlTagAnalyze(String text) {
		// test each short url, conut 1 if the actual url is not sina geo page
		// 😁😁long time no see呀親們[愛你][愛你] @im周露露 @LWH7 @蜡笔_小吠 @Silver-th輝輝
		// @CY_Wrong 長島都係好難飲😱😱😱 我在这里:http://t.cn/zl1cGRA

		text = text + " ";
		int total = 0;
		String pattern = "http://t.cn/.+? ";
		List<String> list = getAllMatches(text, pattern);

		for (String url : list) {
			if (!isGeoUrl(url.trim())) {
				total++;
			}
		}
		System.out.println(total);
		return total;
	}

	private int atTagAnalyze(String text) {
		// "😁😁long time no see呀親們[愛你][愛你] @im周露露 @LWH7 @蜡笔_小吠 @Silver-th輝輝 @CY_Wrong 長島都係好難飲😱😱😱 我在这里:http://t.cn/zl1cGRA"

		text = text + " ";
		String pattern = "@.+? ";
		int totalAtTag = getMatchCount(text, pattern);
		System.out.println(totalAtTag);
		return totalAtTag;
	}

	private Map<String, Integer> punctuationAnalyze(String text) {
		// TODO Auto-generated method stub
		// "😁😁long time no see呀親們[愛你]。[愛你] @im周露露, @LWH7 @蜡笔_小吠 @Silver-th輝輝;. ,,m,,,,，，，@CY_Wrong 長；；,島_-——《》都係好難飲😱😱😱！！！!!!...：：“打发士大夫” 'dfad' 我在这里:http://t.cn/zl1cGRA";

		int total = 0;
		Map<String, Integer> map = new HashMap<String, Integer>();
		String pattern = "[,，.。!！\"“”'‘?？:：;；~-——-《》]";
		// ;~--_——]
		List<String> list = getAllMatches(text, pattern);

		for (String punc : list) {
			total++;
			if (map.containsKey(punc)) {
				int value = map.get(punc);
				value++;
				map.put(punc, value);
			} else {
				map.put(punc, 1);
			}
		}

		mergePunc(map, ",", "，");
		mergePunc(map, ".", "。");
		mergePunc(map, "!", "！");
		mergePunc(map, ";", "；");
		mergePunc(map, ":", "：");
		mergePunc(map, "\"", "“");
		mergePunc(map, "\"", "”");
		mergePunc(map, "\"", "'");

		System.out.println(map);
		System.out.println(total);
		return map;
	}

	private void mergePunc(Map<String, Integer> map, String punc1, String punc2) {
		if (map.containsKey(punc2)) {
			if (map.containsKey(punc1)) {
				int value = map.get(punc1);
				value = value + map.get(punc2);
				map.put(punc1, value);
				map.remove(punc2);
			} else {
				map.put(punc1, map.get(punc2));
				map.remove(punc2);
			}
		}
	}

	private int getMatchCount(String text, String regex) {
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(text);
		int total = 0;
		while (m.find()) {
			total++;
		}
		return total;
	}

	public static List<String> getAllMatches(String text, String pattern) {
		Pattern regex = Pattern.compile(pattern);
		List<String> results = new ArrayList<String>();
		Matcher regexMatcher = regex.matcher(text);
		while (regexMatcher.find()) {
			results.add(regexMatcher.group());
		}
		return results;
	}

	/*
	 * return true if this is an sina geo url tag else false
	 */
	private boolean isGeoUrl(String url) {
		// eg. http://weibo.com/p/1001018003100000000000000
		// eg. http://weibo.cn/pages/100101B2094757D16DA1F44893
		String longURL = ShortURL.expandURL(url);
		if (longURL.startsWith("http://weibo.com/p")
				|| longURL.startsWith("http://weibo.cn/p")) {
			return true;
		}
		return false;
	}
}
