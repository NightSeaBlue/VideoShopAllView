package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import model.RentDao;


public class RentDaoImpl implements RentDao {

	// 필드 선언
	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver";
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.164:1521:xe";
	final static String USER	="BKjeon";
	final static String PASS	="jeon";

	Connection con;
	
	/*
	 * 생성자 : RentDaoImpl
	 * 역할 : 드라이버 로딩
	 */

	public RentDaoImpl() throws Exception{
		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("대여 관리 드라이버 로딩 성공");


	} // end of RentDaoImpl
	
	/*
	 *  함수 : rentVideo
	 *  인자 : 고객의 전화번호, 비디오 번호
	 *  리턴값 : 비디오 정보
	 *  역할 : 고객의 전화번호와 비디오번호를 입력받아 , 고객이 대여하고자 하는 비디오 정보 호출
	 */
	
	@Override
	public void rentVideo(String tel, int vNum) throws Exception {
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		PreparedStatement ps2 = null;

		try {
			// 연결
			con = DriverManager.getConnection(URL,USER,PASS);

			// 비디오 대여여부 확인 (Rent table 에서 입력한 비디오 번호로 된 비디오가 있는지 확인)
			String sql2 = " SELECT vidno FROM rent WHERE vidno = "+vNum;
			ps2 = con.prepareStatement(sql2);
			ResultSet rs = ps2.executeQuery();
			
			// ResultSet의 결과값이 있는 경우, 대여중인 비디오(Rent table에 해당 Video Number가 이미 있으므로, 없을 경우
			// 대여 진행(Rent 테이블에 정보 입력)
			if (!rs.next()) {
				// sql 문장
				String sql = "INSERT INTO rent(rentno,vidno,tel,ret) "
						+ "  VALUES(seq_rent.nextval, "+vNum+" ,?,'N')";

				// 4. sql 전송객체 (PreparedStatement)	
				ps = con.prepareStatement(sql);
				ps.setString(1, tel);

				// 5. 전송
				ps.executeUpdate();
				ps.close();
				JOptionPane.showMessageDialog(null, "대여");
			}else if(rs.next()) {
				JOptionPane.showMessageDialog(null, "대여중인 비디오 입니다");
			}
			rs.close();
		} finally {
			ps2.close();
			con.close();
		}		

	}// end of rentVideo

	/*
	 * 함수명 : returnVideo
	 * 인자 : 비디오 번호 (Rent 테이블의)
	 * 리턴값 : Rent 테이블에서 해당 비디오 정보
	 * 역할 : Rent 테이블의 비디오 번호 입력시, 테이블의 반납여부 정보 변경
	 */
	
	@Override
	public void returnVideo(int vNum) throws Exception {
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		try {
			con = DriverManager.getConnection(URL,USER,PASS);	

			//	sql 문장
			String sql = " UPDATE rent SET ret = 'Y' where vidno = ? AND ret='N'";	
			// sql 전송객체 (PreparedStatement)	
			ps = con.prepareStatement(sql);
			ps.setString(1, String.valueOf(vNum));

			// 전송
			ps.executeUpdate();

		} finally {
			ps.close();
			con.close();
		}// end of Try ~ Finally	

	}// end of returnVideo
	
	/*
	 *  함수명 : unpaidList (미납목록)
	 *  인자 : 각 테이블에 입력되어 있는 고객 정보 및 비디오 정보
	 *  리턴값 : JTable에 해당 정보 표시
	 *  역할 : 각 정보들을 한번에 볼 수 있도록 표시
	 */
	
	@Override
	public ArrayList unpaidList() throws Exception {
		ArrayList data = new ArrayList ();
		//2. 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. SQL 문장
			/*
			 * 현재 구성되어 있는 테이블이 Rent(대여)/Cust(고객)/Video(비디오)/Vid_info(비디오(영상) 정보) 로 나뉘어져 있어, 이들에서 겹치는 값을 찾고자 각 테이블을
			 * INNER Join에 값을 찾음.
			 * 
			 * 미납 목록이므로, 반납여부에서 N으로 표기가 되어 있는 것들을 찾아야 함
			 */
			String sql = "SELECT V.VIDNO AS VIDNO ,O.TIT AS TIT , C.NAME AS NAME ,C.TEL AS TEL, R.RENTDATE+3 AS RETURNDATE, R.RET AS RET "
					+ " FROM CUST  C INNER JOIN RENT R "
					+ " ON C.TEL = R.TEL "
					+ " INNER JOIN VIDEO V "
					+ " ON R.VIDNO = V.VIDNO "
					+ " INNER JOIN VID_INFO O "
					+ " ON V.INFONO = O.INFONO "
					+ " WHERE R.RET = 'N'  ";

			//4. 전송객체
			ps = con.prepareStatement(sql);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				// ResultSet에 데이터가 있을 경우 다음과 같이 데이터를 ArrayList에 할당
				ArrayList temp = new ArrayList ();
				temp.add(rs.getInt("VIDNO"));
				temp.add(rs.getString("TIT"));
				temp.add(rs.getString("NAME"));
				temp.add(rs.getString("TEL"));
				temp.add(rs.getString("RETURNDATE"));
				temp.add(rs.getString("RET"));	
				data.add(temp);
			}
			rs.close();

		} finally {
			// 종료
			ps.close();
			con.close();
		}
		return data;
	}// end of Select Video
	
	
	/*
	 *  함수명 : selectName
	 *  인자 : 고객의 전화번호
	 *  리턴값 : 고객의 이름
	 *  역할 : 입력한 고객의 전화번호와 일치하는 고객의 성명 호출(동명이인 정보 감지)
	 */

	@Override
	public String selectName(String tel) throws Exception {
		String name = null;
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);

			//	sql 문장
			String sql = " SELECT name FROM cust WHERE tel=?";	
			// sql 전송객체 (PreparedStatement)	
			ps = con.prepareStatement(sql);
			ps.setString(1, tel);

			// 전송
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				name = rs.getString("NAME");
			}

		} finally {
			ps.close();
			con.close();
		}	
		return name;
	}// end of SelectName


}
