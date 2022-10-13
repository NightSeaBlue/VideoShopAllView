package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.CustomerDao;
import model.vo.CustomerVO;

public class CustomerDaoImpl implements CustomerDao{
	// 필드 선언
	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver";
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.164:1521:xe";
	final static String USER	="BKjeon";
	final static String PASS	="jeon";


	/*
	 * 생성자 : CustomerDaoImpl
	 * 인자 : 없음
	 * 역할 : 드라이버 연결
	 */

	public CustomerDaoImpl() throws Exception{
		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("고객 관리 드라이버 로딩 성공");


	}//CustomerDaoImpl

	/*
	 * 함수명 : insertCustomer
	 * 인자 : CustomerVO vo
	 * 리턴값 : void
	 * 역할 : 회원정보입력 (회원가입)
	 * 
	 */

	public void insertCustomer(CustomerVO vo) throws Exception{
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			System.out.println("DB 연결 성공");

			// 3. sql 문장 만들기
			String sql = "INSERT INTO cust(tel,addtel,name,addr,email)  "
					+ "	VALUES(?,?,?,?,?) ";
			// 4. sql 전송객체 (PreparedStatement)	
			ps = con.prepareStatement(sql);
			ps.setString(1, vo.getCustTel1());
			ps.setString(2, vo.getCustTel2());
			ps.setString(3, vo.getCustName());
			ps.setString(4, vo.getCustAddr());
			ps.setString(5, vo.getCustEmail());
			// 5. sql 전송
			ps.executeUpdate();
		} finally {
			// 6. 닫기 
			ps.close();
			con.close();
		}

	}// insertCustomer

	/*
	 *  함수명 : selectByTel
	 *  인자 : 전화번호
	 *  리턴값 : CustomerVO dao
	 *  역할 : 사용자가 입력한 전화번호에 따라 CustomerVO 값을 리턴
	 */

	public CustomerVO selectByTel(String tel) throws Exception{
		// 2. 연결 객체 얻어오기 (try, catch 외부에서 지정)
		Connection con = null;
		PreparedStatement ps =  null;
		CustomerVO dao = new CustomerVO();

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. sql 문장
			String sql = "SELECT * FROM cust WHERE tel=?";
			//4. 전송객체 얻어오기
			ps = con.prepareStatement(sql);
			ps.setString(1, tel);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				dao.setCustName(rs.getString("NAME"));
				dao.setCustTel1(rs.getString("TEL"));
				dao.setCustTel2(rs.getString("ADDTEL"));
				dao.setCustAddr(rs.getString("ADDR"));
				dao.setCustEmail(rs.getString("EMAIL"));
			} 

		} finally {
			//6. 닫기
			ps.close();
			con.close();
		} // end of try~finally
		return dao;

	} // end of select by tel

	/*
	 *  함수명 : updateCustomer
	 *  인자 : CustomerVO vo
	 *  리턴값 : 정수
	 *  역할 : 새로운 CustomerVO로 회원정보 수정
	 */

	public int updateCustomer(CustomerVO vo) throws Exception{

		// 2. 연결 객체 얻어오기 (try, catch 외부에서 지정)
		Connection con = null;			// 연결객체
		PreparedStatement ps =  null;	// 전송객체
		int result = 0;					// 수정한 행 수

		try {
			con = DriverManager.getConnection(URL,USER,PASS);

			// 3. sql 문장
			String sql = "UPDATE cust SET tel=?, addtel=?, name=?, addr=?, email=? WHERE tel=?";

			// 4. 전송객체 얻어오기
			ps = con.prepareStatement(sql);
			ps.setString(1, vo.getCustTel1());
			ps.setString(2, vo.getCustTel2());
			ps.setString(3, vo.getCustName());
			ps.setString(4, vo.getCustAddr());
			ps.setString(5, vo.getCustEmail());
			ps.setString(6, vo.getCustTel1());

			//5. 객체 전송
			result = ps.executeUpdate();

		} finally {
			ps.close();
			con.close();
		}

		return result;
	}// end of Update Customer

	/*
	 *  함수명 : selectByName
	 *  인자 : 이름
	 *  리턴값 : CustomerVO dao
	 *  역할 : 사용자가 입력한 이름에 따라 CustomerVO 값을 리턴
	 */

	@Override
	public ArrayList <String> selectByName(String name) throws Exception {
		// 2. 연결 객체 얻어오기 (try, catch 외부에서 지정)
		Connection con = null;
		PreparedStatement ps =  null;
		ArrayList<String> list = new ArrayList<String>();

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. sql 문장
			String sql = "SELECT tel FROM cust WHERE name=?";
			//4. 전송객체 얻어오기
			ps = con.prepareStatement(sql);
			ps.setString(1, name);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				list.add(rs.getString("TEL"));
			} 

		} finally {
			//6. 닫기
			ps.close();
			con.close();
		} // end of try~finally
		return list;

	}// end of select By Name


} // end of Customer DAO
