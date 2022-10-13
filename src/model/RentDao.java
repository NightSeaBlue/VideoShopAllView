package model;

import java.util.ArrayList;

public interface RentDao {
	
	// 대여
	public void rentVideo (String tel, int vNum) throws Exception;
	
	// 반납
	public void returnVideo (int vNum) throws Exception;
	
	// 미납목록검색
	public ArrayList unpaidList () throws Exception;
	
	// 전화번호 입력시 이름 호출
	public String selectName (String tel) throws Exception;

}
