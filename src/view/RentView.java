package  view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import model.RentDao;
import model.dao.RentDaoImpl;



public class RentView extends JPanel 
{
	JTextField tfRentTel, tfRentCustName, tfRentVideoNum;
	JButton bRent;

	JTextField tfReturnVideoNum;
	JButton bReturn;

	JTable tableRecentList;

	RentTableModel rentTM;

	// 비즈니스 로직
	RentDao model;



	//==============================================
	//	 생성자 함수
	public RentView(){
		addLayout();	//화면구성
		eventProc();  	// 이벤트 실행
		connectDB();  	//DB연결
		selectList();	// 생성하자마자 테이블에 비디오목록이 뜰 수 있도록 selectList 추가
	}

	// DB 연결 
	/*
	 * 	함수명 : ConnectDB
	 *  인자 : 없음
	 *  리턴값 : DB 접속
	 *  역할 : 미리 생성되어 있는 DB에 연결할 수 있도록 해줌
	 */
	void connectDB(){
		try {
			// RentDAO(Data Access Object) RENT 테이블에 정보를 입력할 수 있도록 지정
			model = new RentDaoImpl();
		} catch (Exception e) {
			System.out.println("대여 관리 드라이버 로딩 실패"+e.getMessage());
			e.printStackTrace();
		}

	}


	/*	화면 구성   */
	void addLayout(){
		// 멤버변수 객체 생성
		tfRentTel = new JTextField(20);
		tfRentCustName = new JTextField(20);
		tfRentVideoNum = new JTextField(20);
		tfReturnVideoNum = new JTextField(10);

		bRent = new JButton("대여");
		bReturn = new JButton("반납");

		tableRecentList = new JTable();

		rentTM=new RentTableModel();
		tableRecentList = new JTable(rentTM);

		// ************* 화면구성 *****************
		// 화면의 윗쪽
		JPanel p_north = new JPanel();
		p_north.setLayout(new GridLayout(1,2));
		// 화면 윗쪽의 왼쪽
		JPanel p_north_1 = new JPanel();
		p_north_1.setBorder(new TitledBorder("대		여"));
		p_north_1.setLayout(new GridLayout(4,2));
		p_north_1.add(new JLabel("전 화 번 호"));
		p_north_1.add(tfRentTel);
		p_north_1.add(new JLabel("고 객 명"));
		p_north_1.add(tfRentCustName);
		p_north_1.add(new JLabel("비디오 번호"));
		p_north_1.add(tfRentVideoNum);
		p_north_1.add(bRent);



		// 화면 윗쪽의 오른쪽
		JPanel p_north_2 = new JPanel();	
		p_north_2.setBorder(new TitledBorder("반		납"));
		p_north_2.add(new JLabel("비디오 번호"));
		p_north_2.add(tfReturnVideoNum);
		p_north_2.add(bReturn);

		//
		setLayout(new BorderLayout());
		add(p_north, BorderLayout.NORTH);
		add(new JScrollPane(tableRecentList),BorderLayout.CENTER);


		p_north.add(p_north_1);
		p_north.add(p_north_2);
	}

	class RentTableModel extends AbstractTableModel { 

		ArrayList data = new ArrayList();
		String [] columnNames = {"비디오번호","제목","고객명","전화번호","반납예정일","반납여부"};

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

	// 이벤트 등록
	public void eventProc(){
		ButtonEventHandler btnHandler = new ButtonEventHandler();

		tfRentTel.addActionListener(btnHandler);
		bRent.addActionListener(btnHandler);
		bReturn.addActionListener(btnHandler);
		tableRecentList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent ev){

				try{
					int row = tableRecentList.getSelectedRow();
					int col = 0;	// 검색한 열을 클릭했을 때 클릭한 열의 비디오번호

					Integer vNum = (Integer)(tableRecentList.getValueAt(row, col));
					// 그 열의 비디오번호를 tfReturnVideoNum 에 띄우기
					tfReturnVideoNum.setText(vNum.toString());

				}catch(Exception ex){
					System.out.println("실패 : "+ ex.getMessage());
				}

			}
		});

	}

	// 이벤트 핸들러
	class ButtonEventHandler implements ActionListener{
		public void actionPerformed(ActionEvent ev){
			Object o = ev.getSource();

			if(o==tfRentTel){  			// 전화번호 엔터
				rentSelectTel();				
			}
			else if(o==bRent){  		// 대여 클릭
				rentClick();
				selectList();
			}
			else if(o==bReturn){  		// 반납 클릭
				returnClick();
				selectList();
			}

		}


	}

	// 반납버튼 눌렀을 때
	/*
	 *  함수명 : Return Click
	 *  인자 : 반납 버튼 클릭
	 *  역할 : 반납 버튼을 눌렀을 때 ReturnDaoImpl의 returnVideo 실행
	 */
	public void returnClick(){
		int vNum = Integer.parseInt(tfReturnVideoNum.getText());
		try {
			model.returnVideo(vNum);
			JOptionPane.showMessageDialog(null, "반납");
			clearText();
		} catch (Exception e) {
			System.out.println("반납 오류"+e.getMessage());
			e.printStackTrace();
		}


	}// end of returnClick

	// 대여 버튼 눌렀을 때 
	/*
	 *  함수명 : Rent Click
	 *  인자 : 대여 버튼 클릭
	 *  역할 : 대여 버튼을 눌렀을 때, RentDaoImpl의 rentVideo 실행
	 */
	public void rentClick(){
		String tel = tfRentTel.getText();
		int vNum = Integer.parseInt(tfRentVideoNum.getText());
		try {
			model.rentVideo(tel, vNum);
			clearText();

		} catch (Exception e) {
			System.out.println("대여 처리 오류"+e.getMessage());
			e.printStackTrace();
		}

	}//end of rentClick

	// 전화번호입력후 엔터
	/*
	 * 	함수명 : rentSelectTel
	 * 	인자 : RentTel text field의 값 입력 후 엔터
	 *  역할 : name이라는 변수에 RentDaoImpl의 selectName 결과값 할당
	 */
	public void rentSelectTel(){
		String tel = tfRentTel.getText();
		try {
			String name = model.selectName(tel);
			tfRentCustName.setText(name);
			JOptionPane.showMessageDialog(null, "전화번호");
		} catch (Exception e) {
			System.out.println("없는 정보"+e.getMessage());
		}
		
	}// end of rentSelectTel

	// 텍스트필드 정리
	/*
	 *  함수명 : Clear Text
	 *  인자 : 없음
	 *  역할 : 특정 함수가 실행되고 난 후, 해당하는 text Field의 입력값 초기화
	 */
	public void clearText() {
		tfRentTel.setText(null);
		tfRentCustName.setText(null);
		tfRentVideoNum.setText(null);

	}
	
	/*
	 * 	함수명 : selectList
	 * 	인자 : Rent Table Model의 DATA
	 *  역할 : Rent 테이블의 DATA가 있는 경우, RentDAOIMPL의 unpaidList 실행
	 */
	public void selectList () {
		try {
			rentTM.data=model.unpaidList();
			rentTM.fireTableDataChanged();
		} catch (Exception e) {
			System.out.println("미납 목록 검색 실패"+e.getMessage());
			e.printStackTrace();
		}
	}


}
