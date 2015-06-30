/**
 * 
 */
package mt.weibo.common;

import java.io.File;

/**
 * @author Vinent GONG
 *
 */
public class MyFolderLineReaderBatch {

	private File[] fileList;
	private IMyFolderLineReaderProcessor processor;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MyFolderLineReaderBatch mflr = new MyFolderLineReaderBatch();
		mflr.init("D:/documents/Dropbox/TUD/Master TUD/A Master Thesis/share/exp/Test/");

		mflr.setProcessor(new IMyFolderLineReaderProcessor(){
			int i = 0;
			@Override
			public void processor(String line, File f) {
				// TODO Auto-generated method stub
				System.out.println("File: "+f.getName()+", "+line);
				i++;
				System.out.println(i);
			}
			
		});
		mflr.process();
	}

	private void process() throws Exception {
		for(File f:this.fileList){
			MyLineReader mfr = new MyLineReader(f);
			mfr.init();
			while(mfr.hasNextLine()){
				String line = mfr.nextLine();
				this.processor.processor(line, f);
			}
			mfr.close();
		}
		
	}

	private void init(String folderPath) throws Exception {
		File folder = new File(folderPath);
		if(!folder.exists()){
			throw new Exception("Folder not found." + folder.getName()); 
		}
		if(folder.isDirectory()){
			this.fileList = folder.listFiles();
		}else{
			this.fileList = new File[]{folder};
		}
		
	}

	private void setProcessor(
			IMyFolderLineReaderProcessor processor) {
		// TODO Auto-generated method stub
		this.processor = processor;
	}
	
	

}
