package model;

import java.util.ArrayList;

public interface RentDao {
	
	
	public void rentVideo (String tel, int vNum) throws Exception;		// 전화번호, 비디오 번호 입력시 비디오 대여가 가능한 인터페이스
	
	
	public void returnVideo (int vNum) throws Exception;				// 비디오번호에 따라 반납이 가능한 인터페이스
	
	
	public ArrayList unpaidList () throws Exception;					// 비디오 미납목록 생성 인터페이스
	
	
	public String selectName (String tel) throws Exception;				// 전화번호에 따른 고객성명 호출 인터페이스

}
