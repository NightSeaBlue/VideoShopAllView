package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import model.VideoDao;
import model.vo.VideoVO;

public class VideoDaoImpl implements VideoDao{

	// 필드 선언
	final static String DRIVER 	="oracle.jdbc.driver.OracleDriver";
	final static String URL 	= "jdbc:oracle:thin:@192.168.0.164:1521:xe";
	final static String USER	="BKjeon";
	final static String PASS	="jeon";

	public VideoDaoImpl() throws Exception{

		// 1. 드라이버로딩
		Class.forName(DRIVER);
		System.out.println("비디오 관리 드라이버 로딩 성공");


	}



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

	/*
	 *  함수명 : selectVideo
	 *  인자	: index(dialog) , word
	 *  리턴값 : 비디오 정보
	 *  역할 : 감독 또는 제목으로 검색시, 비디오 테이블에 있는 결과 호출
	 */


	@Override
	public ArrayList selectVideo(int idx, String word) throws Exception {
		ArrayList data = new ArrayList ();
		//2. 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;

		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. SQL 문장
			String []colNames = {"tit","dir"};
			//sql 구문을 완벽히 작성해서 넘겨줘야 함. (?,? 로 세팅할 경우 이를 인지하지 못함
			String sql = "SELECT vidno ,tit ,dir ,act	 "
					+ "	FROM video v LEFT OUTER JOIN vid_info o	"
					+ "	ON v.infono = o.infono "
					+ "	WHERE "+ colNames[idx] + " LIKE '%"+ word +"%' ";

			//4. 전송객체
			ps = con.prepareStatement(sql);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {

				// ResultSet에 데이터가 있을 경우 다음과 같이 데이터를 ArrayList에 할당
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



	@Override
	public VideoVO selectByVnum(int vNum) throws Exception {
		VideoVO vo = new VideoVO ();
		//2. 연결객체 얻어오기
		Connection con = null;
		PreparedStatement ps =  null;
		try {
			con = DriverManager.getConnection(URL,USER,PASS);
			//3. sql 문장
			String sql = "SELECT * "
					+ "	FROM video v LEFT OUTER JOIN vid_info o	"
					+ "	ON v.infono = o.infono "
					+ "	WHERE vidno = "+String.valueOf(vNum);


			//4. 전송객체
			ps = con.prepareStatement(sql);

			//5. 전송
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				// ResultSet 에 data가 있을 경우, 이들을 VideoVO vo에 할당함
				vo.setVidno(rs.getInt("vidno"));
				vo.setTit(rs.getString("tit"));
				vo.setDir(rs.getString("dir"));
				vo.setGen(rs.getString("gen"));
				vo.setAct(rs.getString("act"));
				vo.setSumm(rs.getString("summ"));
			}
			rs.close();

		} finally {
			ps.close();
			con.close();
		}
		return vo;
	}// end of select By Vnum



	@Override
	public int modifyVideo(VideoVO vo) throws Exception {
		//2. 연결객체 얻어오기
		Connection con = null;			// 연결객체
		PreparedStatement ps =  null;	// 전송객체
		int result = 0;					// 수정한 행 수

		try {
			// 연결
			con = DriverManager.getConnection(URL,USER,PASS);

			// sql 문장
			String sql = "UPDATE vid_info "
					+ " SET DIR=?, TIT=?, ACT=?, GEN=?, SUMM=?"
					+ "	WHERE infono = ("+"SELECT infono "
					+ " FROM video "
					+ "	WHERE vidno = "+String.valueOf(vo.getVidno())+")";

			// 전송 객체 설정
			ps = con.prepareStatement(sql);
			ps.setString(1, vo.getDir());
			ps.setString(2, vo.getTit());
			ps.setString(3, vo.getAct());
			ps.setString(4, vo.getGen());
			ps.setString(5, vo.getSumm());

			// 전송
			result = ps.executeUpdate();


		} finally {
			ps.close();
			con.close();
		}

		return result;
	}



	@Override
	public int deleteVideo(int vNum) throws Exception {

		//2. 연결객체 얻어오기
		Connection con = null;			// 연결객체
		PreparedStatement ps =  null;	// 전송객체
		
		int result = 0;					// 수정한 행 수
		try {

			// 연결
			con = DriverManager.getConnection(URL,USER,PASS);
			
			// sql 문장
			String sql = "DELETE FROM video "
					+ " where vidno ="+vNum;
			// 전송객체
			ps = con.prepareStatement(sql);
			
			// 전송
			result = ps.executeUpdate();
					

		} finally {
			ps.close();
			con.close();
		}

		return result;
	}// end of Delete Video


}
