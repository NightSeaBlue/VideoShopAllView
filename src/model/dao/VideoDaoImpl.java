package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.VideoDao;
import model.vo.VideoVO;

public class VideoDaoImpl implements VideoDao{

	// 변수 선언
	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver";
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.164:1521:xe";
	final static String USER	="BKjeon";
	final static String PASS	="jeon";

	public VideoDaoImpl() throws Exception{

		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("비디오 관리 드라이버 로딩 성공");


	}


	@SuppressWarnings("resource")
	public void insertVideo(VideoVO vo, int count) throws Exception{
		// 2. Connection 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		try {
			con = DriverManager.getConnection(URL,USER,PASS);


			// 3. sql 문장 만들기
			String sql = "INSERT INTO vid_info(infono,dir,tit,act,gen,summ) VALUES (seq_infono.nextval,?,?,?,?,?)";
			String sql2 = " INSERT INTO video(vidno,infono) VALUES (seq_videono.nextval,seq_infono.currval) ";
			// 4-1. sql 전송객체 (PreparedStatement)
			ps = con.prepareStatement(sql);
			ps.setString(1, vo.getTit());
			ps.setString(2, vo.getDir());
			ps.setString(3, vo.getAct());
			ps.setString(4, vo.getGen());
			ps.setString(5, vo.getSumm());

			// 5-1. sql 전송
			ps.executeUpdate();

			//4-2 sql2 전송객체
			ps = con.prepareStatement(sql2);

			//5-2 sql2 전송
			for (int i = 0 ; i <count; i++) {
				ps.executeUpdate();
			}

		} finally {

			// 6. 닫기
			ps.close();
			con.close();
		}
	}// end of insertVideo


	
	@Override
	public ArrayList selectVideo() throws Exception {
		ArrayList data = new ArrayList ();
		//2. 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. SQL 문장
			String sql = "SELECT vidno ,tit ,dir ,act	 "
					+ "	FROM vid_info v LEFT OUTER JOIN video o	"
					+ "	ON v.infono = o.infono ";
			//4. 전송객체
			ps = con.prepareStatement(sql);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				ArrayList temp = new ArrayList ();
				temp.add(rs.getInt("vidno"));
				temp.add(rs.getString("tit"));
				temp.add(rs.getString("dir"));
				temp.add(rs.getString("act"));	
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


}
