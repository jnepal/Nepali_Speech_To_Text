package application;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

/**
 * The main interface of the application
 * 
 * @author GOXR3PLUS
 *
 */
public class MainInterfaceController extends BorderPane {
	
	@FXML
	private Button start;
	
	@FXML
	private Button pause;
	
	@FXML
	private Button resume;
	
	@FXML
	private Label statusLabel;
	
	@FXML
	private TextArea infoArea;
	
	@FXML
	private TextArea desc;
	
	@FXML
	private ImageView img1;
	
	// -----------------------------------------
	
	private SpeechRecognizer speechRecognition = new SpeechRecognizer();
	
	/**
	 * Constructor
	 */
	public MainInterfaceController() {
		
		// FXMLLoader
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainInterfaceController.fxml"));
		loader.setController(this);
		loader.setRoot(this);
		
		try {
			loader.load();
		} catch (IOException ex) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE, " FXML can't be loaded!", ex);
		}
		
	}
	
	/**
	 * Called as soon as .fxml is initialised
	 */
	@FXML
	private void initialize() {
		//Image image = new Image("stt1.png");
		//img1.setImage(image);
		
		// start
		start.disableProperty().bind(speechRecognition.speechRecognizerThreadRunningProperty());
		start.setOnAction(a -> {
			statusLabel.setText("Status : [Running]");
			//infoArea.appendText("Starting Speech Recognizer\n");
			speechRecognition.startSpeechRecognition();
		});
		
		// stop
		pause.disableProperty().bind(speechRecognition.ignoreSpeechRecognitionResultsProperty().or(start.disabledProperty().not()));
		pause.setOnAction(a -> {
			statusLabel.setText("Status : [Paused]");
			//infoArea.appendText("Pausing Speech Recognizer\n");
			speechRecognition.ignoreSpeechRecognitionResults();
		});
		
		// restart
		resume.disableProperty().bind(speechRecognition.ignoreSpeechRecognitionResultsProperty().not());
		resume.setOnAction(a -> {
			statusLabel.setText("Status : [Running]");
			//infoArea.appendText("Resuming Speech Recognizer\n");
			speechRecognition.stopIgnoreSpeechRecognitionResults();
		});
		
		
       
		
		//Bind the SpeechRecognitionText to InfoArea
		infoArea.setWrapText(true);
		infoArea.setFont(Font.font(20));
		infoArea.textProperty().bind(Bindings.createStringBinding(() -> infoArea.getText() + "  " + speechRecognition.getSpeechRecognitionResultProperty().get(),
				speechRecognition.getSpeechRecognitionResultProperty()));
		
	
		String dec = "\u092E \u0924\u0932\u0915\u093E \u0936\u092C\u0926 \u0926\u0947\u0916\u093E\u0909\u0928 \u0938\u0915\u091B\u0941  \n \"\u092C\u093E\u092C\u093E \u0917\u0930\u092D \u0917\u0930\u092E\u0940 \u0928\u093E\u092E \u0915\u0932\u092E \u0916\u093E\u0928\u093E \u0930\u093E\u091C\u093E \u0928\u093E\u0930\u0940\" .....\n";
			    desc.setText(dec);
			    desc.setFont(Font.font ("preeti", 15));
	    
		
	}
}
