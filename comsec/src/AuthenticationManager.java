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
	 */
	public void createJournal(int pnr, int doctorID, int nurseID, String txt) { //addPerson i Gdocs
		int division = doctorID / 100;
		String sql = "INSERT INTO Journals VALUES(?, ?, ?, ?, ?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			ps.setInt(2, doctorID);
			ps.setInt(3, nurseID);
			ps.setInt(4, division);
			ps.setString(5, txt);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Adds text to a journal if the journal is associated with the nurse/doctor or else generates a sqlException.
	 * @param txt
	 * @param pnr
	 * @param ID
	 * @param IDType Either nurseID or doctorID.
	 */
	public void updateJournal(String txt, int pnr, int ID, String IDType) {
		String sql = "UPDATE Journals SET journal = CONCAT(journal, ?) WHERE pnr = ? AND " + IDType + " = ?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, txt);
			ps.setInt(2, pnr);
			ps.setInt(3, ID);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Lets the nurse/doctor get the journal if the id or the the division is correct.
	 * @param pnr
	 * @param ID
	 * @param IDType
	 * @return The text in the journal as a String.
	 */
	public String getJournal(int pnr, int ID, String IDType) {
		System.out.println("ID " + ID + " type " + IDType);
		int division = ID / 100;
		String sql = "SELECT journal FROM Journals WHERE pnr = ? AND (" + IDType + " = ? OR division = ?)";
		PreparedStatement ps = null;
		ResultSet rs;
		String txt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			ps.setInt(2, ID);
			ps.setInt(3, division);
			rs = ps.executeQuery();
			rs.next();
			txt = rs.getString("journal");
		} catch (SQLException e) {
			e.printStackTrace();
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
	public String getJournal(int pnr) {
		String sql = "SELECT journal FROM Journals WHERE pnr = ?";
		PreparedStatement ps = null;
		ResultSet rs;
		String txt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			rs = ps.executeQuery();
			txt = rs.getString("journal");
		} catch (SQLException e) {
			e.printStackTrace();
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
	 */
	public void deleteJournal(int pnr) {
		String sql = "DELETE FROM Journals WHERE pnr = ?";
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				ps.close();
				}  catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
}






