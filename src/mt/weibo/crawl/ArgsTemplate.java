package mt.weibo.crawl;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgsTemplate {

	public static void main(String[] args) {
		
		String appkey = "appkey.txt";
		int interval = 3;
		
		Option helpOp = new Option("h", "help", false,"print this message");
		Option appkeyOp = new Option("k","appkey", true, "use given file as appkey file");
		Option intervalOp = new Option("i", "interval", true, "interval before next crawling");
		
		Options options = new Options();
		options.addOption(helpOp);
		options.addOption(appkeyOp);
		options.addOption(intervalOp);
		
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if(cmd.hasOption("help")){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( ArgsTemplate.class.getName(), options);
			}
			if(cmd.hasOption("appkey")){
				if(cmd.getOptionValue("appkey")!=null){
					appkey = cmd.getOptionValue("appkey");
				}
			}
			if(cmd.hasOption("interval")){
				if(cmd.getOptionValue("interval")!=null){
					interval = Integer.valueOf(cmd.getOptionValue("interval"));
				}
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArgsTemplate at = new ArgsTemplate(appkey, interval);
		
	}
	

	public ArgsTemplate(String appkey, int interval) {
		// TODO Auto-generated constructor stub
		System.out.println();
		System.out.println("appkey = "+ appkey);
		System.out.println("interval = " + interval);
	}


}
