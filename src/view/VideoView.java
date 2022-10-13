package	 view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import model.VideoDao;
import model.dao.VideoDaoImpl;
import model.vo.VideoVO;


public class VideoView extends JPanel 
{	
	//	member field
	JTextField	tfVideoNum, tfVideoTitle, tfVideoDirector, tfVideoActor;
	JComboBox	comVideoJanre;
	JTextArea	taVideoContent;

	JCheckBox	cbMultiInsert;
	JTextField	tfInsertCount;

	JButton		bVideoInsert, bVideoModify, bVideoDelete;

	JComboBox	comVideoSearch;
	JTextField	tfVideoSearch;
	JTable		tableVideo;

	VideoTableModel tbModelVideo;

	// 비즈니스로직
	VideoDao model;



	//##############################################
	//	constructor method
	public VideoView(){
		addLayout(); 	// 화면설계
		initStyle();
		eventProc();
		connectDB();	// DB연결
	}

	public void connectDB(){	// DB연결
		try {
			model = new VideoDaoImpl();
			System.out.println("DB연결 성공");
		} catch (Exception e) {
			System.out.println("DB연결 실패 : "+e.getMessage());
			e.printStackTrace();
		}
	}

	public void eventProc(){
		// 체크박스가 눌렸을 때 tfInseftCount 가 쓸수있게됨
		cbMultiInsert.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ev){
				/*	if(cbMultiInsert.isSelected()){
					tfInsertCount.setEditable(true);
				}
				else
					tfInsertCount.setEditable(false);*/

				tfInsertCount.setEditable( cbMultiInsert.isSelected() );
			}						
		});	

		ButtonEventHandler btnHandler = new ButtonEventHandler();

		// 이벤트 등록
		bVideoInsert.addActionListener(btnHandler);
		bVideoModify.addActionListener(btnHandler);
		bVideoDelete.addActionListener(btnHandler);
		tfVideoSearch.addActionListener(btnHandler);
		
		// 검색한 열을 클릭했을 때

		tableVideo.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev){

				try{
					int row = tableVideo.getSelectedRow();
					int col = 0;	// 검색한 열을 클릭했을 때 클릭한 열의 비디오번호
					// Object -> Integer -> int 형변환
					int vNum = ((Integer)tableVideo.getValueAt(row, col)).intValue();
					JOptionPane.showMessageDialog(null, vNum);
					
					VideoVO vo = model.selectByVnum(vNum);
					//화면에 비디오정보의 값들을 각각 출력
					tfVideoNum.setText(String.valueOf(vo.getVidno()));
					tfVideoTitle.setText(vo.getTit());
					tfVideoDirector.setText(vo.getDir());
					tfVideoActor.setText(vo.getAct());
					taVideoContent.setText(vo.getSumm());
					comVideoJanre.setSelectedItem(vo.getGen());
					
				}catch(Exception ex){
					System.out.println("실패 : "+ ex.getMessage());
				}

			}// end of mouse Clicked
		});
	}// end of Event Proc		

	// 버튼 이벤트 핸들러 만들기
	class ButtonEventHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			Object o = ev.getSource();

			if(o==bVideoInsert){  
				registVideo();					// 비디오 등록
			}
			else if(o==bVideoModify){  
				modifyVideo();					// 비디오 정보 수정
			}
			else if(o==bVideoDelete){  
				deleteVideo();					// 비디오 정보 삭제
			}
			else if(o==tfVideoSearch){
				searchVideo();					// 비디오 검색
			}
		}
	}

	// 입고 클릭시  - 비디오 정보 등록
	public void registVideo(){

		//1. 사용자 입력값 얻어오기
		String gen = (String) comVideoJanre.getSelectedItem();
		int count = Integer.parseInt(tfInsertCount.getText());
		String tit = tfVideoTitle.getText();
		String dir = tfVideoDirector.getText();
		String act = tfVideoActor.getText();
		String summ = taVideoContent.getText();


		// 2. 1번의 값을 각각 VideoVO 에 지정
		VideoVO vo = new VideoVO ();
		vo.setGen(gen);
		vo.setTit(tit);
		vo.setAct(act);
		vo.setSumm(summ);
		vo.setDir(dir);


		//3. 모델의 insertVideo() 호출
		try {
			model.insertVideo(vo, count);
			JOptionPane.showMessageDialog(null, "입고");
			//4. 화면에 지우기
			clearText();
			//5. 체크박스 초기화
			tfInsertCount.setEditable(false);

		} catch (Exception e) {
			System.out.println("비디오 입고 실패"+e.getMessage());
			e.printStackTrace();
		}

	}//registVideo

	/*
	 * 함수명 : clearText
	 * 인자 : 없음
	 * 리턴값 : void
	 * 역할 : 텍스트필드 초기화
	 * 
	 */
	public void clearText() {
		tfVideoNum.setText(null);
		tfVideoTitle.setText(null);
		tfVideoDirector.setText(null);
		tfInsertCount.setText(null);
		tfVideoActor.setText(null);
		taVideoContent.setText(null);
	}

	public void initStyle(){   
		tfVideoNum.setEditable(false); // 입력하지 못하게 만듬.
		tfInsertCount.setEditable(false);

		tfInsertCount.setHorizontalAlignment(JTextField.RIGHT);
	}

	// 수정 클릭시 - 비디오 정보 수정
	public void modifyVideo(){
		// 1. 새로운 VideoVO 생성
		VideoVO vo = new VideoVO () ;
		vo.setGen((String)comVideoJanre.getSelectedItem());
		vo.setTit(tfVideoTitle.getText());
		vo.setDir(tfVideoDirector.getText());
		vo.setAct(tfVideoActor.getText());
		vo.setSumm(taVideoContent.getText());
		vo.setVidno(Integer.parseInt(tfVideoNum.getText()));
		
		//2. model의 modifyVideo 호출
		try {
			int md =model.modifyVideo(vo);
			System.out.println(md+"행만큼 수정 완료");
			JOptionPane.showMessageDialog(null, "수정");
		} catch (Exception e) {
			System.out.println("수정 불가"+e.getMessage());
			e.printStackTrace();
		}
		
	}// end of Modify Video

	// 삭제 클릭시 - 비디오 정보 삭제
	public void deleteVideo(){
		// 입력한 video number 가져오기
		int vNum = Integer.parseInt(tfVideoNum.getText());
		
		//2. model의 deleteVideo 호출
		try {
			 int result = model.deleteVideo(vNum);
			 System.out.println(result+"행만큼 삭제");
		} catch (Exception e) {
			System.out.println("오류 발생"+e.getMessage());
		}

		JOptionPane.showMessageDialog(null, "삭제");
	}// end of 

	// 비디오현황검색
	public void searchVideo(){
		try {
			// 사용자가 선택하거나 입력한 값을 얻어오기
			int idx = comVideoSearch.getSelectedIndex();
			String word = tfVideoSearch.getText();
			tbModelVideo.data= model.selectVideo(idx,word);
			tbModelVideo.fireTableDataChanged();		// 모델쪽에서 데이터가 변경되었음을 뷰쪽으로 알려줌.
			JOptionPane.showMessageDialog(null, "검색");
			
		} catch(Exception e) {
			System.out.println("검색 실패 : "+e.getMessage());
		}
		
	}// end of searchVideo


	//  화면설계 메소드
	public void addLayout(){
		//멤버변수의 객체 생성
		tfVideoNum = new JTextField();
		tfVideoTitle = new JTextField();
		tfVideoDirector = new JTextField();
		tfVideoActor = new JTextField();

		String []cbJanreStr = {"멜로","엑션","스릴","코미디"};
		comVideoJanre = new JComboBox(cbJanreStr);
		taVideoContent = new JTextArea();

		cbMultiInsert = new JCheckBox("다중입고");
		tfInsertCount = new JTextField("1",5);

		bVideoInsert = new JButton("입고");
		bVideoModify = new JButton("수정");
		bVideoDelete = new JButton("삭제");

		String []cbVideoSearch = {"제목","감독"};
		comVideoSearch = new JComboBox(cbVideoSearch);
		tfVideoSearch = new JTextField(15);

		tbModelVideo = new VideoTableModel();
		tableVideo = new JTable(tbModelVideo);
		// tableVideo.setModel(tbModelVideo);



		//************화면구성************
		//왼쪽영역
		JPanel p_west = new JPanel();
		p_west.setLayout(new BorderLayout());
		// 왼쪽 가운데
		JPanel p_west_center = new JPanel();	
		p_west_center.setLayout(new BorderLayout());
		// 왼쪽 가운데의 윗쪽
		JPanel p_west_center_north = new JPanel();
		p_west_center_north.setLayout(new GridLayout(5,2));
		p_west_center_north.add(new JLabel("비디오번호"));
		p_west_center_north.add(tfVideoNum);
		p_west_center_north.add(new JLabel("장르"));
		p_west_center_north.add(comVideoJanre);
		p_west_center_north.add(new JLabel("제목"));
		p_west_center_north.add(tfVideoTitle);
		p_west_center_north.add(new JLabel("감독"));
		p_west_center_north.add(tfVideoDirector);
		p_west_center_north.add(new JLabel("배우"));
		p_west_center_north.add(tfVideoActor);

		// 왼쪽 가운데의 가운데
		JPanel p_west_center_center = new JPanel();
		p_west_center_center.setLayout(new BorderLayout());
		// BorderLayout은 영역 설정도 해야함
		p_west_center_center.add(new JLabel("설명"),BorderLayout.WEST);
		p_west_center_center.add(taVideoContent,BorderLayout.CENTER);

		// 왼쪽 화면에 붙이기
		p_west_center.add(p_west_center_north,BorderLayout.NORTH);
		p_west_center.add(p_west_center_center,BorderLayout.CENTER);
		p_west_center.setBorder(new TitledBorder("비디오 정보입력"));

		// 왼쪽 아래
		JPanel p_west_south = new JPanel();		
		p_west_south.setLayout(new GridLayout(2,1));

		JPanel p_west_south_1 = new JPanel();
		p_west_south_1.setLayout(new FlowLayout());
		p_west_south_1.add(cbMultiInsert);
		p_west_south_1.add(tfInsertCount);
		p_west_south_1.add(new JLabel("개"));
		p_west_south_1.setBorder(new TitledBorder("다중입력시 선택하시오"));
		// 입력 수정 삭제 버튼 붙이기
		JPanel p_west_south_2 = new JPanel();
		p_west_south_2.setLayout(new GridLayout(1,3));
		p_west_south_2.add(bVideoInsert);
		p_west_south_2.add(bVideoModify);
		p_west_south_2.add(bVideoDelete);

		p_west_south.add(p_west_south_1);
		p_west_south.add(p_west_south_2);

		p_west.add(p_west_center,BorderLayout.CENTER);
		p_west.add(p_west_south, BorderLayout.SOUTH);   // 왼쪽부분완성

		//---------------------------------------------------------------------
		// 화면구성 - 오른쪽영역
		JPanel p_east = new JPanel();
		p_east.setLayout(new BorderLayout());

		JPanel p_east_north = new JPanel();
		p_east_north.add(comVideoSearch);
		p_east_north.add(tfVideoSearch);
		p_east_north.setBorder(new TitledBorder("비디오 검색"));

		p_east.add(p_east_north,BorderLayout.NORTH);
		p_east.add(new JScrollPane(tableVideo),BorderLayout.CENTER);
		// 테이블을 붙일때에는 반드시 JScrollPane() 이렇게 해야함 


		// 전체 화면에 왼쪽 오른쪽 붙이기
		setLayout(new GridLayout(1,2));

		add(p_west);
		add(p_east);

	}

	//화면에 테이블 붙이는 메소드 
	class VideoTableModel extends AbstractTableModel { 

		ArrayList data = new ArrayList();
		String [] columnNames = {"비디오번호","제목","감독","배우"};

		public int getColumnCount() { 
			return columnNames.length; 
		} 

		public int getRowCount() { 
			return data.size(); 
		} 

		public Object getValueAt(int row, int col) { 
			ArrayList temp = (ArrayList)data.get( row );
			return temp.get( col ); 
		}

		public String getColumnName(int col){
			return columnNames[col];
		}
	}
}


