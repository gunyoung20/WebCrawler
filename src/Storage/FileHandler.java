package Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;

public class FileHandler {

	// constructor
	public FileHandler() {
		this(System.getProperty("user.dir"), "", "", System.getProperty("file.encoding"));
	}
	public FileHandler(String targetName, String extension) {
		this(System.getProperty("user.dir"), targetName, extension, System.getProperty("file.encoding"));
	}
	public FileHandler(String directory, String targetName, String extension) {
		this(directory, targetName, extension, System.getProperty("file.encoding"));
	}
	public FileHandler(String directory, String targetName, String extension, String charset) {
		this.directory = directory + "/" + targetName;
		this.targetName = targetName;
		this.extension = extension;
		this.charset = charset;
		
	}

	public void saveDocumentUrlList(String dir, String fileName, ArrayList<String> documentUrlList)
	{
		if(documentUrlList.size()  == 0)
		{
			System.err.println(dir + "/" + fileName + " Data is Not found For Writing file");
			return;
		}

		if ((new File(dir)).mkdirs() == true) {
			System.out.println("Directories : " + dir + " created");
		}
		
	    try 
	    {
	    	BufferedWriter bw;
			if(!extension.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + "." + extension, false));
			else if(fileName.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName, false));
			else
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + extension, false));

			for (int i = 0; i < documentUrlList.size(); i++) {
				bw.write(documentUrlList.get(i));
				bw.newLine();
			}
			bw.close();
	    }
	    
	    catch (IOException e) 
	    {
	       System.err.println(e); 
	    }
	}
	public void saveDocumentList(ArrayList<Document> documentList)
	{
		saveDocumentList(this.directory, this.targetName, documentList);
	}
	public void saveDocumentList(String dir, String fileName, ArrayList<Document> documentList)
	{
		ArrayList<Document> loadedDocument = loadDocumentList();
		if(loadedDocument == null)
			loadedDocument = new ArrayList<Document>();
		
		boolean exist = false;
		for(int i = 0; i < documentList.size(); i++)
		{
			for(int j = 0; j < loadedDocument.size(); j++)
			{
				// Case same document title
				if(documentList.get(i).getTitle().equals(loadedDocument.get(j).getTitle()))
				{
					// Case changed comments
					if(checkChangedComment(documentList.get(i), loadedDocument.get(j)))
					{
						// Replace document
						loadedDocument.remove(j);
						loadedDocument.add(j, documentList.get(i));
					}
					// For not changing again
					exist = true;
					break;
				}
			}
			// Case same title
			if(exist)
			{
				exist = false;
				continue;
			}
			// Case different title
			loadedDocument.add(documentList.get(i));
		}
		
		writeDocumentList(loadedDocument);
	}
	public void writeDocumentList(ArrayList<Document> documentList)
	{
		// record list for saving file
		ArrayList<ArrayList<String>> docRecordList = new ArrayList<ArrayList<String>>();
		// !record list for saving file
		
		// temporary storage for data
		Document doc;
		ArrayList<Comment> comList;
		Comment com;
		// !temporary storage for data
		
		// extract records of documents
		for(int i = 0; i < documentList.size(); i++)
		{
			ArrayList<String> docRecord = new ArrayList<String>();
			// Extract records from documents
			doc = documentList.get(i);
			// Basic element
			docRecord.add(doc.getTitleNum());
			docRecord.add(doc.getTitle());
			docRecord.add(doc.getAuthor());
			docRecord.add(doc.getDate().toString());
			docRecord.add(doc.getStory());
			// !Basic element
			
			// Analyzed element
			if(doc.getMorphemeOfTitle()!=null)
			{
			}
			// !Analyzed element
			// !Extract records from documents
						
			// extract records from comments about one document
			ArrayList<ArrayList<String>> comRecordList = new ArrayList<ArrayList<String>>();
			comList = doc.getCommentList();
			for(int j = 0; j < comList.size(); j++)
			{
				ArrayList<String> comRecord = new ArrayList<String>();
				com = comList.get(j);
				// basic element
				comRecord.add(com.getID());
				comRecord.add(com.getAuthor());
				comRecord.add(com.getDate().toString());
				comRecord.add(com.getAsociatedComment());
				comRecord.add(com.getSentence());
				// !basic element
				
				// Analyzed element
				if(com.getMorpheme() != null)
				{
				}
				// !Analyzed element
				
				comRecordList.add(comRecord);
			}
			// !extract records from comments
			
			// save record of comment
			writeFile(directory + CommentDirectory, targetName + "-" + doc.getTitleNum(), comRecordList);			
			// !save record of comment
						
			// add transfered document records
			docRecordList.add(docRecord);
			// !add transfered document records
		}
		// !extract records of documents
		
		// save record of document
		writeFile(directory, targetName, docRecordList);
		// !save record of document
	}
	public ArrayList<Document> loadDocumentList()
	{
		// Read records for documents
		ArrayList<Document> documentList = new ArrayList<Document>();
		ArrayList<ArrayList<String>> recordOfDocumentList = readFile(directory, targetName);
		// !Read records for documents
		
		if(recordOfDocumentList == null)
			return null;
		else
		{
			// Insert documents data from records
			for (int i = 0; i < recordOfDocumentList.size(); i++) {
				Document doc = new Document();
				ArrayList<String> recordOfDocument = recordOfDocumentList.get(i);

				// Basic elements
				doc.setTitleNum(recordOfDocument.get(0));
				doc.setTitle(recordOfDocument.get(1));
				doc.setAuthor(recordOfDocument.get(2));
				doc.setDate(new Date(recordOfDocument.get(3)));
				doc.setStory(recordOfDocument.get(4));
				// !Basic elements

				// Analyzed elements
				if (recordOfDocument.size() > 5) 
				{
				}
				// !Analyzed elements

				// Read records for comments
				ArrayList<ArrayList<String>> recordOfCommentList = readFile(directory + CommentDirectory,
						targetName + "-" + doc.getTitleNum());
				// !Read records for comments

				if (recordOfCommentList == null)
					recordOfCommentList = new ArrayList<ArrayList<String>>();

				ArrayList<Comment> comList = new ArrayList<Comment>();
				// Insert comments from records
				for (int j = 0; j < recordOfCommentList.size(); j++) {
					Comment com = new Comment();
					ArrayList<String> recordOfComment = recordOfCommentList.get(j);

					// Basic element
					com.setID(recordOfComment.get(0));
					com.setAuthor(recordOfComment.get(1));
					com.setDate(new Date(recordOfComment.get(2)));
					com.setAsociatedComment(recordOfComment.get(3));
					com.setSentence(recordOfComment.get(4));
					// !Basic element

					// Analyzed elements
					if (recordOfComment.size() > 4) {
					}
					// !Analyzed elements

					comList.add(com);
				}
				// !Insert comments from records

				// Match comments to documents
				doc.setCommentList(comList);
				// !Match comments to documents

				documentList.add(doc);
			}
		}
		// !Insert documents data from records
		
		return documentList;
	}
	
	private void writeFile(String dir, String fileName, ArrayList<ArrayList<String>> recordList)
	{
		if(recordList.size()  == 0)
		{
			System.err.println(dir + "/" + fileName + " Data is Not found For Writing file(void writeFile(String dir, String fileName, ArrayList<ArrayList<String>> recordList))");
			return;
		}

		if ((new File(dir)).mkdirs() == true) {
			System.out.println("Directories : " + dir + " created");
		}
		
	    try 
	    {
	    	BufferedWriter bw;
			if(!extension.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + "." + extension, false));
			else if(fileName.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName, false));
			else
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + extension, false));

			ArrayList<String> record;
			for (int i = 0; i < recordList.size(); i++) {
				record = recordList.get(i);
				for (int j = 0; j < record.size(); j++) {
					bw.write(record.get(j).replace("\n", EndLineFromStory) + DistributionOfElement);
				}
				bw.newLine();
			}
			bw.close();
	    }
	    
	    catch (IOException e) 
	    {
	       System.err.println(e); 
	    }
	}
	
	private ArrayList<ArrayList<String>> readFile(String dir, String fileName)
	{
		ArrayList<ArrayList<String>> recordList = new ArrayList<ArrayList<String>>();
		ArrayList<String> record = new ArrayList<String>();
		
		if(fileExist(dir, fileName, extension) == false)
		{
			System.err.println(dir + "/" + fileName + " file for reading is not found(ArrayList<ArrayList<String>> readFile(String dir, String fileName))");
			return null;
		}
		try{
			BufferedReader in;
			if(!extension.contains("."))
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName + "." + extension), charset));
			else if(fileName.contains("."))
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName), charset));
			else
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName + extension), charset));
		
			while(in.ready())
				record.add(in.readLine().replace(EndLineFromStory, "\n"));
			
			in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return recordList;
		}
		
		for(int i = 0; i < record.size(); i++)
		{
			String temp = record.get(i);
			ArrayList<String> splitRecord = new ArrayList<String>();
			while(temp.contains(DistributionOfElement))
			{
				splitRecord.add(temp.substring(0, temp.indexOf(DistributionOfElement)));
				temp = temp.substring(temp.indexOf(DistributionOfElement)+1);
			}
			
			recordList.add(splitRecord);
		}
		
		return recordList;
	}
	
	public void writeWebFile(String dir, String fileName, String web)
	{
		if(web == null)
		{
			System.err.println(dir + "/" + fileName + " Data is Not found For Writing file(void writeWebFile(String dir, String fileName, String web))");
			return;
		}

		if ((new File(dir)).mkdirs() == true) {
			System.out.println("Directories : " + dir + " created");
		}
		
	    try 
	    {
	    	BufferedWriter bw;
			if(!extension.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + "." + extension, false));
			else if(fileName.contains("."))
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName, false));
			else
				bw = new BufferedWriter(new FileWriter(dir + "/" + fileName + extension, false));

			bw.write(web);
			bw.close();
	    }
	    
	    catch (IOException e) 
	    {
	       System.err.println(e); 
	    }
	}
	public String readWebFile(String dir, String fileName, String extension)
	{
		String record = "";
		
		if(fileExist(dir, fileName, extension) == false)
		{
			System.err.println(dir + "/" + fileName + " file for reading is not found(String readWebFile(String dir, String fileName))");
			return null;
		}
		try{
			BufferedReader in;
			if(!extension.contains("."))
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName + "." + extension), charset));
			else if(fileName.contains("."))
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName), charset));
			else
				in = new BufferedReader(new InputStreamReader(new FileInputStream(dir + "/" + fileName + extension), charset));
		
			while(in.ready())
				record = record + in.readLine();
			
			in.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return record;
		}
		
		return record;
	}
	public String readWebFile(String dir, String fileName)
	{
		return readWebFile(dir, fileName, extension); 
	}
	
	private boolean checkChangedComment(Document prevDoc, Document nowDoc)
	{
		boolean changed = true;
		ArrayList<Comment> prevComList = prevDoc.getCommentList();
		ArrayList<Comment> nowComList = nowDoc.getCommentList();
		for(int i = 0; i < nowComList.size(); i++)
		{
			for(int j = 0; j < prevComList.size(); j++)
			{
				if(nowComList.get(i).equals(prevComList.get(j)))
				{
					changed = false;
					break;
				}
			}
			if(changed)
				break;
			changed = true;
		}
		return changed;
	}
	
 	public String[] getDirectoryList()
	{
		return (new File(directory)).list();
	}
	
	static public String[] getDirectoryList(String path)
	{
		return (new File(path)).list();
	}
	
	static public String nowPath()
	{
		return System.getProperty("user.dir");
	}
	
	static public boolean fileExist(String directory, String fileName)
	{
		String[] sa = (new File(directory)).list();

		if(sa == null)return false;
		
		for(int i=0;i<sa.length;i++)
			if(sa[i].compareTo(fileName) == 0)
				return true;
		return false;
	}
	
	static public boolean fileExist(String directory, String fileName, String extension)
	{
		String s;
		if(extension.indexOf('.') == -1)
			s = fileName + "." +extension;
		else
			s = fileName + extension;
		return fileExist(directory, s);
	}
	
	public boolean fileDelete(String directory, String fileName, String extension){
		String target = directory+"/"+fileName;
		
		if(fileName.contains("."))
			return (new File(target)).delete();
		else if(extension.contains("."))
			return (new File(target + extension)).delete();
		else
			return (new File(target + "." + extension)).delete();
	}
		
	private String directory;		
	private String targetName;		
	private String extension;		
	private String charset;			
	final public String EndLineFromStory = "§";
	final public String DistributionOfElement = "º";
	final public String CommentDirectory = "/Comment";
	
	public void setDirectory(String dir)
	{
		this.directory = dir;
	}
	
	public void setTargetName(String targetName)
	{
		this.targetName = targetName;
	}
	
	public void setExtension(String ex)
	{
		this.extension = ex;
		
	}
	public void setCharSet(String cs)
	{
		this.charset = cs;
	}
	public String getDirectory()
	{
		return directory;
	}
	public String getTargetName()
	{
		return targetName;
	}
	public String getExtension()
	{
		return extension;
	}
	// character set
	public String getCharset()
	{
		return charset;
	}
}
