package Storage.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Data.Comment;
import Storage.DBConnection;

public class CommentDAO {

	private DBConnection pool;
	private Connection con;

	public CommentDAO(){ pool = DBConnection.getInstance(); con = null;};

	public boolean insert(String docId, Comment com)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into comment (ID, author, date, sentence, asociated_id, document_id) values(?, ?, ?, ?, ?, ?)";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, com.getID());
			pstmt.setString(2, com.getAuthor());
			pstmt.setString(3, com.getDate().toString());
			pstmt.setString(4, com.getSentence());
			pstmt.setString(5, com.getAsociatedComment());

			pstmt.setString(6, docId);

			int count = pstmt.executeUpdate(); // 실행한 갯수만큼 count에 리턴
			if (count > 0)
				success = true;
			if (pstmt != null)
				pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean insertList(String docId, ArrayList<Comment> coml)
	{
		ArrayList<Comment> ccoml = new ArrayList<Comment>();
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into comment (ID, author, date, sentence, asociated_id, document_id) values(?, ?, ?, ?, ?, ?)";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			for(int i = 0; i < coml.size(); i++)
			{
				pstmt = con.prepareStatement(sql);
				
				Comment com = coml.get(i);
				pstmt.setString(1, com.getID() );
				pstmt.setString(2, com.getAuthor() );
				pstmt.setString(3, com.getDate().toString() );
				pstmt.setString(4, com.getSentence() );
				pstmt.setString(5, com.getAsociatedComment() );

				pstmt.setString(6, docId );

				int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
				if( count > 0 )
					success = true;
				if(pstmt != null)
					pstmt.close();
				
				ccoml.add(com);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			for(int i = 0; i < ccoml.size(); i++)
				delete(ccoml.get(i));
			
			return false;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}

	public boolean update(Comment com)
	{
		return update(com, com);
	}
	public boolean update(Comment com, Comment mcom) {
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "update comment set author=?, date=?, sentence=?, asociated_id=? where ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			// 인자로 받은 GuestBook 객체를 이용해 사용자가 수정한 값을 가져와 SQL문 완성
			pstmt.setString(1, mcom.getAuthor() );
			pstmt.setString(2, mcom.getDate().toString() );
			pstmt.setString(3, mcom.getSentence() );
			pstmt.setString(4, mcom.getAsociatedComment() );
			
			pstmt.setString(5, com.getID() );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;
		} catch (SQLException e) {
//			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean update(Comment com, String docId, Comment mcom) {
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "update comment set ID=?, author=?, date=?, sentence=?, asociated_id=?, docId=? where ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			// 인자로 받은 GuestBook 객체를 이용해 사용자가 수정한 값을 가져와 SQL문 완성
			pstmt.setString(1, mcom.getID() );
			pstmt.setString(2, mcom.getAuthor() );
			pstmt.setString(3, mcom.getDate().toString() );
			pstmt.setString(4, mcom.getSentence() );
			pstmt.setString(5, mcom.getAsociatedComment() );
			pstmt.setString(6, docId );
			
			pstmt.setString(7, com.getID() );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;
		} catch (SQLException e) {
//			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean updateAll(String docId, ArrayList<Comment> coml) {
		boolean success = false;

		for(int i = 0; i < coml.size(); i++)
			if(!update(coml.get(i)))
				insert(docId, coml.get(i));

		return success;
	}

	public Comment get(String comId) {
		Comment com = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select ID, author, date, sentence, asociated_id from comment where id=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, comId);
			rs = pstmt.executeQuery();

			rs.next(); 
			com = new Comment(rs.getString("ID"), rs.getString("author"), rs.getString("date")
					, rs.getString("sentence"), rs.getString("asociated_id"));

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return com;
	}
	public ArrayList<Comment> getAllList(String docId) {
		ArrayList<Comment> list = new ArrayList<Comment>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select ID, author, date, sentence, asociated_id from comment where document_id=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, docId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Comment com = new Comment(rs.getString("ID"), rs.getString("author"), rs.getString("date")
						, rs.getString("sentence"), rs.getString("asociated_id"));

				// 리스트에 추가
				list.add(com);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	public ArrayList<Comment> getAllList() {
		ArrayList<Comment> list = new ArrayList<Comment>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select ID, author, date, sentence, asociated_id from comment";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				Comment com = new Comment(rs.getString("ID"), rs.getString("author"), rs.getString("date")
						, rs.getString("sentence"), rs.getString("asociated_id"));

				// 리스트에 추가
				list.add(com);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	
	public boolean delete(String docId, Comment com)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from comment where ID=? and document_ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, com.getID() );
			pstmt.setString(2, docId );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean delete(Comment com)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from comment where ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, com.getID() );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean deleteAll(String docId)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from comment where document_ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, docId );
			
			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean deleteAll()
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from comment";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
}
