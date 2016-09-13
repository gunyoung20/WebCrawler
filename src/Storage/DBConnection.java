package Storage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import java.sql.Statement;

public class DBConnection {
	private static DBConnection instance = null;
	
	public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }

        return instance;
    }
	
	public Connection getConnection() {
		Connection con = null;
		//String driverName = "org.gjt.mm.mysql.Driver";
		String driverName = "com.mysql.jdbc.Driver";
		String dbURL = "jdbc:mysql://192.168.0.4:8888/thewaroncyber";
		try {
			Class.forName(driverName);
			con = DriverManager.getConnection(dbURL, "server_crawler", "b403");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return con;
	}

	public void freeConnection(Connection con, PreparedStatement pstmt) {
		// Connection - PreParedStatement - ResultSet
		// ���� ���� �������δݾƾ� �Ѵ�.
		try {
			if (pstmt != null)
				pstmt.close();
			if (con != null)
				con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void freeConnection(Connection con, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (con != null)
				con.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void freeConnection(Connection con, Statement stmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (con != null)
				con.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void freeConnection(Connection con, CallableStatement cstmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (cstmt != null)
				cstmt.close();
			if (con != null)
				con.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
