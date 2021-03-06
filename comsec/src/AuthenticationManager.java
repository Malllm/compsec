import java.sql.*;


public class AuthenticationManager {

	private Connection conn;
	
	public AuthenticationManager() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://puccini.cs.lth.se/db62", "db62", "kasper");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new journal.
	 * @param pnr
	 * @param doctorID
	 * @param nurseID
	 * @param txt
	 * @return Message about how the creation went.
	 */
	public String createJournal(long pnr, int doctorID, int nurseID, String txt) { //addPerson i Gdocs
		String msg = "Journal was created";
		int division = doctorID / 100;
		String sql = "INSERT INTO Journals VALUES(?, ?, ?, ?, ?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, pnr);
			ps.setInt(2, doctorID);
			ps.setInt(3, nurseID);
			ps.setInt(4, division);
			ps.setString(5, txt);
			ps.executeUpdate();
		} catch (SQLException e) {
			msg = "Journal was not created, it does already exist";
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return msg;
	}
	
	/**
	 * Adds text to a journal if the journal is associated with the nurse/doctor or else generates a sqlException.
	 * @param txt
	 * @param pnr
	 * @param ID
	 * @param IDType Either nurseID or doctorID.
	 * @return Message about how a the update went.
	 */
	public String updateJournal(String txt, long pnr, int ID, String IDType) {
		String msg = "Journal was updated";
		String sql = "UPDATE Journals SET journal = CONCAT(journal, ?) WHERE pnr = ? AND " + IDType + " = ?";
		PreparedStatement ps = null;
		txt = "\n"+txt;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, txt);
			ps.setLong(2, pnr);
			ps.setInt(3, ID);
			if(0 == ps.executeUpdate()) {
				msg = "Journal does not exist";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return msg;
	}
	
	/**
	 * Lets the nurse/doctor get the journal if the id or the the division is correct.
	 * @param pnr
	 * @param ID
	 * @param IDType
	 * @return The text in the journal as a String.
	 */
	public String getJournal(long pnr, int ID, String IDType) {
		int division = ID / 100;
		String sql = "SELECT journal FROM Journals WHERE pnr = ? AND (" + IDType + " = ? OR division = ?)";
		PreparedStatement ps = null;
		ResultSet rs;
		String txt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, pnr);
			ps.setInt(2, ID);
			ps.setInt(3, division);
			rs = ps.executeQuery();
			rs.next();
			txt = rs.getString("journal");
		} catch (SQLException e) {
			txt = "That person doesn't have a journal or you don't have access rights";
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return txt;
		
	}
	
	/**
	 * Returns the journal corresponding to the social security number, pnr.
	 * @param pnr
	 * @return The text in the journal as a String.
	 */
	public String getJournal(long pnr) {
		String sql = "SELECT journal FROM Journals WHERE pnr = ?";
		PreparedStatement ps = null;
		ResultSet rs;
		String txt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, pnr);
			rs = ps.executeQuery();
			rs.next();
			txt = rs.getString("journal");
		} catch (SQLException e) {
			txt = "That person doesn't have a journal";
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return txt;
		
	}
	/**
	 * Deletes the journal corresponding to the social security number, pnr.
	 * @param pnr
	 * @return Message about how the deletion went.
	 */
	public String deleteJournal(long pnr) {
		String msg = "Journal was deleted";
		String sql = "DELETE FROM Journals WHERE pnr = ?";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, pnr);
			if(0 == ps.executeUpdate()) {
				msg = "Journal does not exist";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return msg;
	}
}






