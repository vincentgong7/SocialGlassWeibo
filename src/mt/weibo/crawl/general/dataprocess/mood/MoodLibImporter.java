package mt.weibo.crawl.general.dataprocess.mood;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mt.weibo.common.MyLineReader;
import mt.weibo.db.MyDBConnection;

public class MoodLibImporter {

	private MyDBConnection mdbc;
	private String moodLibTablename = "socialmedia.mood_lib";
	private String moodLibFilename;
	
	public MoodLibImporter(String moodLibTablename, String moodLibFilename) {
		// TODO Auto-generated constructor stub
		this.moodLibFilename = moodLibFilename;
		this.moodLibTablename = moodLibTablename;
		
		if (mdbc == null) {
			mdbc = new MyDBConnection();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String moodLibTablename = "socialmedia.mood_lib";
		String moodLibFilename = "/Users/vincentgong/Documents/TUD/Master TUD/A Master Thesis/share/configuration/mood_lib/moods.csv";
		MoodLibImporter mli = new MoodLibImporter(moodLibTablename, moodLibFilename);
		
		mli.process();
		mli.close();
	}

	private void process() {
		int id = 0;
		try {
			MyLineReader mlr = new MyLineReader(this.moodLibFilename);
			while(mlr.hasNextLine()){
				String line = mlr.nextLine().trim();
				if(line!=null && !"".equals(line)){
					id++;
					store(line, id);
				}
			}
			mlr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void store(String line, int id) {
		if(line!=null && !"".equals(line)){
			String[] item = line.split("\t");
			//词语	词性种类	情感分类	强度	极性	
			//脏乱	adj	      NN	7	 2		
			String wid = String.valueOf(id);
			String text = item[0];
			String pos = item[1];
			String intensityStr = item[3];
			int intensity = 0;
			if(intensityStr!=null & !"".equals(intensityStr)){
				intensity = Integer.valueOf(intensityStr);
			}
			String category = item[2];
			String polarityStr = item[4];
			int polarity = 0;
			if(polarityStr!=null & !"".equals(polarityStr)){
				polarity = Integer.valueOf(polarityStr);
			}
			
			PreparedStatement preparedStatement = null;
			String insertTableSQL = "INSERT INTO "
					+ this.moodLibTablename
					+ "(id, text, pos, category, intensity, polarity)"
					+ " VALUES"
					+ "(?,?,?,?,?,?)";
			
			try {
				preparedStatement = mdbc.getPrepareStatement(insertTableSQL);
				preparedStatement.setString(1, wid);
				preparedStatement.setString(2, text);
				preparedStatement.setString(3, pos);
				preparedStatement.setString(4, category);
				preparedStatement.setInt(5, intensity);
				preparedStatement.setInt(6, polarity);
				
				// execute insert SQL stetement
				preparedStatement.executeUpdate();
				preparedStatement.close();
				System.out.println(line);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if(this.mdbc!=null){
			this.mdbc.close();
		}
	}
	
}
