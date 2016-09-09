package Storage.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Data.WebSource;
import Storage.DBConnection;
import Storage.FileHandler;

public class WebSourceDAO {
	private DBConnection pool;
	private Connection con;
	private String dir;
	
	public WebSourceDAO(){ con=null; pool=DBConnection.getInstance(); dir = System.getProperty("user.dir") + "/WebSource/"; };
	
	public boolean insert(WebSource ws)
	{
		String u = ws.getUrl();
		String dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
		String fileName = getFileName(u);
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into websource (url, web_url, web_name, target, date, dir, source_name) values (?, ?, ?, ?, ?, ?, ?)";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, ws.getUrl() );
			pstmt.setString(2, ws.getWebUrl() );
			pstmt.setString(3, ws.getWebName() );
			pstmt.setString(4, ws.getTarget() );
			pstmt.setString(5, ws.getDate().toString() );
			pstmt.setString(6, dir );
			pstmt.setString(7, ws.getSourceName() );

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
		
		FileHandler fh = new FileHandler();
		fh.writeWebFile(dir, fileName, ws.getSource());
		
		return success;
	}
	public boolean insertList(ArrayList<WebSource> wsl)
	{
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "insert into websource (url, web_url, web_name, target, date, dir, source_name) values(?, ?, ?, ?, ?, ?, ?)";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			String u, dir, fileName;
			for(int i = 0; i < wsl.size(); i++)
			{
				pstmt = con.prepareStatement(sql);
				
				WebSource ws = wsl.get(i);
				
				u = ws.getUrl();
				dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
				fileName = getFileName(u);
				
				pstmt.setString(1, ws.getUrl() );
				pstmt.setString(2, ws.getWebUrl() );
				pstmt.setString(3, ws.getWebName() );
				pstmt.setString(4, ws.getTarget() );
				pstmt.setString(5, ws.getDate().toString() );
				pstmt.setString(6, dir );
				pstmt.setString(7, ws.getSourceName() );

				int count = pstmt.executeUpdate();		// 실행한 갯수만큼 count에 리턴
				if( count > 0 )
				{
					success = true;
					FileHandler fh = new FileHandler();
					fh.writeWebFile(dir, fileName, ws.getSource());
				}
				if(pstmt != null)
					pstmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			pool.freeConnection(con, pstmt);
		}
		return success;
	}
	
	public boolean delete(WebSource ws)
	{
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from websource where url=? and source_name=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, ws.getUrl() );
			pstmt.setString(2, ws.getSourceName() );

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

		String u = ws.getUrl();
		String dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
		String fileName = getFileName(u);
		
		FileHandler fh = new FileHandler();
		fh.fileDelete(dir, fileName, "txt");
		
		return success;
	}
	public boolean delete(String webName)
	{
		ArrayList<WebSource> targetForDelete = getList(webName);
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from websource where webName=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, webName );

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

		String u, dir, fileName;
		FileHandler fh = new FileHandler();		
		for(int i=0; i < targetForDelete.size(); i++)
		{
			WebSource ws = targetForDelete.get(i);
			
			u = ws.getUrl();
			dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
			fileName = getFileName(u);

			fh.fileDelete(dir, fileName, "txt");
		}
		
		return success;
	}
	public boolean delete(String webName, String target)
	{
		ArrayList<WebSource> targetForDelete = getList(webName, target);
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from websource where webName=? and target=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return false;
		}
		try {
			pstmt = con.prepareStatement(sql);
			
			pstmt.setString(1, webName );
			pstmt.setString(2, target );

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
		
		String u, dir, fileName;
		FileHandler fh = new FileHandler();		
		for(int i=0; i < targetForDelete.size(); i++)
		{
			WebSource ws = targetForDelete.get(i);
			
			u = ws.getUrl();
			dir = System.getProperty("user.dir") + "/" + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
			fileName = getFileName(u);

			fh.fileDelete(dir, fileName, "txt");
		}
		
		return success;
	}
	public boolean deleteAll()
	{
		ArrayList<WebSource> targetForDelete = getAllList();
		
		boolean success = false;
		PreparedStatement pstmt = null;
		String sql = "delete from websource";

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
		
		String u, dir, fileName;
		FileHandler fh = new FileHandler();		
		for(int i=0; i < targetForDelete.size(); i++)
		{
			WebSource ws = targetForDelete.get(i);
			
			u = ws.getUrl();
			dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
			fileName = getFileName(u);

			fh.fileDelete(dir, fileName, "txt");
		}
		
		return success;
	}
	
	public boolean update(WebSource ws) { return update(ws, ws); }
	public boolean update(WebSource ws, WebSource mws) {	
		boolean success = false;
		PreparedStatement pstmt = null;	
		
		ws = getSource(ws.getUrl());
		if(ws==null)
			return false;

		if(!mws.getSource().equals(ws.getSource()) || !ws.getUrl().equals(mws.getUrl()))
		{
			String sql = "update websource set url=?, web_url=?, web_name=?, target=?, date=?, dir=?, source_name=? "
					+ "where url=? and source_name=?";

			con = pool.getConnection();
			if (con == null) {
				System.out.println("연결이 이루어지지 않았다");
				return false;
			}
			try {
				pstmt = con.prepareStatement(sql);
				// 인자로 받은 GuestBook 객체를 이용해 사용자가 수정한 값을 가져와 SQL문 완성
				pstmt.setString(1, mws.getUrl());
				pstmt.setString(2, mws.getWebUrl());
				pstmt.setString(3, mws.getWebName());
				pstmt.setString(4, mws.getTarget());
				pstmt.setString(5, mws.getDate().toString());
				pstmt.setString(6, mws.getSource());
				pstmt.setString(7, mws.getSourceName());

				pstmt.setString(8, ws.getUrl());
				pstmt.setString(9, ws.getSourceName());

				int count = pstmt.executeUpdate(); // 실행한 갯수만큼 count에 리턴
				if (count > 0)
					success = true;
			} catch (SQLException e) {
				return success;
			} finally {
				pool.freeConnection(con, pstmt);
			}
			FileHandler fh = new FileHandler();
			
			String u = ws.getUrl();
			String dir = this.dir + ws.getWebName() + "/" + ws.getTarget() + "/" + ws.getSourceName();
			String fileName = getFileName(u);
			
			fh.fileDelete(dir, fileName, "txt");
			
			dir = this.dir + mws.getWebName() + "/" + mws.getTarget() + "/" + mws.getSourceName();
			fh.writeWebFile(dir, fileName, mws.getSource());
		}
		return success;
	}
	public boolean update(ArrayList<WebSource> wsl) {
		boolean success = false;

		for(int i = 0; i < wsl.size(); i++)
			if(!update(wsl.get(i)))
				return insert(wsl.get(i));

		return success;
	}
	
	public WebSource get(String id){
		WebSource ws = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource where id=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			rs.next();

			ws = new WebSource(rs.getString("url"), rs.getString("web_url"), rs.getString("web_name")
					, rs.getString("target"), rs.getString("date"), rs.getString("source_name"));
			
			String u = ws.getUrl();
			String dir = rs.getString("dir");
			String fileName = getFileName(u);
			FileHandler fh = new FileHandler();
			String source = fh.readWebFile(dir, fileName);
			
			ws.setSource(source);

			rs.close();
		} catch (SQLException e) {
//			e.printStackTrace();
			return null;
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return ws;
	}
	public WebSource getSource(String url){
		WebSource ws = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource where url=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, url);
			rs = pstmt.executeQuery();

			rs.next();
			
			ws = new WebSource(url, rs.getString("web_url"), rs.getString("web_name")
					, rs.getString("target"), rs.getString("date"), rs.getString("source_name"));
			
			String dir = rs.getString("dir");
			String fileName = getFileName(url);
			FileHandler fh = new FileHandler();
			String source = fh.readWebFile(dir, fileName);
			
			ws.setSource(source);

			rs.close();
		} catch (SQLException e) {
//			e.printStackTrace();
			return null;
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		
		return ws;
	}
	
	public ArrayList<WebSource> getAllList() {
		ArrayList<WebSource> list = new ArrayList<WebSource>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			rs = pstmt.executeQuery();

			FileHandler fh = new FileHandler();
			String dir, fileName;
			String url, web_url, web_name, target, date, source_name, source;
			while (rs.next()) {
				url = rs.getString("url");
				web_url = rs.getString("web_url");
				web_name = rs.getString("web_name");
				target = rs.getString("target");
				date = rs.getString("date");
				dir = rs.getString("dir");
				source_name = rs.getString("source_name");
				
				fileName = getFileName(url);
				source = fh.readWebFile(dir, fileName);
				
				WebSource ws = new WebSource(url, web_url, web_name, target, date, source_name, source);

				// 리스트에 추가
				list.add(ws);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			list = null;
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}

	public ArrayList<WebSource> getList(String webName) {
		ArrayList<WebSource> list = new ArrayList<WebSource>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource where web_name=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			rs = pstmt.executeQuery();

			FileHandler fh = new FileHandler();
			String dir, fileName;
			String url, web_url, web_name, target, date, source_name, source;
			while (rs.next()) {
				url = rs.getString("url");
				web_url = rs.getString("web_url");
				web_name = rs.getString("web_name");
				target = rs.getString("target");
				date = rs.getString("date");
				dir = rs.getString("dir");
				source_name = rs.getString("source_name");
				
				fileName = getFileName(url);
				source = fh.readWebFile(dir, fileName);
				
				WebSource ws = new WebSource(url, web_url, web_name, target, date, source_name, source);

				// 리스트에 추가
				list.add(ws);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	public ArrayList<WebSource> getList(String webName, String target) {
		ArrayList<WebSource> list = new ArrayList<WebSource>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource where web_name=? and target=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			pstmt.setString(2, target);
			rs = pstmt.executeQuery();

			FileHandler fh = new FileHandler();
			String dir, fileName;
			String url, web_url, web_name, ttarget, date, source_name, source;
			while (rs.next()) {
				url = rs.getString("url");
				web_url = rs.getString("web_url");
				web_name = rs.getString("web_name");
				ttarget = rs.getString("target");
				date = rs.getString("date");
				dir = rs.getString("dir");
				source_name = rs.getString("source_name");
				
				fileName = getFileName(url);
				source = fh.readWebFile(dir, fileName);
				
				WebSource ws = new WebSource(url, web_url, web_name, ttarget, date, source_name, source);

				// 리스트에 추가
				list.add(ws);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	public ArrayList<WebSource> getList(String webName, String target, String sourceName) {
		ArrayList<WebSource> list = new ArrayList<WebSource>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select * from websource where web_name=? and target=? and source_name=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			pstmt.setString(2, target);
			pstmt.setString(3, sourceName);
			rs = pstmt.executeQuery();

			FileHandler fh = new FileHandler();
			String dir, fileName;
			String url, web_url, web_name, ttarget, date, source_name, source;
			while (rs.next()) {
				url = rs.getString("url");
				web_url = rs.getString("web_url");
				web_name = rs.getString("web_name");
				ttarget = rs.getString("target");
				date = rs.getString("date");
				dir = rs.getString("dir");
				source_name = rs.getString("source_name");
				
				fileName = getFileName(url);
				source = fh.readWebFile(dir, fileName);
				
				WebSource ws = new WebSource(url, web_url, web_name, ttarget, date, source_name, source);

				// 리스트에 추가
				list.add(ws);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}

	public ArrayList<String> getUrlList(String webName)
	{
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url from websource where web_name=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 리스트에 추가
				list.add(rs.getString("url"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	public ArrayList<String> getUrlList(String webName, String target)
	{
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url from websource where web_name=? and target=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			pstmt.setString(2, target);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 리스트에 추가
				list.add(rs.getString("url"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	public ArrayList<String> getUrlList(String webName, String target, String sourceName)
	{
		ArrayList<String> list = new ArrayList<String>();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String SQL = "select url from websource where web_name=? and target=? and source_name=?";

		con = pool.getConnection();
		if (con == null) {
			System.out.println("연결이 이루어지지 않았다");
			return null;
		}
		try {
			pstmt = con.prepareStatement(SQL);
			pstmt.setString(1, webName);
			pstmt.setString(2, target);
			pstmt.setString(3, sourceName);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// 리스트에 추가
				list.add(rs.getString("url"));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt, rs);
		}
		return list;
	}
	
	private String getFileName(String u)
	{
		String fileName = u.substring(u.indexOf("//")+2).substring(u.substring(u.indexOf("//")+2).indexOf("/")+1);
		fileName = fileName.replace("/", "-");
		fileName = fileName.replace("?", "@");
		fileName = fileName.replace(".", "#");
		
		return fileName;
	}
}
