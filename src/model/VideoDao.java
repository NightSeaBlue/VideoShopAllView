package model;

import java.util.ArrayList;

import model.vo.VideoVO;

public interface VideoDao {
	public void 		insertVideo(VideoVO vo, int count) throws Exception;	// 비디오 번호와 , 카운트를 인자로 비디오 정보 입력
	public ArrayList 	selectVideo (int idx , String word) throws Exception;	// 인덱스와 입력한 단어로 비디오 정보 검색
	public VideoVO 		selectByVnum(int vNum) throws Exception;				// 비디오 번호로 비디오 정보 검색
	public int			modifyVideo(VideoVO vo) throws Exception;				// 비디오 정보를 전달받아 입력되어 있는 비디오 정보 수정
	public int			deleteVideo(int vNum) throws Exception;					// 비디오 번호를 선택했을 때 해당하는 비디오 정보 삭제
}
