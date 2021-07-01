package com.couplecon.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.stream.Collectors;

import com.couplecon.data.Couple;
import com.couplecon.data.CoupleRequests;
import com.couplecon.data.Location;
import com.couplecon.data.Partner;
import com.couplecon.data.SearchParam;
import com.couplecon.data.SurveyChoice;
import com.couplecon.data.SurveyGroup;
import com.couplecon.data.SurveyOverview;
import com.couplecon.data.SurveyQuestion;
import com.couplecon.data.Match;
import com.couplecon.data.MatchSearchParameters;
import com.couplecon.util.Config;
import com.couplecon.util.Utils;

public class DB {
	
	protected static Connection getDBConnection (String dbname)
			throws SQLException, ClassNotFoundException {
		
		Connection conn = null;
		
		if (conn == null)  {
			
			// Get the database connect string components and build the URL
			String host = Config.getProperty("DB_HOST");
			String port = Config.getProperty("DB_PORT");
			String url = host + ":" + port;
			String user = Config.getProperty("DB_USER");
			String password = Config.getProperty("DB_PASSWORD");
			
			// Get rid of trailing slash in the url if present so that dbconnectstring won't get messed up
			if(url.charAt(url.length()-1) == '/'){
				url = url.substring(0, url.length()-1);
			}
			
			String dbconnectstring = "jdbc:mysql://"+url+"/"+dbname+"?user="+user+"&password="+password+"&useSSL=false";
			
			// Initialize driver if not already initialized
			try {
				DriverManager.getDriver(dbconnectstring);
			} catch (Throwable t) {
				Class.forName("com.mysql.jdbc.Driver");//.newInstance();
			}
			
			// Build the connection using the connect string and return it
			conn = DriverManager.getConnection(dbconnectstring);
		}
		return conn;
	}
	
	public static ArrayList<String> getReccomendedIds (String partnerId, int numCouples) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String homeId = getCoupleId(partnerId);
			String q = "select * from Connections where Couples_ID='"+ homeId +"'";
			if (numCouples > 0){
				q += " limit "+ numCouples +";";
			} else {
				q += ";";
			}
			ResultSet resultSet = statement.executeQuery(q);
			ArrayList<String> awayCoupleIds = new ArrayList<String>();
			while (resultSet.next()) {
				awayCoupleIds.add(resultSet.getString("Couples_ID_other"));
			}
			return awayCoupleIds;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static Couple getCouple (String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "select *,ST_X(location) as lat,ST_Y(location) as lon from Couples where ID='"+coupleId+"';";
			ResultSet resultSet = statement.executeQuery(q);
			Couple couple = new Couple();
			if (resultSet.next()) {
				couple.loadResultSet(resultSet);
			}
			couple.addPartner(couple.getPartnerIdHigher(), getPartner(couple.getPartnerIdHigher()));
			couple.addPartner(couple.getPartnerIdLower(), getPartner(couple.getPartnerIdLower()));
			String sql = "select pic_number, size, link from Couple_Img "
						  +"where "
						  +"ID='"+couple.getCoupleId()+"';";
			ResultSet rs = statement.executeQuery(sql);
			boolean imagesAdded = false;
			while (rs.next()) {
				imagesAdded = true;
				Integer picNum = rs.getInt("pic_number");
				String size = rs.getString("size");
				String link = rs.getString("link");
				couple.addPictureLink(picNum, size, link);
			}
			if (!imagesAdded) {
				String link = "https://s3.amazonaws.com/coupleconn-static/img/default-user-image.png";
				couple.addPictureLink(0, "small", link);
				couple.addPictureLink(0, "medium", link);
				couple.addPictureLink(0, "large", link);
				couple.addPictureLink(0, "native", link);
			}
			return couple;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getOtherPartnersId(String homePartnerId) throws Exception{
		String otherPartnersId = null;
		Connection conn = null;
		try{
			conn = getDBConnection("Couples_Connection");
			Statement stmt = conn.createStatement();
			String q = "select partner_ID_higher, partner_ID_lower from Couples where "
					+ "partner_ID_higher='"+homePartnerId+"' or "
					+ "partner_ID_lower='"+homePartnerId+"';";
			ResultSet resultSet = stmt.executeQuery(q);
			if(resultSet.next()){
				String partnerIdHigher = resultSet.getString("partner_ID_higher");
				String partnerIdLower = resultSet.getString("partner_ID_lower");
				if (partnerIdHigher.equals(homePartnerId)){
					otherPartnersId = partnerIdLower;
				} else if (partnerIdLower.equals(homePartnerId)){
					otherPartnersId = partnerIdHigher;
				}
			}
		} catch (Exception e){
			throw e;
		} finally {
			try{
				conn.close();
			} catch (Exception f){
				// Nothing needs to happen here. Only here in case
				// you try to close the conn but haven't opened it yet
			}
		}
		return otherPartnersId;
	}
	
	public static Partner getPartner (String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "select * from Partner where ID='"+partnerId+"';";
			ResultSet resultSet = statement.executeQuery(q);
			Partner partner = null;
			if (resultSet.next()) {
				partner = new Partner(resultSet);
			}
			return partner;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean partnerInCouple(String partnerId) throws Exception {
		String coupleId = getCoupleId(partnerId);
		return coupleId != null;
	}
		
	public static boolean approveProfile(String partnerId, String awayCoupleId, String approve) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String homeCoupleId = getCoupleId(partnerId);
			String activePartnerCol = null;
			int partnerIndex = Utils.partnerIndex(partnerId, homeCoupleId);
			if (partnerIndex == 0){
				activePartnerCol = "p1_approve";
			} else if (partnerIndex == 1) {
				activePartnerCol = "p2_approve";
			} else {
				return false;
			}
			// Home side match
			String h = "insert into Matches set "
					   +"home_couple_id='"+homeCoupleId+"', "
					   +"away_couple_id='"+awayCoupleId+"', "
					   + "p1_approve='"+approve+"', "
					   + "p2_approve='"+approve+"' "
					   +"on duplicate key update "
					   +"p1_approve='"+approve+"', "
					   +"p2_approve='"+approve+"';";
			int homeUpdated = statement.executeUpdate(h);
			// Away side match
			String a = "insert ignore into Matches set "
					   +"home_couple_id='"+awayCoupleId+"', "
					   +"away_couple_id='"+homeCoupleId+"';";
			int awayUpdated = statement.executeUpdate(a);
			updateCoupleApprovalCode(homeCoupleId, awayCoupleId);
			updateIsMatch(homeCoupleId, awayCoupleId);
			return homeUpdated > 0;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean updateIsMatch(String couple1, String couple2) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			boolean updateMade = false;
			String ca1 = "";
			String ca2 = "";
			Statement statement = conn.createStatement();
			String q1 = "select couple_approve from Matches where "
						+"home_couple_id='"+couple1+"' and "
						+"away_couple_id='"+couple2+"';";
			ResultSet rs1 = statement.executeQuery(q1);
			if (rs1.next()) {
				ca1 = rs1.getString("couple_approve");
			}
			String q2 = "select couple_approve from Matches where "
					  	+"home_couple_id='"+couple2+"' and "
					  	+"away_couple_id='"+couple1+"';";
			ResultSet rs2 = statement.executeQuery(q2);
			if (rs2.next()) {
				ca2 = rs2.getString("couple_approve");
			}
			if (ca1.equals("approve") && ca2.equals("approve")) {
				String matchId = Utils.getMatchId(couple1,couple2);
				String update_q1 = "update Matches set "
						   		   +"is_match=1, "
								   +"match_id='"+matchId+"' "
								   +"where "
						   		   +"home_couple_id='"+couple1+"' and "
						   		   +"away_couple_id='"+couple2+"';";
				updateMade = statement.execute(update_q1);
				String update_q2 = "update Matches set "
				   		   		   +"is_match=1, "
								   +"match_id='"+matchId+"' "
								   +"where "
				   		   		   +"home_couple_id='"+couple2+"' and "
				   		   		   +"away_couple_id='"+couple1+"';";
				updateMade = statement.execute(update_q2);
			}
			return updateMade;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void updateAllMatches() throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String selAllQuery = "select home_couple_id, away_couple_id from Matches;";
			ResultSet rs = statement.executeQuery(selAllQuery);
			while (rs.next()) {
				String hc = rs.getString("home_couple_id");
				String ac = rs.getString("away_couple_id");
				updateIsMatch(hc,ac);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean updateCoupleApprovalCode(String couple1, String couple2) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "select p1_approve, p2_approve from Matches where "
						+"home_couple_id='"+couple1+"' and "
						+"away_couple_id='"+couple2+"';";
			ResultSet rs = statement.executeQuery(q);
			if (rs.next()) {
				Statement updateStatement = conn.createStatement();
				String p1a = rs.getString("p1_approve");
				String p2a = rs.getString("p2_approve");
				String approveCode = Utils.getCoupleApproveCode(p1a, p2a);
				q = "update Matches set "
					+"couple_approve='"+approveCode+"' where "
					+"home_couple_id='"+couple1+"' and "
					+"away_couple_id='"+couple2+"';";
				return updateStatement.execute(q);
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getMatchStatus(String homeCoupleId, String awayCoupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "select match_status from Recommended_Couples where Couples_ID='"+homeCoupleId+"' and "+
					   "Couples_ID_other='"+awayCoupleId+"';";
			ResultSet matchStatusResult = statement.executeQuery(q);
			if (matchStatusResult.next()) {
				return matchStatusResult.getString("match_status");
			} else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getMatchStatusByPartner(String homePartnerId, String awayCoupleId) throws Exception {
		String homeCoupleId = getCoupleId(homePartnerId);
		return getMatchStatus(homeCoupleId, awayCoupleId);
	}
	
	public static String createPartner(Partner accountInfo) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String salt = Utils.getRandomHexString(45);
			String saltyPass = accountInfo.getPassword() + salt;
			String passwordHash = Utils.sha256(saltyPass);
			String partnerId = null;
			boolean duplicateRow = true;
			int n = 0;
			// TODO handle race conditions better here. Repeat the pid creation and insert query execution on a PrimaryKey hit.
			// Currently, a partner with the same pid could be created in between the check and the insert query.
			while (duplicateRow) {
				partnerId = Utils.getRandomHexString(8);
				String checkPIDQuery = "select * from Partner where ID='"+partnerId+"';";
				ResultSet rs = statement.executeQuery(checkPIDQuery);
				duplicateRow = rs.next();
				n ++;
				if (n > 100) { // Limit the loops
					break;
				}
			}
			accountInfo.setPartnerId(partnerId);
			String insertQuery = "insert into Partner (ID, email_address, password_hash, salt) values (?, ?, ?, ?);";
			PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
			insertStmt.setString(1, accountInfo.getPartnerId());
			insertStmt.setString(2, accountInfo.getEmailAddress());
			insertStmt.setString(3, passwordHash);
			insertStmt.setString(4, salt);
			insertStmt.execute();
			updatePartner(accountInfo);
			return accountInfo.getPartnerId();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean changePassword(Partner partner) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			String partnerId = partner.getPartnerId();
			String password = partner.getPassword();
			return changePassword(partnerId, password);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean changePassword(String partnerId, String password) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String salt = Utils.getRandomHexString(45);
			String saltyPass = password + salt;
			String passwordHash = Utils.sha256(saltyPass);
			String updateQuery = "update Partner set "
								 +"salt='"+salt+"', "
								 +"password_hash='"+passwordHash+"' "
								 +"where ID='"+partnerId+"';";
			return statement.execute(updateQuery);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getPartnerId(String emailAddress) throws Exception {
		String partnerId = null;
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String partnerIdQuery = "select ID from Partner where email_address='"+emailAddress+"';";
			ResultSet partnerIdResult = statement.executeQuery(partnerIdQuery);
			if (partnerIdResult.next()){
				partnerId = partnerIdResult.getString("ID");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		return partnerId;
	}
	
	public static String getPartnerIdByEmail(String email) throws Exception {
		String partnerId = null;
		Connection conn = getDBConnection("Couples_Connection");
		try {
			String partnerIdQuery = "select ID from Partner where email_address=?;";
			PreparedStatement stmt = conn.prepareStatement(partnerIdQuery);
			stmt.setString(1,  email);
			ResultSet partnerIdResult = stmt.executeQuery();
			if (partnerIdResult.next()){
				partnerId = partnerIdResult.getString("ID");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		return partnerId;
	}
	
	public static String getCoupleId(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String coupleIdQuery = "select ID from Couples where "
								   +"partner_ID_lower='"+partnerId+"' or "
								   +"partner_ID_higher='"+partnerId+"';";
			ResultSet coupleIdResult = statement.executeQuery(coupleIdQuery);
			String coupleId = null;
			if (coupleIdResult.next()) {
				coupleId = coupleIdResult.getString("ID");
			}
			return coupleId;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
		
	public static boolean updatePartner(Partner partner) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			
			if(partner.getPassword_hash() != null && !partner.getPassword_hash().equals("")) {
				//partner.setPassword(partner.getPassword_hash());
				DB.changePassword(partner.getPartnerId(), partner.getPassword_hash());
			}
			
			String updateQuery = "update Partner set "
								 +"first_name=?, "
								 +"middle_name=?, "
								 +"last_name=?, "
								 +"age=?, "
								 +"phone_number=?, "
								 +"address=?, "
								 +"city=?, "
								 +"state=?, "
								 +"zipcode=?, "
								 +"smoke=?, "
								 +"drink=?, "
								 +"politics=?, "
								 +"religion=?, "
								 +"gender=?, "
								 +"race=?, "
								 +"time_in_area=?, "
								 +"income_range=?, "
								 +"email_address=? "
								 +"where ID=?;";
			PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
			if (partner.getFirstName() != null) { 
				updateStmt.setString(1, partner.getFirstName());
			} else { updateStmt.setNull(1, java.sql.Types.VARCHAR); }
			
			if (partner.getMiddleName() != null) { 
				updateStmt.setString(2, partner.getMiddleName());
			} else { updateStmt.setNull(2, java.sql.Types.VARCHAR); }
			
			if (partner.getLastName() != null) { 
				updateStmt.setString(3, partner.getLastName());
			} else { updateStmt.setNull(3, java.sql.Types.VARCHAR); }
			
			if (partner.getAge() != null) { 
				updateStmt.setInt(4, partner.getAge());
			} else { updateStmt.setNull(4, java.sql.Types.INTEGER); }
			
			if (partner.getPhoneNumber() != null) { 
				updateStmt.setString(5, partner.getPhoneNumber());
			} else { updateStmt.setNull(5, java.sql.Types.VARCHAR); }
			
			if (partner.getAddress() != null) { 
				updateStmt.setString(6, partner.getAddress());
			} else { updateStmt.setNull(6, java.sql.Types.VARCHAR); }
			
			if (partner.getCity() != null) { 
				updateStmt.setString(7, partner.getCity());
			} else { updateStmt.setNull(7, java.sql.Types.VARCHAR); }
			
			if (partner.getState() != null) { 
				updateStmt.setString(8, partner.getState());
			} else { updateStmt.setNull(8, java.sql.Types.VARCHAR); }
			
			if (partner.getZipcode() != null) { 
				updateStmt.setString(9, partner.getZipcode());
			} else { updateStmt.setNull(9, java.sql.Types.VARCHAR); }
			
			if (partner.getSmoke() != null) { 
				updateStmt.setString(10, partner.getSmoke());
			} else { updateStmt.setNull(10, java.sql.Types.VARCHAR); }
			
			if (partner.getDrink() != null) { 
				updateStmt.setString(11, partner.getDrink());
			} else { updateStmt.setNull(11, java.sql.Types.VARCHAR); }
			
			if (partner.getPolitics() != null) { 
				updateStmt.setString(12, partner.getPolitics());
			} else { updateStmt.setNull(12, java.sql.Types.VARCHAR); }
			
			if (partner.getReligion() != null) { 
				updateStmt.setString(13, partner.getReligion());
			} else { updateStmt.setNull(13, java.sql.Types.VARCHAR); }
			
			if (partner.getGender() != null) { 
				updateStmt.setString(14, partner.getGender());
			} else { updateStmt.setNull(14, java.sql.Types.VARCHAR); }
			
			if (partner.getRace() != null) { 
				updateStmt.setString(15, partner.getRace());
			} else { updateStmt.setNull(15, java.sql.Types.VARCHAR); }
			
			if (partner.getTimeInArea() != null) { 
				updateStmt.setString(16, partner.getTimeInArea());
			} else { updateStmt.setNull(16, java.sql.Types.VARCHAR); }
			
			if (partner.getIncomeRange() != null) { 
				updateStmt.setString(17, partner.getIncomeRange());
			} else { updateStmt.setNull(17, java.sql.Types.VARCHAR); }
			
			if (partner.getEmailAddress() != null) { 
				updateStmt.setString(18, partner.getEmailAddress());
			} else { updateStmt.setNull(18, java.sql.Types.VARCHAR); }
			
			if (partner.getPartnerId() != null) { 
				updateStmt.setString(19, partner.getPartnerId());
			} else { updateStmt.setNull(19, java.sql.Types.VARCHAR); }
			return updateStmt.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean updateCouple(Couple couple, String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			String coupleId = getCoupleId(partnerId);
			String updateQuery = "update Couples set "
								 +"location=point(?, ?)"
								 +", relationship_type=?"
								 +", time_together=?"
								 +", story=?"
								 +", children_at_home=?"
								 +", youngest_child=?"
								 +", oldest_child=?"
								 +", num_children=?"
								 +", max_distance=?"
								 +" where ID=?;";
			PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
			
			updateStmt.setFloat(1, couple.getLocation().getLat());
			updateStmt.setFloat(2, couple.getLocation().getLon());
			
			if (couple.getRelationshipType() != null) { 
				updateStmt.setString(3, couple.getRelationshipType());
			} else { updateStmt.setNull(3, java.sql.Types.VARCHAR); }
			
			if (couple.getTimeTogether() != null) { 
				updateStmt.setString(4, couple.getTimeTogether());
			} else { updateStmt.setNull(4, java.sql.Types.VARCHAR); }
			
			if (couple.getStory() != null) { 
				updateStmt.setString(5, couple.getStory());
			} else { updateStmt.setNull(5, java.sql.Types.VARCHAR); }
			
			if (couple.getChildrenAtHome() != null) { 
				updateStmt.setString(6, couple.getChildrenAtHome());
			} else { updateStmt.setNull(6, java.sql.Types.VARCHAR); }
			
			if (couple.getYoungestChild() != null) { 
				updateStmt.setString(7, couple.getYoungestChild());
			} else { updateStmt.setNull(7, java.sql.Types.VARCHAR); }
			
			if (couple.getOldestChild() != null) { 
				updateStmt.setString(8, couple.getOldestChild());
			} else { updateStmt.setNull(8, java.sql.Types.VARCHAR); }
			
			if (couple.getNumChildren() != null) { 
				updateStmt.setInt(9, couple.getNumChildren());
			} else { updateStmt.setNull(9, java.sql.Types.INTEGER); }
			
			if (couple.getMaxDistance() != null) { 
				updateStmt.setInt(10, couple.getMaxDistance());
			} else { updateStmt.setNull(10, java.sql.Types.INTEGER); }
			
			updateStmt.setString(11, coupleId);
			
			return updateStmt.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean checkCoupleRequestExists(String homePartnerId, String awayPartnerId) throws Exception {
		return false;
	}
	
	
	public static CoupleRequests getCoupleRequestByEmail(String homePartnerId) throws Exception {
		Connection conn = null;
		CoupleRequests cr = null;
		try{
			conn = getDBConnection("Couples_Connection");
			cr = new CoupleRequests(homePartnerId);
			getCoupleRequestByEmail(conn, homePartnerId, cr);
		} catch (Exception e){
			throw e;
		} finally {
			try {conn.close();} catch(Exception f){}
		}
		return cr;
	}
	
	private static void getCoupleRequestByEmail(Connection conn, String homePartnerId, CoupleRequests cr) throws Exception {
		// Check if a couple request has been sent by email
		// Passing in the db conn so we don't have to create a new one
		// Because this is inside the try of getAllCoupleRequests
		// the finally will be hit and the DB connection will be closed
		// First get the ones that are to from the home parter
		Statement stmt = conn.createStatement();
		String query = "select email_address from Couple_Request_Tokens "
				+ "where partner_id = '"+homePartnerId+"';";
		ResultSet rs = stmt.executeQuery(query);
		if (rs.next()){
			Partner resPartner = new Partner();
			resPartner.setEmailAddress(rs.getString("email_address"));
			Partner reqPartner = DB.getPartner(homePartnerId);
			cr.addPair(reqPartner, resPartner);
		}
		// Now get the ones that are to the home partner
		Statement stmt2 = conn.createStatement();
		String getEmailAddressQuery = "select email_address from Partner where ID = '"+homePartnerId+"';";
		ResultSet rs2 = stmt2.executeQuery(getEmailAddressQuery);
		if (rs2.next()){
			Partner responsePartner = DB.getPartner(homePartnerId);
			String homeEmailAddress = rs2.getString("email_address");
			Statement stmt3 = conn.createStatement();
			String requestTokenQuery = "select partner_id from Couple_Request_Tokens where email_address = '"+homeEmailAddress+"';";
			ResultSet rs3 = stmt3.executeQuery(requestTokenQuery);
			while (rs3.next()){
				String requestPartnerId = rs3.getString("partner_id");
				Partner requestPartner = DB.getPartner(requestPartnerId);
				cr.addPair(requestPartner, responsePartner);
			}
		}
		
	}
	
	public static CoupleRequests getAllCoupleRequests(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "select * from Couple_Requests where "
					   +"requesting_partner='"+partnerId+"' or "
					   +"responding_partner='"+partnerId+"';";
			ResultSet rs = statement.executeQuery(q);
			CoupleRequests cr = new CoupleRequests(partnerId); 
			while(rs.next()) {
				String reqPid = rs.getString("requesting_partner");
				Partner reqPartner = DB.getPartner(reqPid);
				String resPid = rs.getString("responding_partner");
				Partner resPartner = DB.getPartner(resPid);
				cr.addPair(reqPartner, resPartner);
			}
			// Check if a couple request has been sent by email
			getCoupleRequestByEmail(conn, partnerId, cr);
			return cr;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean deleteCoupleRequest(String reqPartner, String resPartner) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		boolean requestDeleted = false;
		try {
			Statement statement = conn.createStatement();
			String q = "delete from Couple_Requests where "
					   +"requesting_partner='"+reqPartner+"' and "
					   +"responding_partner='"+resPartner+"';";
			int numRowsCRDeleted = statement.executeUpdate(q);
			if (numRowsCRDeleted > 0){
				requestDeleted = true;
			}
			int numRowsCRTdeleted = deleteCoupleRequestTokenByRequestingPartnerId(conn, reqPartner);
			if (numRowsCRTdeleted > 0){
				requestDeleted = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		return requestDeleted;
	}
	
	public static boolean coupleRequestExists (String reqPartner, String resPartner) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		boolean requestExists = false;
		try {
			// Check for request in Couple_Requests
			Statement statement = conn.createStatement();
			String q = "select * from Couple_Requests where "
					   +"requesting_partner='"+reqPartner+"' and "
					   +"responding_partner='"+resPartner+"';";
			ResultSet rs = statement.executeQuery(q);
			if (rs.next()){
				requestExists = true;
			}
			// Check for request in Couple_Request_Tokens
			Statement stmt2 = conn.createStatement();
			String q2 = "select * from Couple_Request_Tokens where "
					+ "partner_id='"+reqPartner+"';";
			ResultSet rs2 = stmt2.executeQuery(q2);
			if (rs2.next()){
				requestExists = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		return requestExists;
	}
	
	public static boolean createCoupleRequest (String reqPartnerId, String resPartnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String dblReqQuery = "select * from Couple_Requests where "
								 +"requesting_partner='"+reqPartnerId+"';";
			ResultSet dblReqResponse = statement.executeQuery(dblReqQuery);
			while (dblReqResponse.next()) {
				String existingResId = dblReqResponse.getString("responding_partner");
				deleteCoupleRequest(reqPartnerId, existingResId);
			}
			String createReqQuery = "insert into Couple_Requests "
									+"(requesting_partner, responding_partner) values ("
									+"'"+reqPartnerId+"', "
									+"'"+resPartnerId+"'"
									+");";
			return statement.execute(createReqQuery);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static Boolean createCouple(String partner1Id, String partner2Id) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String coupleId = Utils.getCoupleId(partner1Id, partner2Id);
			ArrayList<String> ordered = Utils.orderPartnerIds(partner1Id, partner2Id);
			String createCoupleQuery = "insert into Couples (ID, partner_ID_lower, partner_ID_higher, location) "
									   +"values ("
									   +"'"+coupleId+"', "
									   +"'"+ordered.get(0)+"', "
									   +"'"+ordered.get(1)+"', "
									   +"POINT(80.0381807, 14.0921768)"
									   + ");";
			int action = statement.executeUpdate(createCoupleQuery);
			if(action > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean verifyUser(String emailAddress, String password) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String hashSaltQuery = "select password_hash, salt from Partner where email_address='"+emailAddress+"';";
			ResultSet hashSaltResult = statement.executeQuery(hashSaltQuery);
			String correctHash = "";
			String salt = "";
			if (hashSaltResult.next()) {
				correctHash = hashSaltResult.getString("password_hash");
				salt = hashSaltResult.getString("salt");
			} else {
				return false;
			}
			String saltyPass = password + salt;
			String submittedHash = Utils.sha256(saltyPass);
			return correctHash.equals(submittedHash);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String verifyUserByToken(String token) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String tokenQuery = "select partner_id from Login_Tokens where "
					+"token='"+token+"';";
			ResultSet tokenResult = statement.executeQuery(tokenQuery);

			if (tokenResult.next()) {
				String partnerId = tokenResult.getString("partner_id");
				return partnerId;
			} else {
				return null;
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static int deleteLoginToken(String token) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String delQuery = "delete from Login_Tokens where "
					  +"token='"+token+"';";
			return statement.executeUpdate(delQuery);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String generateLoginToken(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String token = Utils.randomString(32);
			String insQuery = "insert into Login_Tokens (partner_id, token) values ("
							  +"'"+partnerId+"', "
							  +"'"+token+"')";
			int numRowsAffected = statement.executeUpdate(insQuery);	
			if(numRowsAffected > 0) {
				return token;
			} else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String createCoupleRequestToken(String partnerId, String toBeRegisteredEmail) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String token = Utils.randomString(32);
			String insQuery = "insert into Couple_Request_Tokens (partner_id, token, email_address) values ("
							  +"'"+partnerId+"', "
							  +"'"+token+"', "
							  +"'"+toBeRegisteredEmail+"')";
			int numRowsAffected = statement.executeUpdate(insQuery);	
			if(numRowsAffected > 0) {
				return token;
			} else {
				return "";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getCoupleVisibility(String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String visibilityQuery = "select visible from Couples where ID='"+coupleId+"';";
			ResultSet visibilityResult = statement.executeQuery(visibilityQuery);
			visibilityResult.next();
			return visibilityResult.getString("visible");
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean setCoupleVisibility(String coupleId, boolean visibility) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			String visCode;
			if (visibility) {
				visCode = "y";
			} else {
				visCode = "n";
			}
			Statement statement = conn.createStatement();
			String visibilityQuery = "update Couples set visible='"+visCode+"' "
									 +"where ID='"+coupleId+"';";
			Boolean success = statement.execute(visibilityQuery);
			return success;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static ArrayList<SurveyQuestion> getCategoricalQuestions(String level) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			ArrayList<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
			Statement questionsStatement = conn.createStatement();
			String levelCode = level.substring(0,1);
			String questionsQuery = "select qID, column_name, text from CP_Categorical_Questions where level='"+levelCode+"';";
			ResultSet questionsResult = questionsStatement.executeQuery(questionsQuery);
			while (questionsResult.next()) {
				SurveyQuestion question = new SurveyQuestion();
				int qID = questionsResult.getInt("qID");
				String columnName = questionsResult.getString("column_name");
				question.id = Utils.undercaseToCamelCase(columnName);
				question.text = questionsResult.getString("text");
				Statement choicesStatement = conn.createStatement();
				String choicesQuery = "select choice_no, text from CP_Categorical_Choices where qID="+qID+";";
				ResultSet choicesResult = choicesStatement.executeQuery(choicesQuery);
				while (choicesResult.next()) {
					SurveyChoice choice = new SurveyChoice();
					choice.value = choicesResult.getInt("choice_no");
					choice.text = choicesResult.getString("text");
					question.choices.add(choice);
				}
				questions.add(question);
			}
			return questions;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static HashMap<String,ArrayList<SurveyQuestion>> getSurveyGroupQuestions(String[] surveyGroups, String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			HashMap<String,ArrayList<SurveyQuestion>> allQuestions = new HashMap<String,ArrayList<SurveyQuestion>>();
			Statement questionsStatement = conn.createStatement();
			Statement choicesStatement = conn.createStatement();
			Statement answerStatement = conn.createStatement();
			for (String groupId : surveyGroups) {
				ArrayList<SurveyQuestion> groupQuestions = new ArrayList<SurveyQuestion>();
				ArrayList<String> questionIds = new ArrayList<String>();
				String questionsQuery = "Select question_ID, question from Survey_Questions where "
									    +"question_group='"+groupId+"' and "
									    +"active='y';";
				ResultSet questionsResult = questionsStatement.executeQuery(questionsQuery);
				while (questionsResult.next()) {
					SurveyQuestion question = new SurveyQuestion();
					question.text = questionsResult.getString("question");
					question.id = String.valueOf(questionsResult.getInt("question_ID"));
					questionIds.add(question.id);
					String choicesQuery = "select score, text, question_ID from Survey_Choices where "
										  +"question_ID='"+question.id+"';";
					ResultSet choicesResult = choicesStatement.executeQuery(choicesQuery);
					while (choicesResult.next()) {
						SurveyChoice choice = new SurveyChoice();
						choice.text = choicesResult.getString("text");
						choice.value = choicesResult.getInt("score");
						question.choices.add(choice);
					}
					groupQuestions.add(question);
				}
				String answerQuery = "select score, weight, question_ID from Survey_Answers where "
									 +"partner_ID='"+partnerId+"' and "
									 +"question_ID in ("+String.join(",", questionIds)+");";
				ResultSet answerResult = answerStatement.executeQuery(answerQuery);
				while (answerResult.next()) {
					String qid = String.valueOf(answerResult.getInt("question_ID"));
					for (SurveyQuestion question: groupQuestions) {
						if (question.id.equals(qid)) {
							question.currentValue = answerResult.getInt("score");
							question.currentImportance = answerResult.getInt("weight");
						}
					}
				}
				allQuestions.put(groupId, groupQuestions);
			}
			return allQuestions;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static ArrayList<String> getSurveyGroups() throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement groupsStatement = conn.createStatement();
			String groupsQuery = "select distinct question_group from Survey_Questions where active='y' order by question_group;";
			ResultSet groupsResult = groupsStatement.executeQuery(groupsQuery);
			ArrayList<String> groups = new ArrayList<String>();
			while (groupsResult.next()) {
				groups.add(groupsResult.getString("question_group"));
			}
			return groups;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static SurveyOverview getSurveyOverview(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			SurveyOverview overview = new SurveyOverview();
			ArrayList<String> groups = getSurveyGroups();
			double minRatioAnswered = 1.0;
			String reccGroup = "";
			for (String groupId : groups) {
				SurveyGroup group = new SurveyGroup();
				group.id = groupId;
				group.title = groupId;
				group.description = "Placeholder description";
				Statement countsStatement = conn.createStatement();
				String questionsQuery = "Select question_ID from Survey_Questions "
									 +"where question_group='"+groupId+"' and "
									 +"active='y';";
				ResultSet questionsResult = countsStatement.executeQuery(questionsQuery);
				int numQuestions = 0;
				ArrayList<String> questionIds = new ArrayList<String>();
				while (questionsResult.next()) {
					numQuestions += 1;
					questionIds.add(String.valueOf(questionsResult.getInt("question_ID")));
				}
				if (numQuestions > 0) {
					group.numQuestions = numQuestions;
				} else {
					continue;
				}
				String idsCsv = String.join(",", questionIds);
				String answersQuery = "select count(*) from Survey_Answers where "
									  +" Partner_ID='"+partnerId+"' and "
									  +"question_ID in ("+idsCsv+");";
				ResultSet answersResult = countsStatement.executeQuery(answersQuery);
				int numAnswered = 0;
				if (answersResult.next()) {
					numAnswered = answersResult.getInt(1);
				}
				group.numAnswered = numAnswered;
				overview.groups.add(group);
				double ratioAnswered = (double)group.numAnswered/group.numQuestions;
				if (ratioAnswered < minRatioAnswered) {
					minRatioAnswered = ratioAnswered;
					reccGroup = group.id;
				}
			}
			overview.nextGroup = reccGroup;
			return overview;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean answerSurveyQuestion(String partnerId, SurveyChoice choice) throws Exception {
		return answerSurveyQuestion(partnerId, choice.questionID, choice.value, choice.importance);
	}
	
	public static boolean answerSurveyQuestion(String partnerId, String questionId, int value, int importance) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String answerQuery = "insert into Survey_Answers "
					 +"(Partner_ID, weight, score, question_ID) values ("
					 +"'"+partnerId+"', "
					 +importance+", "
					 +value+", "
					 +questionId+") on duplicate key update score="+value+", weight="+importance+";";
			int numRowsChanged = statement.executeUpdate(answerQuery);
			if (numRowsChanged > 0){
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static Match getMatch(String homeCoupleId, String awayCoupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String matchQuery = "select * from Matches where "
							   	+"home_couple_id='"+homeCoupleId+"' "
							   	+"and away_couple_id='"+awayCoupleId+"';"
							   	;
			ResultSet matchResult = statement.executeQuery(matchQuery);
			Match match = null;
			if (matchResult.next()) {
				match = matchRowToMatch(matchResult);
				String matchId = Utils.getMatchId(homeCoupleId, awayCoupleId);
				String msgQuery = "select content from Messages where room='"+matchId+"' order by msg_id desc limit 1;";
				ResultSet msgResult = statement.executeQuery(msgQuery);
				if (msgResult.next()) {
					match.messages.add(msgResult.getString("content"));
				}
			}
			return match;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	private static Match matchRowToMatch(ResultSet rs) throws SQLException {
		String homeCoupleId = rs.getString("home_couple_id");
		String awayCoupleId = rs.getString("away_couple_id");
		Match match = new Match(homeCoupleId, awayCoupleId);
		String[] partners = Utils.getPartners(match.homeCoupleId);
		for (int i = 0; i < partners.length; i++) {
			String partnerId = partners[i];
			String colName;
			if (i == 0) {
				colName = "p1_approve";
			} else {
				colName = "p2_approve";
			}
			String approve = rs.getString(colName);
			match.setPartnerApprove(partnerId, approve);
		}
		match.coupleApprove = rs.getString("couple_approve");
		match.isMatch = rs.getBoolean("is_match");
		match.priority = rs.getFloat("priority");
		match.type = rs.getString("type");
		match.matchId = rs.getString("match_id");
		return match;
	}
	
	public static ArrayList<Match> getAllMatches(String homeCoupleId, String partnerId, MatchSearchParameters matchParams) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String mainPartnerCol;
			String otherPartnerCol;
			if (Utils.partnerIndex(partnerId, homeCoupleId) == 0) {
				mainPartnerCol = "p1_approve";
				otherPartnerCol = "p2_approve";
			} else {
				mainPartnerCol = "p2_approve";
				otherPartnerCol = "p1_approve";
			}
			String matchQuery = "select * from Matches where "
							   	+"home_couple_id='"+homeCoupleId+"' ";
			if (matchParams.filterApprove()) {
				ArrayList<String> stringApproves = matchParams.getApprove();
				matchQuery += "and "+ mainPartnerCol+" in ('"+String.join("','", stringApproves)+"') ";
			}
			if (matchParams.filterPartnerApprove()) {
				ArrayList<String> stringApproves = matchParams.getPartnerApprove();
				matchQuery += "and "+ otherPartnerCol+" in ('"+String.join("','", stringApproves)+"') ";
			}
			if (matchParams.filterIsMatch()) {
				matchQuery += "and is_match="+String.valueOf(matchParams.getIsMatch())+" ";
			}
			if (matchParams.getOrderPriority()) {
				matchQuery += "order by priority ";
			}
			if (matchParams.useLimit()) {
				matchQuery += "limit "+matchParams.getLimit()+" ";
			}
			matchQuery += ";";
			ResultSet matchResult = statement.executeQuery(matchQuery);
			ArrayList<Match> allMatches = new ArrayList<Match>();
			while (matchResult.next()) {
				Match match = matchRowToMatch(matchResult);
				allMatches.add(match);
			}
			return allMatches;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static ArrayList<Couple> searchCouples(Collection<SearchParam> parameters) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			ArrayList<Couple> out = new ArrayList<Couple>();
			String q = "select *,ST_X(location) as lat,ST_Y(location) as lon from Couples as c "
					   +"join Partner as p1 on c.Partner_ID_lower=p1.ID "
					   +"join Partner as p2 on c.Partner_ID_higher=p2.ID ";
			int n = 0;
			for (SearchParam param : parameters) {
				if (n == 0) {
					q += "where (";
				} else {
					q += "and (";
				}
				switch (param.getType()) {
				case "range":
					switch (param.getTable()) {
					case "couple":
						q += String.valueOf(param.getMin())+"<=c."+param.getColumn()+" and "
					         +"c."+param.getColumn()+"<="+String.valueOf(param.getMax())+" ";
						break;
					case "partner":
						q += "("+String.valueOf(param.getMin())+"<=p1."+param.getColumn()+" and "
					         +"p1."+param.getColumn()+"<="+String.valueOf(param.getMax())+") ";
						if (param.isBothPartners()) {
							q += "and ";
						} else {
							q += "or ";
						}
						q += "("+String.valueOf(param.getMin())+"<=p2."+param.getColumn()+" and "
						         +"p2."+param.getColumn()+"<="+String.valueOf(param.getMax())+") ";
						break;
					}
					break;
				case "multiselect":
					switch (param.getTable()) {
					case "couple":
						q += "c."+param.getColumn()+" in ('"+String.join("','", param.getValues())+"') ";
						break;
					case "partner":
						q += "(p1."+param.getColumn()+" in ('"+String.join("','", param.getValues())+"')) ";
						if (param.isBothPartners()) {
							q += "and ";
						} else {
							q += "or ";
						}
						q += "(p2."+param.getColumn()+" in ('"+String.join("','", param.getValues())+"')) ";
						break;
					}
					break;
				case "singleselect":
					switch (param.getTable()) {
					case "couple":
						if (param.getColumn().equals("location")) {
							Location center = param.getCenter();
							String lat = String.valueOf(center.getLat());
							String lon = String.valueOf(center.getLon());
							String dist = String.valueOf(Float.valueOf(param.getValues().get(0))*1.61);
							q += "mbrcontains(linestring(point("+lat+"+"+dist+"/111.1, "+lon+"+"+dist+"/(111.1/cos(radians("+lat+")))),point("+lat+"-"+dist+"/111.1,"+lon+"-"+dist+"/(111.1/cos(radians("+lat+"))))),location) ";
						} else {
							q += "c."+param.getColumn()+"='"+param.getValues().get(0)+"' ";
						}
						break;
					case "partner":
						q += "p1."+param.getColumn()+"='"+param.getValues().get(0)+"' ";
						if (param.isBothPartners()) {
							q += "and ";
						} else {
							q += "or ";
						}
						q += "p2."+param.getColumn()+"='"+param.getValues().get(0)+"' ";
						break;
					}
					break;
				default: 
					break;
				}
				q += ") ";
				n++;
			}
			q += ';';
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(q);
			while (rs.next()) {
				Couple couple = new Couple(rs);
				Partner partner1 = DB.getPartner(couple.getPartnerIdLower());
				couple.addPartner(couple.getPartnerIdLower(), partner1);
				Partner partner2 = DB.getPartner(couple.getPartnerIdHigher());
				couple.addPartner(couple.getPartnerIdHigher(), partner2);
				out.add(couple);
			}
			return out;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void setCoupleImage(String coupleId, int picNum, String size, String bucket, String key, String link) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement stmt = conn.createStatement();
			String rowExistsSql = "select link from Couple_Img "
								  +"where "
								  +"ID='"+coupleId+"' "
								  +"and pic_number="+String.valueOf(picNum)+" "
								  +"and size='"+size+"'"
								  +";";
			ResultSet existsRS = stmt.executeQuery(rowExistsSql);
			if (existsRS.next()) {
				String updateSql = "update Couple_Img set "
								   +"s3_bucket='"+bucket+"', "
								   +"s3_key='"+key+"', "
								   +"link='"+link+"' "
								   +"where "
								   +"ID='"+coupleId+"' "
								   +"and pic_number="+String.valueOf(picNum)+" "
								   +"and size='"+size+"'"
								   +";";
				stmt.execute(updateSql);
			} else {
				String insertSql = "insert into Couple_Img "
								   +"(ID,pic_number,size,s3_bucket,s3_key,link) values ("
								   +"'"+coupleId+"', "
								   +String.valueOf(picNum)+", "
								   +"'"+size+"', "
								   +"'"+bucket+"', "
								   +"'"+key+"', "
								   +"'"+link+"'); ";
				stmt.execute(insertSql);
			}
			correctImageOrder(coupleId);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean blockCouple(String awayCoupleId, String homePartnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String homeCoupleId = getCoupleId(homePartnerId);
			String blockQuery = "insert into Blocked_Contacts (ID, block_ID, blocking_partner_ID) "
								+"values ( "
								+"'"+homeCoupleId+"', "
								+"'"+awayCoupleId+"', "
								+"'"+homePartnerId+"');";
			return statement.execute(blockQuery);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean deleteCoupleImages(String coupleId, Collection<Integer> imageNumbers) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String numbersString = imageNumbers.stream().map(Object::toString).collect(Collectors.joining(", "));
			String delQuery = "delete from Couple_Img where "
							  +"ID='"+coupleId+"' and "
							  +"pic_number in ("+numbersString+");";
			statement.execute(delQuery);
			correctImageOrder(coupleId);
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean correctImageOrder(String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement queryStatement = conn.createStatement();
			String curNumsQuery = "select distinct pic_number from Couple_Img where ID='"+coupleId+"' order by pic_number asc;";
			ResultSet curNumsRs = queryStatement.executeQuery(curNumsQuery);
			int i=0;
			Statement updateStmt = conn.createStatement();
			while (curNumsRs.next()) {
				int curPicNum = curNumsRs.getInt("pic_number");
				String resetSeqQuery = "update Couple_Img set pic_number="+String.valueOf(i)+" where "
									   +"ID='"+coupleId+"' and "
									   +"pic_number="+curPicNum+";";
				updateStmt.execute(resetSeqQuery);
				i++;
			}
			return true;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static BlockedIds getBlocks(String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement queryStatement = conn.createStatement();
			String blockQuery = "select ID, block_ID from Blocked_Contacts where "
								+"ID='"+coupleId+"' or "
								+"block_ID='"+coupleId+"';";
			ResultSet blockResults = queryStatement.executeQuery(blockQuery);
			BlockedIds blocks = new BlockedIds();
			blocks.ignoreCoupleId(coupleId);
			while (blockResults.next()) {
				blocks.addCoupleId(blockResults.getString("ID"));
				blocks.addCoupleId(blockResults.getString("block_ID"));
			}
			return blocks;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String createEmailVerificationToken(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String tokenType = "verify";
			String delQuery = "delete from Email_Tokens where "
							  +"partner='"+partnerId+"' and "
							  +"type='"+tokenType+"';";
			statement.execute(delQuery);
			String token = Utils.randomString(32);
			String insQuery = "insert into Email_Tokens (partner, type, token) values ("
							  +"'"+partnerId+"', "
							  +"'"+tokenType+"', "
							  +"'"+token+"');";
			statement.execute(insQuery);
			return token;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean resetPassword(String token, String password) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String getPQuery = "select partner from Email_Tokens where "
					+"token='"+token+"';";
			String removeQuery = "delete from Email_Tokens where "
					+"token='"+token+"';";
			ResultSet partnerResult = statement.executeQuery(getPQuery);
			if (partnerResult.next()) {
				String partnerId = partnerResult.getString("partner");
				boolean deleteResult = statement.execute(removeQuery);
				changePassword(partnerId, password);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String createPasswordResetToken(String partnerId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String tokenType = "resetpw";
			String delQuery = "delete from Email_Tokens where "
							  +"partner='"+partnerId+"' and "
							  +"type='"+tokenType+"';";
			statement.execute(delQuery);
			String token = Utils.randomString(32);
			String insQuery = "insert into Email_Tokens (partner, type, token) values ("
							  +"'"+partnerId+"', "
							  +"'"+tokenType+"', "
							  +"'"+token+"');";
			statement.execute(insQuery);
			return token;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
		
	public static String verifyEmail(String token) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String partnerId = null;
			String getPQuery = "select partner from Email_Tokens where "
			+"token='"+token+"';";
			ResultSet partnerResult = statement.executeQuery(getPQuery);
			if (partnerResult.next()) {
				partnerId = partnerResult.getString("partner");
				String setVerifiedQuery = "update Partner set "
				+"email_verified=1 where "
						+"ID='"+partnerId+"';";
				statement.execute(setVerifiedQuery);
			}
			if (partnerId != null){
				// Delete all previous login tokens for the user
				statement.executeUpdate("delete from Login_Tokens where partner_id='"+partnerId+"';");
			}
			return partnerId;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static Boolean getCoupleActive(String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String getActiveQuery = "select active from Couples where "
								+"ID='"+coupleId+"';";
			ResultSet rs = statement.executeQuery(getActiveQuery);
			Boolean active = null;
			if (rs.next()) {
				String activeStr = rs.getString("active");
				active = activeStr.equals("y");
			}
			return active;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void setCoupleActive(String coupleId, Boolean active) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String activeStr = active ? "y" : "n";
			String setActive = "update Couples set "
							   +"active='"+activeStr+"' where "
							   +"ID='"+coupleId+"';";
			statement.execute(setActive);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void setPartnerEmailIsVerified(String partnerId, Integer active) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String setActive = "update Partner set "
							   +"email_verified='"+active+"' where "
							   +"ID='"+partnerId+"';";
			statement.execute(setActive);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static String getPartnerIdByCoupleRequestToken(String coupleRequestToken) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String getActiveQuery = "select partner_id from Couple_Request_Tokens where "
								+"token='"+coupleRequestToken+"';";
			ResultSet rs = statement.executeQuery(getActiveQuery);
			String partnerId = null;
			if (rs.next()) {
				partnerId = rs.getString("partner_id");

			}
			return partnerId;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static HashMap<String, String> getCoupleRequestTokenData(String coupleRequestToken) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String getActiveQuery = "select partner_id, email_address from Couple_Request_Tokens where "
								+"token='"+coupleRequestToken+"';";
			ResultSet rs = statement.executeQuery(getActiveQuery);
			String partnerId = null;
			String email_address = null;
			HashMap<String, String> coupleRequestTokenData = new HashMap<String, String>();
			coupleRequestTokenData.put("partnerId", null);
			coupleRequestTokenData.put("email_address", null);
			if (rs.next()) {
				coupleRequestTokenData.put("partnerId", rs.getString("partner_id"));
				coupleRequestTokenData.put("email_address", rs.getString("email_address"));

			}
			return coupleRequestTokenData;
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static int deleteCoupleRequestTokenByRequestingPartnerId(String partnerId) throws Exception {
		Connection conn = null;
		int numDeleted = 0;
		try {
			conn = getDBConnection("Couples_Connection");
			numDeleted = deleteCoupleRequestTokenByRequestingPartnerId(conn, partnerId);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		return numDeleted;
	}
	
	public static int deleteCoupleRequestTokenByRequestingPartnerId(Connection conn, String partnerId) throws Exception {
		Statement statement = conn.createStatement();
		String q = "delete from Couple_Request_Tokens where "
				   +"partner_id='"+partnerId+"';";
		int numDeleted = statement.executeUpdate(q);
		return numDeleted;
	}
	
	public static boolean deleteCoupleRequestToken(String coupleRequestToken) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			String q = "delete from Couple_Request_Tokens where "
					   +"token='"+coupleRequestToken+"';";
			return statement.execute(q);
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}

	public static void updateLastActive(String coupleId) throws Exception {
		Connection conn = getDBConnection("Couples_Connection");
		try {
			String q = "update Couples set last_active=CURRENT_TIMESTAMP where ID=?";
			PreparedStatement stmt = conn.prepareStatement(q);
			stmt.setString(1, coupleId);
			stmt.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static boolean createExtraLogin(String partnerId, String emailAddress, String firstName, String lastName, String password) throws Exception {
	
		Connection conn = getDBConnection("Couples_Connection");
		try {
			Statement statement = conn.createStatement();
			
			String salt = Utils.getRandomHexString(45);
			String saltyPass = password + salt;
			String passwordHash = Utils.sha256(saltyPass);
			
			String query = "update Partner set email_address='"+emailAddress+"', first_name='"+firstName+"', last_name='"+lastName+"', password_hash='"+passwordHash+"', salt='"+salt+"' where ID='"+partnerId+"';";
			int rs = statement.executeUpdate(query);
			
			Partner partner = DB.getPartner(partnerId);
			
			System.out.println("DB Partner: "+partner.getEmailAddress()+ " - "+emailAddress);
			if(partner.getEmailAddress().equals(emailAddress)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			conn.close();
		}
		
	}
	
}