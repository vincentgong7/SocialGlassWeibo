package mt.weibo.crawl.general.dataprocess.semantic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mt.weibo.db.MyDBConnection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class SemanticCrawl {

	private int port = 5432;
	private String postTableName = "socialmedia.post_in_scope";
	private String semanticTableName = "socialmedia.semantic";
	private MyDBConnection mdbc;
	private Connection con;

	public SemanticCrawl(int port) {
		this.port = port;
		if (mdbc == null) {
			mdbc = new MyDBConnection(this.port);
			con = mdbc.getDBConnection();
		}
	}

	public static void main(String[] args) {
		int port = 5432;

		Options options = new Options();
		options.addOption("h", "help", false, "print this message");
		options.addOption("p", true, "database port");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(SemanticCrawl.class.getName(), options);
			} else {
				if (cmd.hasOption("p")) {
					if (cmd.getOptionValue("p") != null) {
						port = Integer.valueOf(cmd.getOptionValue("p"));
					}
				}
				SemanticCrawl sc = new SemanticCrawl(port);
				sc.process();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void process() {
		// step1: get post with other user_id, content from post_in_scope which
		// has not been crawled
		System.out.println("Start extracting...");

		Statement stmt = null;

		try {
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			String querySql = "select post_id, user_id, content from "
					+ this.postTableName
					+ " where post_id not in (select distinct post_id from "
					+ this.semanticTableName + " )";
			System.out.println(querySql);
			ResultSet postRS = stmt.executeQuery(querySql);
			int npPosts = 0;
			while (postRS.next()) {
				String post_id = postRS.getString("post_id");
				String user_id = postRS.getString("user_id");
				String content = postRS.getString("content");
				npPosts++;
				System.out.println("Processing number: " + npPosts);
				PostSemantic ps = new PostSemantic(post_id, user_id, content);
				// step2: pre-process the text
				ps = preProcess(ps);
				// step3: crawl the content of this post
				ps = crawlSemantic(ps);
				// step4: store it into the semantic table
				storeSemantic(ps);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private PostSemantic preProcess(PostSemantic ps) {
		String content = ps.getContent();
		System.out.println(content);
		content = content.replaceAll("<U+.*?>", "");
		content = removeEmojiAndSymbolFromString(content);
		System.out.println(content);
		ps.setContent(content);
		return ps;
	}

	private void storeSemantic(PostSemantic ps) {
		if (ps.getJson() == null || "".equals(ps.getJson())) {
			return;
		}
		System.out.println("[Store pS] " + ps.toString());
		String sql = "insert into " + this.semanticTableName
				+ " (user_id, post_id, content, json) values (?,?,?,?)";
		try {
			PreparedStatement inserSemantic = con.prepareStatement(sql);
			inserSemantic.setString(1, ps.getUser_id());
			inserSemantic.setString(2, ps.getPost_id());
			inserSemantic.setString(3, ps.getContent());
			inserSemantic.setString(4, ps.getJson());
			inserSemantic.executeUpdate();

			inserSemantic.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
	}

	private PostSemantic crawlSemantic(PostSemantic ps) {
		String content = ps.getContent();
		String json = LtpCloud.getInstance().detectForJson(content);
		ps.setJson(json);
		return ps;
	}

	private String removeEmojiAndSymbolFromString(String content) {
		String utf8tweet = "";
		try {
			byte[] utf8Bytes = content.getBytes("UTF-8");

			utf8tweet = new String(utf8Bytes, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Pattern unicodeOutliers = Pattern
				.compile(
						"[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
						Pattern.UNICODE_CASE | Pattern.CANON_EQ
								| Pattern.CASE_INSENSITIVE);
		Matcher unicodeOutlierMatcher = unicodeOutliers.matcher(utf8tweet);

		utf8tweet = unicodeOutlierMatcher.replaceAll(" ");
		return utf8tweet;
	}

}
