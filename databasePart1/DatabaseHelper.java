package databasePart1;
import java.sql.*;

import application.*;

import java.util.ArrayList;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database
 * and facilitates CRUD operations on questions and answers.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/HW2Database";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
	    // Create questions table
		String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "title VARCHAR(255), "
				+ "text VARCHAR(500), "
				+ "postedBy VARCHAR(255), "
				+ "resolved INT)";
		statement.execute(questionsTable);
		
	    // Create answers table
		String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "text VARCHAR(500), "
				+ "postedBy VARCHAR(255), "
				+ "underQuestion INT, "
				+ "isSolution INT)";
		statement.execute(answersTable);
	}

	// Create a new question
	public void postQuestion(Question question) throws SQLException {
		String insertQuestion = "INSERT INTO questions (title, text, postedBy, resolved) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(1, question.getTitle());
			pstmt.setString(2, question.getText());
			pstmt.setString(3, question.getPostedBy());
			pstmt.setInt(4, question.getResolved() ? 1 : 0);
			pstmt.executeUpdate();
		}
	}
	
	// Create a new answer
	public void postAnswer(Answer answer) throws SQLException {
		String insertQuestion = "INSERT INTO answers (text, postedBy, underQuestion, isSolution) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(1, answer.getText());
			pstmt.setString(2, answer.getPostedBy());
			pstmt.setInt(3, answer.getUnderQuestion());
			pstmt.setInt(4, answer.getIsSolution() ? 1 : 0);
			pstmt.executeUpdate();
		}
	}
	
	// List all questions
	public QuestionList listAllQuestions() {
	    String query = "SELECT * FROM questions";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        
	        String data = "";
	        ArrayList<String> questions = new ArrayList<String>();
	        
	        while (rs.next()) {
	        	data += rs.getInt("id") + ". ";
	            data += rs.getString("title");
	            if (rs.getInt("resolved") == 1) {
	            	data += " (Resolved)" + "\n";
	            } else {
	            	data += " (Unresolved)" + "\n";
	            }
	            data += "Posted by: " + rs.getString("postedBy") + "\n";
	            data += rs.getString("text");
	            data += "\n------------------------------------------\n";
	            questions.add(data);
	            data = "";
	        }
	        QuestionList ql = new QuestionList(questions);
	        return ql;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// View the answers to a question
	public AnswerList viewAnswersToQuestion(int qid) {
		String query = "SELECT * FROM answers WHERE underQuestion = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setInt(1, qid);
	    	
	        ResultSet rs = pstmt.executeQuery();
	        
	        String data = "";
	        ArrayList<String> answers = new ArrayList<String>();
	        
	        while (rs.next()) {
	        	if (rs.getInt("isSolution") == 1) {
	        		data += "(Solution) ";
	        	}
	            data += rs.getString("text") + "\n";
	            data += "Posted by: " + rs.getString("postedBy");
	            data += "\n------------------------------------------\n";
	            answers.add(data);
	            data = "";
	        }
	        
	        AnswerList al = new AnswerList(answers);
	        return al;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Show the user's answers, which they are permitted to manage
	public AnswerList showAnswersToManage(String currentUser) {
		String query = "SELECT * FROM answers WHERE postedBy = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setString(1, currentUser);
	    	
	        ResultSet rs = pstmt.executeQuery();
	        
	        String data = "";
	        int index = 1;
	        
	        ArrayList<String> answers = new ArrayList<String>();
	        ArrayList<Integer> db_ids = new ArrayList<Integer>();
	        db_ids.add(null); // Fill index 0 of ArrayList with null
	        
	        while (rs.next()) {
	        	data += index + ". ";
	        	if (rs.getInt("isSolution") == 1) {
	        		data += "(Solution) ";
	        	}
	            data += rs.getString("text") + "\n";
	            data += "Posted by: " + rs.getString("postedBy");
	            data += "\n------------------------------------------\n";
	            answers.add(data);
	            db_ids.add(rs.getInt("id"));
	            index++;
	            data = "";
	        }
	        
	        AnswerList al = new AnswerList(answers, db_ids);
	        return al;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Show the answers under a user's question, which they are permitted to mark as solution
	public AnswerList showAnswersToManage(int qid) {
		String query = "SELECT * FROM answers WHERE underQuestion = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setInt(1, qid);
	    	
	        ResultSet rs = pstmt.executeQuery();
	        
	        String data = "";
	        int index = 1;
	        
	        ArrayList<String> answers = new ArrayList<String>();
	        ArrayList<Integer> db_ids = new ArrayList<Integer>();
	        db_ids.add(null); // Fill index 0 of ArrayList with null
	        
	        while (rs.next()) {
	        	data += index + ". ";
	        	if (rs.getInt("isSolution") == 1) {
	        		data += "(Solution) ";
	        	}
	            data += rs.getString("text") + "\n";
	            data += "Posted by: " + rs.getString("postedBy");
	            data += "\n------------------------------------------\n";
	            answers.add(data);
	            db_ids.add(rs.getInt("id"));
	            index++;
	            data = "";
	        }
	        
	        AnswerList al = new AnswerList(answers, db_ids);
	        return al;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Show the user's questions, which they are permitted to manage
	public QuestionList showQuestionsToManage(String currentUser) {
		String query = "SELECT * FROM questions WHERE postedBy = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	    	pstmt.setString(1, currentUser);
	    	
	        ResultSet rs = pstmt.executeQuery();
	        
	        String data = "";
	        int index = 1;
	        
	        ArrayList<String> questions = new ArrayList<String>();
	        ArrayList<Integer> db_ids = new ArrayList<Integer>();
	        db_ids.add(null); // Fill index 0 of ArrayList with null
	        
	        while (rs.next()) {
	        	data += index + ". ";
	            data += rs.getString("title");
	            if (rs.getInt("resolved") == 1) {
	            	data += " (Resolved)" + "\n";
	            } else {
	            	data += " (Unresolved)" + "\n";
	            }
	            data += "Posted by: " + rs.getString("postedBy") + "\n";
	            data += rs.getString("text");
	            data += "\n------------------------------------------\n";
	            questions.add(data);
	            db_ids.add(rs.getInt("id"));
	            index++;
	            data = "";
	        }
	        
	        QuestionList ql = new QuestionList(questions, db_ids);
	        return ql;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}

	// Update an answer
	public void updateAnswer(int aid, String text) {
		String query = "UPDATE answers SET text = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, text);
			pstmt.setInt(2, aid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Delete an answer
	public void deleteAnswer(int aid) {
		String query = "DELETE answers WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, aid);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Mark an answer as solution
	public void markAnswerAsSolution(int aid) {
		String query = "UPDATE answers SET isSolution = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, 1);
			pstmt.setInt(2, aid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Update a question
	public void updateQuestion(int qid, String title, String text) {
		String query = "UPDATE questions SET title = ?, text = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, title);
			pstmt.setString(2, text);
			pstmt.setInt(3, qid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Mark a question as resolved
	public void markQuestionAsResolved(int qid) {
		String query = "UPDATE questions SET resolved = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, 1);
			pstmt.setInt(2, qid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Delete a question
	public void deleteQuestion(int qid) {
		String query = "UPDATE questions SET title = ?, text = ?, postedBy = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, "This question has been deleted.");
			pstmt.setString(2, "--");
			pstmt.setString(3, "--");
			pstmt.setInt(4, qid);
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}
