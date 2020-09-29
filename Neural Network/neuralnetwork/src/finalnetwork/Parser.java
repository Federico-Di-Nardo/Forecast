package finalnetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class Parser {
	

	
	private Node mainContent;
	private String fileName;
	
	public void load(String file) throws Exception {
		this.fileName = file;
		String data = "<MainContent>";
		BufferedReader reader = new BufferedReader(new FileReader (file));
		String line;
		while((line = reader.readLine())!= null){
			if(!line.trim().equals(""))
				data += line;
		}
		data += "</MainContent>";
		this.generateNodes(data);
	}
	
	public void create(String fileName) {
		this.fileName = fileName;
		this.mainContent = new Node("MainContent");
	}
	
	private void generateNodes(String data) throws Exception{
		mainContent = Node.parse(data);
	}
	
	public Node getContent() {
		return mainContent;
	}

	public String getValue(String[] keys, String attr) {
		Node curr = mainContent;
		for(String k:keys){
			curr = curr.getChild(k);
			if(curr == null){
				return null;
			}
		}
		return curr.getAttribute(attr).getValue();
	}

	
	public void close() throws Exception{
		PrintWriter out = new PrintWriter(fileName);
		for(Node n:mainContent.getChilds()){
			out.print(n.toParse(0));
		}
		out.close();
	}
	
	
}
