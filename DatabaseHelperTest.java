import org.junit.jupiter.api.*;
import java.sql.*;
import application.*;
import databasePart1.DatabaseHelper;

import static org.junit.jupiter.api.Assertions.*;


/**
 * This class contains unit tests for the DatabaseHelper class, making sure that database operations like posting questions, 
 * posting answers, listing questions, marking answers as solutions, and deleting questions are functioning correctly.
 */
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private Connection connection;
    
    /**
     * Sets up the test environment by connecting to the database.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @BeforeEach
    
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        connection = DriverManager.getConnection("jdbc:h2:~/HW2Database", "sa", "");
    }

    /**
     * Cleans up after each test by closing the database connection.
     * 
     * @throws SQLException if a database access error occurs.
     */
    @AfterEach
    
    public void tearDown() throws SQLException {
        dbHelper.closeConnection();
        Statement stmt = connection.createStatement();
        stmt.execute("DROP ALL OBJECTS;");
        stmt.close();
    }

    /**
     * Tests the postQuestion method by adding a question to the database.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
    @Test
    
    public void testPostQuestion() throws SQLException {
        Question question = new Question("Test Title", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
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

    /**
     * Tests the postAnswer method by adding an answer to an existing question.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
    @Test
    public void testPostAnswer() throws SQLException {
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }
        Answer answer = new Answer("Test Answer", "AnswerUser", questionId);
        dbHelper.postAnswer(answer);
        String answerQuery = "SELECT * FROM answers WHERE underQuestion = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(answerQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Test Answer", rs.getString("text"));
            assertEquals("AnswerUser", rs.getString("postedBy"));
        }
    }

    /**
     * Tests the listAllQuestions method by verifying that it correctly retrieves all posted questions.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
    @Test
    public void testListAllQuestions() throws SQLException {
        Question question1 = new Question("Title1", "Body1", "User1");
        Question question2 = new Question("Title2", "Body2", "User2");
        dbHelper.postQuestion(question1);
        dbHelper.postQuestion(question2);
        QuestionList questionList = dbHelper.listAllQuestions();
        assertNotNull(questionList);
        assertEquals(2, questionList.getQuestions().size());
    }

    /**
     * Tests the markAnswerAsSolution method by marking an answer as the solution.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
    @Test
    public void testMarkAnswerAsSolution() throws SQLException {
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }
        Answer answer = new Answer("Test Answer", "AnswerUser", questionId);
        dbHelper.postAnswer(answer);
        String answerQuery = "SELECT id FROM answers WHERE underQuestion = ?";
        int answerId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(answerQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                answerId = rs.getInt("id");
            }
        }
        dbHelper.markAnswerAsSolution(answerId);
        String solutionQuery = "SELECT isSolution FROM answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(solutionQuery)) {
            pstmt.setInt(1, answerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                assertEquals(1, rs.getInt("isSolution"));
            }
        }
    }

    /**
     * Tests the deleteQuestion method by deleting a question from the database.
     * 
     * @throws SQLException if an error occurs during the database interaction.
     */
    @Test
    public void testDeleteQuestion() throws SQLException {
        Question question = new Question("Test Question", "Test Body", "TestUser");
        dbHelper.postQuestion(question);
        String query = "SELECT id FROM questions WHERE title = ?";
        int questionId = -1;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "Test Question");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                questionId = rs.getInt("id");
            }
        }
        dbHelper.deleteQuestion(questionId);
        String deleteQuery = "SELECT * FROM questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            assertTrue(rs.next(), "The question should be deleted.");
        }
    }
}
