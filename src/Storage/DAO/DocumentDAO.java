package Storage.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Data.Comment;
import Data.Document;
import Storage.DBConnection;

public class DocumentDAO {

	private DBConnection pool;
	private Connection con;
	
	public DocumentDAO(){ pool = DBConnection.getInstance(); con = null;};
	
	public boolean insert(Document doc)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into document (ID, title, author, date, story, web_id) values(?, ?, ?, ?, ?, (select id from websource where url=? and source_name='Document'))";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, doc.getTitleNum() );
			pstmt.setString(2, doc.getTitle() );
			pstmt.setString(3, doc.getAuthor() );
			pstmt.setString(4, doc.getDate().toString() );
			pstmt.setString(5, doc.getStory() );
			pstmt.setString(6, doc.getWebUrl() );

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
		
		CommentDAO cdao = new CommentDAO();
		if(!cdao.insertList(doc.getTitleNum(), doc.getCommentList()))
		{
			delete(doc);
			return false;
		}
		
		return success;
	}
	public boolean insertList(ArrayList<Document> docl)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into document (ID, title, author, date, story, web_id) values(?, ?, ?, ?, ?, (select id from websource where url=? and source_name='Document'))";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			for(int i = 0; i < docl.size(); i++)
			{
				pstmt = con.prepareStatement(sql);
				
				Document doc = docl.get(i);
				pstmt.setString(1, doc.getTitleNum() );
				pstmt.setString(2, doc.getTitle() );
				pstmt.setString(3, doc.getAuthor() );
				pstmt.setString(4, doc.getDate().toString() );
				pstmt.setString(5, doc.getStory() );
				pstmt.setString(6, doc.getWebUrl() );

				int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
				if( count > 0 )
					success = true;
				if(pstmt != null)
					pstmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}

		Document doc;
		CommentDAO cdao = new CommentDAO();
		for(int i = 0; i < docl.size(); i++)
		{
			doc = docl.get(i);
			if (!cdao.insertList(doc.getTitleNum(), doc.getCommentList())) 
			{
				for (int j = 0; j < docl.size(); j++)
				{
					cdao.deleteAll(docl.get(j).getTitleNum());
					delete(docl.get(j));
				}
				return false;
			}
		}
		
		return success;
	}

	public boolean update(Document doc)
	{
		return update(doc, doc);
	}
	public boolean update(Document doc, Document mdoc) {
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "update document set title=?, author=?, date=?, story=?, web_id=(select id from websource where url=? and source_name='Document') "
				+ "where document_ID=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			// 인자로 받은 GuestBook 객체를 이용해 사용자가 수정한 값을 가져와 SQL문 완성
			pstmt.setString(1, mdoc.getTitle() );
			pstmt.setString(2, mdoc.getAuthor() );
			pstmt.setString(3, mdoc.getDate().toString() );
			pstmt.setString(4, mdoc.getStory() );
			pstmt.setString(5, mdoc.getWebUrl() );
			
			pstmt.setString(6, doc.getTitleNum() );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;
		} catch (SQLException e) {
//			e.printStackTrace();
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		
		CommentDAO cdao = new CommentDAO();
		cdao.updateAll(mdoc.getTitleNum(), mdoc.getCommentList());
		
		return success;
	}
	public boolean updateAll(ArrayList<Document> docl) {
		boolean success = false;

		for(int i = 0; i < docl.size(); i++)
			if(!update(docl.get(i)))
				return insert(docl.get(i));

		return success;
	}

	public Document get(String docId) {
		Document doc = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url, document.ID, title, author, document.date, story from document join websource where document.id=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, docId);
			rs = pstmt.executeQuery();

			rs.next(); 
			doc = new Document(rs.getString("url"), rs.getString("ID"), rs.getString("title")
						, rs.getString("author"), rs.getString("date"), rs.getString("story"));

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		CommentDAO cdao = new CommentDAO();
		doc.setCommentList(cdao.getAllList(doc.getTitleNum()));
		
		return doc;
	}
	
	public ArrayList<Document> getAllList() {
		ArrayList<Document> list = new ArrayList<Document>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url, document.ID, title, author, document.date, story from document join websource";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();

			CommentDAO cdao = new CommentDAO();
			while (rs.next()) {
				Document doc = new Document(rs.getString("url"), rs.getString("ID"), rs.getString("title")
						, rs.getString("author"), rs.getString("date"), rs.getString("story"));

				doc.setCommentList(cdao.getAllList(doc.getTitleNum()));

				// 리스트에 추가
				list.add(doc);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}

	public boolean delete(Document doc)
	{
		CommentDAO cdao = new CommentDAO();
		ArrayList<Comment> comList = cdao.getAllList(doc.getTitleNum());
		if(!cdao.deleteAll(doc.getTitleNum()))
		{
			System.err.println("Could not delete Comment - if(!cdao.deleteAll(doc.getTitleNum()))");
			return false;
		}
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from document where ID=? and date=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, doc.getTitleNum() );
			pstmt.setString(1, doc.getDate().toString() );

			int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
			if( count > 0 )
				success = true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cdao.insertList(doc.getTitleNum(), comList);
			
			return success;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	public boolean deleteAll()
	{
		CommentDAO cdao = new CommentDAO();
		if(!cdao.deleteAll())
		{
			System.err.println("Could not delete Comment - if(!cdao.deleteAll(doc.getTitleNum()))");
			return false;
		}
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from document";

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
	
	public ArrayList<Document> searchTitle(String title) {
		ArrayList<Document> list = new ArrayList<Document>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url, document.ID, title, author, document.date, story from document join websource where document.id=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, "%"+title+"%");
			rs = pstmt.executeQuery();

			CommentDAO cdao = new CommentDAO();
			while (rs.next()) {
				Document doc = new Document(rs.getString("url"), rs.getString("ID"), rs.getString("title")
						, rs.getString("author"), rs.getString("date"), rs.getString("story"));

				doc.setCommentList(cdao.getAllList(doc.getTitleNum()));
				// 리스트에 추가
				list.add(doc);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
}
