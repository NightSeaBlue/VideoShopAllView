package model.vo;

public class VideoVO {
	
	int vidno;					// 비디오번호
	String gen;					// 장르
	String tit;					// 비디오명
	String dir;					// 감독
	String act;					// 배우
	String summ;				// 설명
	
	public int getVidno() {
		return vidno;
	}
	public void setVidno(int vidno) {
		this.vidno = vidno;
	}
	public String getGen() {
		return gen;
	}
	public void setGen(String gen) {
		this.gen = gen;
	}
	public String getTit() {
		return tit;
	}
	public void setTit(String tit) {
		this.tit = tit;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public String getAct() {
		return act;
	}
	public void setAct(String act) {
		this.act = act;
	}
	public String getSumm() {
		return summ;
	}
	public void setSumm(String summ) {
		this.summ = summ;
	}

	

	

}
