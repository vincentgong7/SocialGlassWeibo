package mt.weibo.model;

public class Word {
	private int id;
	private String text; // cont
	private String partOfSpeech; // pos, character of a word in a sentence
	private String ner; // ne, name entity recognition
	private int nerCategory; // the category of the name entity
	private int parent; // parent
	private String relate;
	private String arg;
	public static final String[] NER_TYPE = {"NULL", "NUMBER", "ORGANIZATION", "PEOPLE", "TIME", "DATE", "NOUN"};

	/* NER return field
	 * Nm=数词 Ni=机构名 Ns=机构名 Nh=人名 Nt=时间 Nr=日期 Nz=专有名词
	 * O 表示这个词不是 NE
	 * S 表示这个词单独构成一个 NE 
	 * B 表示这个词为一个 NE 的开始 
	 * I 表示这个词为一个 NE 的中间 
	 * E 表示这个词位一个 NE 的结尾
	 * 
	 * eg. 巴格达 南部 地区
	 *     B-Ns I-Ns E-Ns
	 * */
	public Word(int id, String cont, String pos, String ne, int nerCategory, int parent,
			String relate, String arg) {
		this.id = id;
		this.text = cont;
		this.partOfSpeech = pos;
		this.ner = ne;
		this.nerCategory = nerCategory;
		this.parent = parent;
		this.relate = relate;
		this.arg = arg;
	}

	public String toString() {
		return "id=" + this.id + ", text=" + this.text + ", pos="
				+ this.partOfSpeech + ", ner=" + this.ner + ", nerCategory=" + Word.NER_TYPE[nerCategory] +", parent="
				+ this.parent + ", relate=" + this.relate + ", arg=" + this.arg;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public String getNer() {
		return ner;
	}

	public void setNer(String ner) {
		this.ner = ner;
	}

	public int getNerCategory() {
		return nerCategory;
	}

	public void setNerCategory(int nerCategory) {
		this.nerCategory = nerCategory;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public String getRelate() {
		return relate;
	}

	public void setRelate(String relate) {
		this.relate = relate;
	}

	public String getArg() {
		return arg;
	}

	public void setArg(String arg) {
		this.arg = arg;
	}
}
