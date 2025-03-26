package application;

import databasePart1.DatabaseHelper;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * The Menu.java file implements all the necessary menus for the full
 * console application, allowing users to perform a variety of operations on data.
 */

public class Menu {
	
    private final DatabaseHelper databaseHelper;
    private String currentUser;
    
    public Menu(DatabaseHelper databaseHelper, String currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

	public void mainMenu(Scanner scnr) {
		System.out.println("Welcome to the HW2 question and answer system. Select an option to proceed.");
		System.out.println("[1] Post new question");
		System.out.println("[2] View all questions");
		System.out.println("[3] Manage my questions");
		System.out.println("[4] Manage my answers");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			postNewQuestion(scnr);
		} else if (input == 2) {
			viewAllQuestions(scnr);
		} else if (input == 3) {
			manageQuestions(scnr);
		} else if (input == 4) {
			manageAnswers(scnr);
		} else {
			System.out.println("Invalid selection. Please try again.");
			mainMenu(scnr);
		}
	}
    
	public void postNewQuestion(Scanner scnr) {
		System.out.println("Title: ");
		String title = scnr.nextLine();
		System.out.println("Body: ");
		String text = scnr.nextLine();
		Question q = new Question(title, text, currentUser);
		try {
			databaseHelper.postQuestion(q);
			System.out.println("Question successfully posted!");
		} catch (SQLException e) {
			System.out.println("Question failed to post. Please try again.");
			e.printStackTrace();
		}
		mainMenu(scnr);
	}
    
	public void viewAllQuestions(Scanner scnr) {
		QuestionList allQuestions = databaseHelper.listAllQuestions();
		allQuestions.print();
		System.out.println("[1] Post an answer to a question");
		System.out.println("[2] View the answers to a question");
		System.out.println("[3] Search for questions");
		System.out.println("[4] Return to the main menu");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			postNewAnswer(scnr);
		} else if (input == 2) {
			viewAnswersToQuestion(scnr);
		} else if (input == 3) {
			searchQuestions(scnr, allQuestions);
		} else if (input == 4) {
			mainMenu(scnr);
		}
	}
	
	public void postNewAnswer(Scanner scnr) {
		System.out.println("Enter the # corresponding to the question you want to answer: ");
		int qid = scnr.nextInt();
		scnr.nextLine();
		System.out.println("Enter answer text: ");
		String text = scnr.nextLine();
		Answer a = new Answer(text, currentUser, qid);
		try {
			databaseHelper.postAnswer(a);
			System.out.println("Answer successfully posted!");
		} catch (SQLException e) {
			System.out.println("Answer failed to post. Please try again.");
			e.printStackTrace();
		}
		mainMenu(scnr);
	}
	
	public void viewAnswersToQuestion(Scanner scnr) {
		System.out.println("Enter the # corresponding to the question you want to see the answers to: ");
		int qid = scnr.nextInt();
		scnr.nextLine();
		AnswerList answersToQuestion = databaseHelper.viewAnswersToQuestion(qid);
		System.out.println("Here are all the answers to question " + qid + ".\nSome of these answers may not be correct. Look out for the answer(s) marked \"(Solution)\".");
		System.out.println("------------------------------------------");
		answersToQuestion.print();
		mainMenu(scnr);
	}
	
	public void manageQuestions(Scanner scnr) {
		System.out.println("[1] Edit a question");
		System.out.println("[2] Mark a question as resolved");
		System.out.println("[3] Delete a question");
		System.out.println("[4] Return to the main menu");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			updateQuestion(scnr);
		} else if (input == 2) {
			resolveQuestion(scnr);
		} else if (input == 3) {
			deleteQuestion(scnr);
		} else if (input == 4) {
			mainMenu(scnr);
		}
	}
	
	public void manageAnswers(Scanner scnr) {
		System.out.println("[1] Update an answer");
		System.out.println("[2] Delete an answer");
		System.out.println("[3] Return to the main menu");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			updateAnswer(scnr);
		} else if (input == 2) {
			deleteAnswer(scnr);
		} else if (input == 3) {
			mainMenu(scnr);
		}
	}
	
	public void updateAnswer(Scanner scnr) {
		AnswerList answers = databaseHelper.showAnswersToManage(currentUser);
		answers.print();
		System.out.println("Enter the # corresponding to the answer you want to update: ");
		int pseud_id = scnr.nextInt();
		scnr.nextLine();
		System.out.println("Enter updated answer text: ");
		String text = scnr.nextLine();
		databaseHelper.updateAnswer(answers.getIDs().get(pseud_id), text);
		System.out.println("Answer successfully updated.");
		mainMenu(scnr);
	}
	
	public void deleteAnswer(Scanner scnr) {
		AnswerList answers = databaseHelper.showAnswersToManage(currentUser);
		answers.print();
		System.out.println("Enter the # corresponding to the answer you want to delete: ");
		int pseud_id = scnr.nextInt();
		scnr.nextLine();
		System.out.println("Are you sure?");
		System.out.println("[1] Yes");
		System.out.println("[2] No");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			databaseHelper.deleteAnswer(answers.getIDs().get(pseud_id));
			System.out.println("Answer successfully deleted.");
		} else if (input == 2) {
			System.out.println("Your answer has not been deleted.");
		}
		mainMenu(scnr);
	}
	
	public void searchQuestions(Scanner scnr, QuestionList ql) {
		System.out.println("Enter search term: ");
		String filter = scnr.nextLine();
		ql.search(filter);
		System.out.println("Below are the results of your search.");
		System.out.println("------------------------------------------");
		ql.print();
		mainMenu(scnr);
	}
	
	public void updateQuestion(Scanner scnr) {
		QuestionList questions = databaseHelper.showQuestionsToManage(currentUser);
		questions.print();
		System.out.println("Enter the # corresponding to the question you want to edit: ");
		int pseud_id = scnr.nextInt();
		scnr.nextLine();
		System.out.println("Enter updated question title: ");
		String title = scnr.nextLine();
		System.out.println("Enter updated question text: ");
		String text = scnr.nextLine();
		databaseHelper.updateQuestion(questions.getIDs().get(pseud_id), title, text);
		System.out.println("Question successfully edited.");
		mainMenu(scnr);
	}
	
	public void resolveQuestion(Scanner scnr) {
		QuestionList questions = databaseHelper.showQuestionsToManage(currentUser);
		questions.print();
		System.out.println("Enter the # corresponding to the question you want to mark as resolved: ");
		int pseud_id = scnr.nextInt();
		scnr.nextLine();
		AnswerList answers = databaseHelper.showAnswersToManage(questions.getIDs().get(pseud_id));
		answers.print();
		System.out.println("Enter the # corresponding to the answer you want to mark as solution: ");
		int answer_pseud_id = scnr.nextInt();
		scnr.nextLine();
		databaseHelper.markAnswerAsSolution(answers.getIDs().get(answer_pseud_id));
		databaseHelper.markQuestionAsResolved(questions.getIDs().get(pseud_id));
		System.out.println("Answer successfully marked as solution. Your question is now marked as resolved.");
		mainMenu(scnr);
	}
	
	public void deleteQuestion(Scanner scnr) {
		QuestionList questions = databaseHelper.showQuestionsToManage(currentUser);
		questions.print();
		System.out.println("Enter the # corresponding to the question you want to delete: ");
		int pseud_id = scnr.nextInt();
		scnr.nextLine();
		System.out.println("Are you sure?");
		System.out.println("[1] Yes");
		System.out.println("[2] No");
		System.out.println("Enter the # corresponding to your choice: ");
		int input = scnr.nextInt();
		scnr.nextLine();
		if (input == 1) {
			databaseHelper.deleteQuestion(questions.getIDs().get(pseud_id));
			System.out.println("Question successfully deleted.");
		} else if (input == 2) {
			System.out.println("Your question has not been deleted.");
		}
		mainMenu(scnr);
	}
}
