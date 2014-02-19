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
	
	public void addJournal(int pnr, int doctorID, int nurseID, String journal) { //addPerson i Gdocs
		String sql = "INSERT INTO Journals VALUES(?, ?, ?, ?)";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			ps.setInt(2, doctorID);
			ps.setInt(3, nurseID);
			ps.setString(4, journal);
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
	
	public void addJournal(String txt, int pnr) {
		String sql = "UPDATE Journals SET journal = CONCAT(journal, ?) WHERE pnr = ?";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, txt);
			ps.setInt(2, pnr);
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
	
	public String getJournal(int pnr, int ID, String IDType) {
		String sql = "SELECT journal FROM Journals WHERE pnr = ? AND ? = ?";
		PreparedStatement ps = null;
		ResultSet rs;
		String txt = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pnr);
			ps.setString(2, IDType);
			ps.setInt(3, ID);
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
}






