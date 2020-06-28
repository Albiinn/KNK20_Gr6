package app;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import utils.BCrypt;
import utils.DBConnection;
import utils.Language;
import utils.Session;

public class Login extends GridPane {
	
	private Label email = new Label();
	private Label password;
	private Label resultLabel1;
	private Label resultLabel2;
	
	public static TextField emailTxt = new TextField();
	public static PasswordField passwordTxt = new PasswordField();
	public static Label resultLabel = new Label();
	public static Scene gameScene;


	public GridPane getLogIn() {
		email = Language.getLabel("label1");
		resultLabel1 = Language.getLabel("TextField1");
		resultLabel2 = Language.getLabel("resultlabel1");
		password = Language.getLabel("label2");
		GridPane pane = new GridPane();
		pane.setAlignment(Pos.CENTER);
		pane.setPadding(new Insets(20, 20, 20, 20));
		pane.setHgap(10);
		pane.setVgap(10);
		pane.setStyle("-fx-background-image: url(\"https://i.imgur.com/OGQJDqs.png\");");

		Button backBtn = new Button("Back");
		backBtn.setOnAction(e -> {
			cleanForm();
			//Specify the scene to be used on this stage
			(Main.window).setScene(Main.indexScene);
		});
		Text formName = Language.getText("text1");
		formName.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));

		HBox title = new HBox(120);
		title.getChildren().addAll(formName, backBtn);

		pane.add(title, 0, 0, 2, 1);
		pane.add(email, 0, 1);
		pane.add(emailTxt, 1, 1);
		pane.add(password, 0, 2);
		pane.add(passwordTxt, 1, 2);
		pane.add(resultLabel,0,4,2,1);

		Button logInBtn = Language.getButton("Button3");
		logInBtn.setOnAction(e -> logInAction());
		pane.setOnKeyPressed(e -> {if(e.getCode() == KeyCode.ENTER) 
									logInAction();});
		pane.add(logInBtn, 1, 3);
		GridPane.setHalignment(logInBtn, HPos.RIGHT);

		final Text actionTarget = new Text();
		pane.add(actionTarget, 1, 4);

		return pane;
	}

	public void cleanForm() {
		emailTxt.setText("");
		emailTxt.setStyle("-fx-background:white;");
		passwordTxt.setText("");
		passwordTxt.setStyle("-fx-background:white;");
	}
	
	public void removeErrors() {
		emailTxt.setStyle("-fx-background:white;");
		passwordTxt.setStyle("-fx-background:white;");
		emailTxt.setPromptText("");
		passwordTxt.setPromptText("");
	}
	public void logInAction()
	{
		Validator.emailValidate(Login.emailTxt, "Invalid Email Address!");
		logIn();
		cleanForm();
	}
	private void logIn() 
	{
        //try catch statement with SQLException handling
		try 
		{
			String query = "SELECT * FROM users WHERE email = ?";
			
			PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
			preparedStatement.setString(1,  emailTxt.getText().toLowerCase());

			ResultSet resultSet = preparedStatement.executeQuery();			
			if(resultSet.next()) 
			{
				if (BCrypt.checkpw(passwordTxt.getText(), resultSet.getString("password"))) {
					Session.setSession(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
					Game game = new Game();
					gameScene = new Scene(game.getGameView(), 400, 400);
					(Main.window).setScene(gameScene);
					(Main.window).setResizable(false);
					(Main.window).setTitle("Game");
				}
				else {
					resultLabel.setStyle("-fx-text-fill:red");
					resultLabel.setText(resultLabel2.getText());
				}
			} 
			else 
			{
				resultLabel.setStyle("-fx-text-fill:red");
				resultLabel.setText(resultLabel2.getText());
			}
		} 
		catch (SQLException ex) 
		{
            //Prints this throwable and its backtrace to the standard error stream
			ex.printStackTrace();
		}
	}
}