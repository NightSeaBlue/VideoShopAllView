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

	public RentDaoImpl() throws Exception{
		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("대여 관리 드라이버 로딩 성공");


	} // end of RentDaoImpl

	@Override
	public void rentVideo(String tel, int vNum) throws Exception {
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		PreparedStatement ps2 = null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);

			// 대여 중인지 확인
			String sql2 = " SELECT vidno FROM rent WHERE vidno = "+vNum;
			ps2 = con.prepareStatement(sql2);
			ResultSet rs = ps2.executeQuery();

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
		}	

	}// end of returnVideo

	@Override
	public ArrayList unpaidList() throws Exception {
		ArrayList data = new ArrayList ();
		//2. 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. SQL 문장
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
