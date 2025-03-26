import org.junit.jupiter.api.*;
import java.sql.*;
import application.*;
import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private Connection connection;

    @BeforeEach
    // connect to database
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        connection = DriverManager.getConnection("jdbc:h2:~/HW2Database", "sa", "");
    }

    @AfterEach
    // disconnect from database
    public void tearDown() throws SQLException {
        dbHelper.closeConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("DROP ALL OBJECTS;");
        stmt.close();
    }

    @Test
    public void testPostQuestion() throws SQLException {
    	//create a new question
        Question question = new Question("Test Title", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        // see if the question is posted
        String query = "SELECT * FROM questions WHERE title = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Title");
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Test Title", rs.getString("title"));
            assertEquals("Test Body", rs.getString("text"));
            assertEquals("TestUser", rs.getString("postedBy"));
        }
    }

    @Test
    public void testPostAnswer() throws SQLException {
    	// create a new question
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        // get the id of the new question
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }
        // create a new answer
        Answer answer = new Answer("Test Answer", "AnswerUser", questionId);
        dbHelper.postAnswer(answer);
        // see if the answer is posted
        String answerQuery = "SELECT * FROM answers WHERE underQuestion = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(answerQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Test Answer", rs.getString("text"));
            assertEquals("AnswerUser", rs.getString("postedBy"));
        }
    }

    @Test
    public void testListAllQuestions() throws SQLException {
    	// create 2 new question
        Question question1 = new Question("Title1", "Body1", "User1");
        Question question2 = new Question("Title2", "Body2", "User2");
        dbHelper.postQuestion(question1);
        dbHelper.postQuestion(question2);
        // test to see if the listAllQuestion() work as intended
        QuestionList questionList = dbHelper.listAllQuestions();
        assertNotNull(questionList);
        assertEquals(2, questionList.getQuestions().size());
    }

    @Test
    public void testMarkAnswerAsSolution() throws SQLException {
    	// create a new question
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        // get id of the new question
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }
        // create a new answer
        Answer answer = new Answer("Test Answer", "AnswerUser", questionId);
        dbHelper.postAnswer(answer);
        // get the id of the new answer
        String answerQuery = "SELECT id FROM answers WHERE underQuestion = ?";
        int answerId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(answerQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                answerId = rs.getInt("id");
            }
        }
        // mark the answer as solution
        dbHelper.markAnswerAsSolution(answerId);
        // see if the answer is correctly marked as solution
        String solutionQuery = "SELECT isSolution FROM answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(solutionQuery)) {
            pstmt.setInt(1, answerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assertEquals(1, rs.getInt("isSolution"));
            }
        }
    }
    
    @Test
    public void testDeleteQuestion() throws SQLException {
        // create a new question
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);

        // get the id of the question
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }

        // delete question
        dbHelper.deleteQuestion(questionId);

        // check if the question is deleted
        String deleteQuery = "SELECT * FROM questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next(), "The question has been deleted.");
        }
    }

}
