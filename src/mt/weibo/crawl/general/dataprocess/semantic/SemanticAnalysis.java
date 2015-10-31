package mt.weibo.crawl.general.dataprocess.semantic;

import java.util.List;

import mt.weibo.model.Word;

public class SemanticAnalysis {

	public static void main(String[] args) {
		String text = "【最新#全国快递投诉排行# 你觉得靠谱吗？】按照国家邮政局的8月份申诉情况统计，德邦、速尔、国通三家占据投诉榜前三；其中，德邦的\"丢件损毁\"投诉量高居榜首；国通延误情况最严重。与此同时，表现最好的是DHL、京东和顺丰。网友：EMS不进前三不科学~你觉得呢？[思考]";
		SemanticAnalysis sa = new SemanticAnalysis();
		String result = sa.analyze(text);
		System.out.println(result);
	}

	private String analyze(String text) {
		// TODO Auto-generated method stub
		// name entity identifying
		List<Word> list = nerAnalysis(text);
		
//		// google translate
//		String line = "";
//		for(Word word: list){
//			line = word.getText() + " ";
//		}
//		if(""==line || "".equals(line)){
//			line = text;
//		}
//		String enText = googleTranslate(line);
//		
//		// semantic analysis
//		OpenCalaisAnalysis(enText);
		return "done";
	}
	
	private List<Word> nerAnalysis(String text) {
		List<Word> list = LtpCloud.getInstance().detect(text);
		return list;
	}

	private String googleTranslate(String text) {
		// TODO Auto-generated method stub
		
		return null;
	}

	private void OpenCalaisAnalysis(String text) {
		// TODO Auto-generated method stub
		
	}


}
