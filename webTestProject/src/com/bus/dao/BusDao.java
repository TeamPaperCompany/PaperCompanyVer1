package com.bus.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;*/


import com.booking.vo.BookingVO;
import com.util.DBConnectionMgr;

public class BusDao {
	 
	DBConnectionMgr dbMgr = new DBConnectionMgr();
	//통합예매코드--리턴값==시퀀스
   	public int booking(String m_email){
		StringBuffer dml = new StringBuffer();
		dml.append("INSERT INTO BOOKING(BOOKING_CODE, BOOKING_DAY, M_EMAIL) ");
		dml.append("VALUES(seq_booking_number.nextval, to_char(sysdate,'yyyy-mm-dd HH:MM'), ?) ");
		Connection	con = null;
		PreparedStatement pstmt = null;
		
		
		try{
			con	=dbMgr.getConnection();
			
			pstmt = con.prepareStatement(dml.toString());
			pstmt.setString(1,m_email);
			pstmt.executeUpdate();
	
		}catch(SQLException se){
			System.out.println("booking = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("booking = [ "+e+" ]");
		}
	
		int seq = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT LAST_NUMBER FROM USER_SEQUENCES WHERE SEQUENCE_NAME='SEQ_BOOKING_NUMBER' ");
	    ResultSet rs = null;
		try{
			pstmt = con.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				seq = rs.getInt("LAST_NUMBER");
			}
					
		}catch(SQLException se){
			System.out.println("booking2 = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("booking2 = [ "+e+" ]");
		}
		return seq-1;
	}
   	
   	
   	//예매 체크
	public BookingVO bookingChack(int d_code, int b_code){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT arrival_time, booking_price, booking_dcode, booking_age, booking_code, dp_date, ");
	    sql.append("(SELECT TIME_TIME FROM TIMETABLE WHERE TIMETABLE.TIME_CODE = BOOKING_DETAILS.time_code)as TIME_CODE, ");
	    sql.append("(SELECT SEAT_NUMBER FROM SEAT WHERE SEAT.SEAT_CODE = BOOKING_DETAILS.seat_code)as SEAT_CODE, ");
	    sql.append("(SELECT SEAT_SEAT FROM SEAT WHERE SEAT.SEAT_CODE = BOOKING_DETAILS.seat_code)as SEAT_TYPE, ");
	    sql.append("(SELECT city_city AS st_city FROM city WHERE city.city_code = BOOKING_DETAILS.start_city)as START_CITY, ");
	    sql.append("(SELECT city_city AS ed_city FROM city WHERE city.city_code = BOOKING_DETAILS.arrival_city)as arrival_city, ");
	    sql.append("(SELECT vehicle_kinds FROM vehicle WHERE vehicle.vehicle_code = BOOKING_DETAILS.vehicle_code)as VEHICLE_CODE ");
	    sql.append("  FROM BOOKING_DETAILS ");
		sql.append(" WHERE booking_dcode = ? ");
		sql.append("   AND Booking_code = ? ");
	    ResultSet rs = null;
	    BookingVO bvo = new BookingVO(); 
	    Connection con = null;
		PreparedStatement pstmt = null;
		try{
			con	=dbMgr.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setInt(1,d_code);
			pstmt.setInt(2,b_code);
			rs = pstmt.executeQuery();
		  while(rs.next()){
			  bvo.setArrival_time(rs.getString("ARRIVAL_TIME"));        
			  bvo.setBooking_price(rs.getString("BOOKING_PRICE"));
			  bvo.setBooking_dcode(rs.getInt("BOOKING_DCODE"));        
			  bvo.setBooking_age(rs.getString("BOOKING_AGE"));       
			  bvo.setBooking_code(rs.getInt("BOOKING_CODE"));       
			  bvo.setSeat_code(rs.getString("SEAT_CODE"));    
			  bvo.setSeat_type(rs.getString("SEAT_TYPE"));    
			  bvo.setTime_code(rs.getString("TIME_CODE"));      
			  bvo.setStart_city(rs.getString("START_CITY"));      
			  bvo.setVehicle_code(rs.getString("VEHICLE_CODE"));      
			  bvo.setDp_date(rs.getString("DP_DATE"));  
			  bvo.setArrival_city(rs.getString("ARRIVAL_CITY"));
		  }
		
		}catch(SQLException se){
			System.out.println("bookingchk32 = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("bookingchk3 = [ "+e+" ]");
		}
	    
	    return bvo;
	}
   
   	//버스예매
   	public boolean bookingBus(String b_brice, int d_code, String age, int b_code, String s_code, String t_code,
						String st_city, String en_city, String a_time, String dp_date){
		StringBuffer dml = new StringBuffer();
		dml.append("INSERT INTO BOOKING_DETAILS (VEHICLE_CODE, BOOKING_PRICE, BOOKING_DCODE, BOOKING_AGE, ");
		dml.append("BOOKING_CODE, SEAT_CODE, TIME_CODE, START_CITY, ARRIVAL_CITY, ARRIVAL_TIME, DP_DATE ");
		dml.append("VALUES('bg01', '?', ?, '?', ?, '?', '?', '?', '?', '?','?') ");
		//'버스 = bg01', '가격', DCODE, 성인아동, BOOKINGCODE , '좌석', 'TIME_CODE', '출발지', '도착지', '예상시간','출발날짜'
		
		boolean success = false;
		Connection	con = null;
		PreparedStatement pstmt = null;
		
		try{
			con	=dbMgr.getConnection();
			pstmt = con.prepareStatement(dml.toString());
			pstmt.setString(1,b_brice);//가격
			pstmt.setInt(2,d_code);//예약상세코드
			pstmt.setString(3,age);//성인-아동
			pstmt.setInt(4,b_code);//예약코드
			pstmt.setString(5,s_code);//좌석코드
			pstmt.setString(6,t_code);//시간코드
			pstmt.setString(7,st_city);//출발지
			pstmt.setString(8,en_city);//도착지
			pstmt.setString(9,a_time);//예상시간
			pstmt.setString(10,dp_date);//출발날짜
			
			int i = pstmt.executeUpdate();
			if(i==1)success = true;
			
		}catch(SQLException se){
			System.out.println("bookingBus = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("bookingBus = [ "+e+" ]");
		
		}
		return success;
	}
	/*//운송수단코드로 운송수단종류를 리턴
	public String checkVehicle(String b_brice){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT VEHICLE_KINDS FROM VEHICLE ");
		sql.append("WHERE VEHICLE_CODE = ? ");
	    String vehicle = "";
	    Connection	con = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try{
	    	con	= dbMgr.getConnection();
	    	pstmt = con.prepareStatement(sql.toString());
	    	pstmt.setString(1,b_brice);
			rs = pstmt.executeQuery();
			  if(rs.next()){
				  vehicle = rs.getString("VEHICLE_KINDS");
			  }
			
		}catch(SQLException se){
			System.out.println("bookingBus = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("bookingBus = [ "+e+" ]");
	   
    	}
	    return vehicle;
	}
	//시트코드로 좌석과 좌석종류를 리턴
	public  List<HashMap> checkSeat(String s_code){
		
		List<HashMap> boardList = new ArrayList<HashMap>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEAT_NUMBER FROM SEAT ");
		sql.append("WHERE SEAT_CODE = ? ");
	    String vehicle = "";
	    Connection	con = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try{
	    	con	= dbMgr.getConnection();
	    	pstmt = con.prepareStatement(sql.toString());
	    	pstmt.setString(1,s_code);
			rs = pstmt.executeQuery();
			  if(rs.next()){
				  vehicle = rs.getString("SEAT_NUMBER");
				  vehicle = rs.getString("SEAT_SEAT");
			  }
			
		}catch(SQLException se){
			System.out.println("bookingBus = [ "+se+" ]");
		}catch (Exception e){
			System.out.println("bookingBus = [ "+e+" ]");
	   
    	}
	    return vehicle;
	}*/
   	
}