package application;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SpeechRecognizer {
	
	// Necessary
	private LiveSpeechRecognizer recognizer;
	
	// Logger
	private Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * This String contains the Result that is coming back from SpeechRecognizer
	 */
	private String speechRecognitionResult;
	
	/**
	 * A simple property to bind the current SpeechRecognitionResult
	 */
	private StringProperty speechRecognitionResultProperty = new SimpleStringProperty("");
	
	//-----------------Lock Variables-----------------------------
	
	/**
	 * This variable is used to ignore the results of speech recognition cause actually it can't be stopped...
	 * 
	 * <br>
	 * Check this link for more information: <a href=
	 * "https://sourceforge.net/p/cmusphinx/discussion/sphinx4/thread/3875fc39/">https://sourceforge.net/p/cmusphinx/discussion/sphinx4/thread/3875fc39/</a>
	 */
	private SimpleBooleanProperty ignoreSpeechRecognitionResults = new SimpleBooleanProperty(false);
	
	/**
	 * Checks if the speech recognise is already running
	 */
	private SimpleBooleanProperty speechRecognizerThreadRunning = new SimpleBooleanProperty(false);
	
	/**
	 * Checks if the resources Thread is already running
	 */
	private boolean resourcesThreadRunning;
	
	//---
	
	/**
	 * This executor service is used in order the playerState events to be executed in an order
	 */
	private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2);
	
	
	/**
	 * Mapping Nepali Unicode
	 */
	
	static Map<String, String> maatraMap = new HashMap<String, String>();
	static Map<String, String> phoneticMap = new HashMap<String, String>();
    

    static {
    	maatraMap.put("aa","\u093E");
    	maatraMap.put("i","\u093F");
    	maatraMap.put("ii","\u0940");
    	//maatraMap.put("u","\u0941");
    	maatraMap.put("uu","\u0942");
    	maatraMap.put("e","\u0947");
    	maatraMap.put("ai","\u0948");
    	maatraMap.put("o","\u094B");
    	maatraMap.put("au","\u094C");
    	
    	phoneticMap.put("A","\u0905");
        phoneticMap.put("AA","\u0906");
        phoneticMap.put("I","\u0907");
        phoneticMap.put("II","\u0908");
        phoneticMap.put("u","\u0909");
        phoneticMap.put("UU","\u090A");
        phoneticMap.put("E","\u090F");
        phoneticMap.put("AI","\u0910");
        phoneticMap.put("O","\u0913");
        phoneticMap.put("AU","\u0914");
        
        phoneticMap.put("k","\u0915");
        phoneticMap.put("kh","\u0916");
        phoneticMap.put("g","\u0917");
        phoneticMap.put("gh","\u0918");
        phoneticMap.put("nh","\u0919");
        phoneticMap.put("ch","\u091A");
        phoneticMap.put("chh","\u091B");
        phoneticMap.put("j","\u091C");
        phoneticMap.put("jh","\u091D");
        phoneticMap.put("yh","\u091E");
        phoneticMap.put("T","\u091F");
        phoneticMap.put("Th","\u0920");
        phoneticMap.put("D","\u0921");
        phoneticMap.put("Dh","\u0922");
        phoneticMap.put("N","\u0923");
        phoneticMap.put("t","\u0924");
        phoneticMap.put("th","\u0925");
        phoneticMap.put("d","\u0926");
        phoneticMap.put("dh","\u0927");
        phoneticMap.put("n","\u0928");
        phoneticMap.put("p","\u092A");
        phoneticMap.put("ph","\u092B");
        phoneticMap.put("b","\u092C");
        phoneticMap.put("bh","\u092D");
        phoneticMap.put("m","\u092E");
        phoneticMap.put("y","\u092F");
        phoneticMap.put("r","\u0930");
        phoneticMap.put("l","\u0932");
        phoneticMap.put("v","\u0935");
        phoneticMap.put("sh","\u0936");
        phoneticMap.put("shy","\u0937");
        phoneticMap.put("s","\u0938");
        phoneticMap.put("h","\u0939");
        
        
        phoneticMap.put("ka","\u0915");
        phoneticMap.put("kha","\u0916");
        phoneticMap.put("ga","\u0917");
        phoneticMap.put("gha","\u0918");
        phoneticMap.put("nha","\u0919");
        phoneticMap.put("cha","\u091A");
        phoneticMap.put("chha","\u091B");
        phoneticMap.put("ja","\u091C");
        phoneticMap.put("jha","\u091D");
        phoneticMap.put("yha","\u091E");
        phoneticMap.put("Ta","\u091F");
        phoneticMap.put("Tha","\u0920");
        phoneticMap.put("Da","\u0921");
        phoneticMap.put("Dha","\u0922");
        phoneticMap.put("Na","\u0923");
        phoneticMap.put("ta","\u0924");
        phoneticMap.put("tha","\u0925");
        phoneticMap.put("da","\u0926");
        phoneticMap.put("dha","\u0927");
        phoneticMap.put("na","\u0928");
        phoneticMap.put("pa","\u092A");
        phoneticMap.put("pha","\u092B");
        phoneticMap.put("ba","\u092C");
        phoneticMap.put("bha","\u092D");
        phoneticMap.put("ma","\u092E");
        phoneticMap.put("ya","\u092F");
        phoneticMap.put("ra","\u0930");
        phoneticMap.put("la","\u0932");
        phoneticMap.put("va","\u0935");
        phoneticMap.put("sha","\u0936");
        phoneticMap.put("shya","\u0937");
        phoneticMap.put("sa","\u0938");
        phoneticMap.put("ha","\u0939");
        
    }
	
	//------------------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public SpeechRecognizer() {
		
		// Loading Message
		logger.log(Level.INFO, "Loading Speech Recognizer...\n");
		
		// Configuration
		Configuration configuration = new Configuration();
		
		// Load model from the jar
		configuration.setAcousticModelPath("resource:/np_acuostic");
		configuration.setDictionaryPath("resource:/np_dic/np.dic");
		
		//====================================================================================
		//=====================READ THIS!!!===============================================
		//Uncomment this line of code if you want the recognizer to recognize every word of the language 
		//you are using , here it is English for example	
		//====================================================================================
		//configuration.setLanguageModelPath("resource:/np_dic/np.lm");
		
		//====================================================================================
		//=====================READ THIS!!!===============================================
		//If you don't want to use a grammar file comment below 3 lines and uncomment the above line for language model	
		//====================================================================================
		
		// Grammar
		configuration.setGrammarPath("resource:/grammars");
		configuration.setGrammarName("grammar");
		configuration.setUseGrammar(true);
		
		try {
			recognizer = new LiveSpeechRecognizer(configuration);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		
		// Start recognition process pruning previously cached data.
		// recognizer.startRecognition(true);
		
		//Check if resources available
		startResourcesThread();
	}
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Starts the Speech Recognition Thread
	 */
	public synchronized void startSpeechRecognition() {
		
		//Check lock
		if (speechRecognizerThreadRunning.get())
			logger.log(Level.INFO, "Speech Recognition Thread already running...\n");
		else
			//Submit to ExecutorService
			eventsExecutorService.submit(() -> {
				
				//locks
				Platform.runLater(() -> {
					speechRecognizerThreadRunning.set(true);
					ignoreSpeechRecognitionResults.set(false);
				});
				
				//Start Recognition
				recognizer.startRecognition(true);
				
				//Information			
				logger.log(Level.INFO, "You can start to speak...\n");
				
				try {
					while (speechRecognizerThreadRunning.get()) {
						/*
						 * This method will return when the end of speech is reached. Note that the end pointer will determine the end of speech.
						 */
						SpeechResult speechResult = recognizer.getResult();
						
						//Check if we ignore the speech recognition results
						if (!ignoreSpeechRecognitionResults.get()) {
							
							//Check the result
							if (speechResult == null)
								logger.log(Level.INFO, "I can't understand what you said.\n");
							else {
								
								//Get the hypothesis
								speechRecognitionResult = speechResult.getHypothesis();
								
								speechRecognitionResult = speechRecognitionResult.replaceAll("aa",maatraMap.get("aa") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("ii",maatraMap.get("ii") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("i",maatraMap.get("i") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("uu",maatraMap.get("uu") );
								//speechRecognitionResult = speechRecognitionResult.replaceAll("u",maatraMap.get("u") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("e",maatraMap.get("e") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("ai",maatraMap.get("ai") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("o",maatraMap.get("o") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("au",maatraMap.get("au") );
								
								speechRecognitionResult = speechRecognitionResult.replaceAll("AA",phoneticMap.get("AA") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("A",phoneticMap.get("A") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("II",phoneticMap.get("II") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("I",phoneticMap.get("I") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("UU",phoneticMap.get("UU") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("u",phoneticMap.get("u") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("E",phoneticMap.get("E") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("AI",phoneticMap.get("AI") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("O",phoneticMap.get("O") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("AU",phoneticMap.get("AU") );
						        
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ka",phoneticMap.get("ka") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("kha",phoneticMap.get("kha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ga",phoneticMap.get("ga") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("gha",phoneticMap.get("gha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("nha",phoneticMap.get("nha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("cha",phoneticMap.get("cha") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("chha",phoneticMap.get("chha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ja",phoneticMap.get("ja") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("jha",phoneticMap.get("jha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("yha",phoneticMap.get("yha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("Ta",phoneticMap.get("Ta") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("Tha",phoneticMap.get("Tha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("Da",phoneticMap.get("Da") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("Dha",phoneticMap.get("Dha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("Na",phoneticMap.get("Na") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ta",phoneticMap.get("ta") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("tha",phoneticMap.get("tha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("da",phoneticMap.get("da") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("dha",phoneticMap.get("dha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("na",phoneticMap.get("na") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("pa",phoneticMap.get("pa") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("pha",phoneticMap.get("pha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ba",phoneticMap.get("ba") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("bha",phoneticMap.get("bha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ma",phoneticMap.get("ma") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ya",phoneticMap.get("ya") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("ra",phoneticMap.get("ra") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("la",phoneticMap.get("la") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("va",phoneticMap.get("va") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("sha",phoneticMap.get("sha") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("shya",phoneticMap.get("shya") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("sa",phoneticMap.get("sa") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ha",phoneticMap.get("ha") );
						        
						        speechRecognitionResult = speechRecognitionResult.replaceAll("k",phoneticMap.get("k") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("kh",phoneticMap.get("kh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("g",phoneticMap.get("g") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("gh",phoneticMap.get("gh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("nh",phoneticMap.get("nh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("ch",phoneticMap.get("ch") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("chh",phoneticMap.get("chh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("j",phoneticMap.get("j") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("jh",phoneticMap.get("jh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("yh",phoneticMap.get("yh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("T",phoneticMap.get("T") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("Th",phoneticMap.get("Th") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("D",phoneticMap.get("D") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("Dh",phoneticMap.get("Dh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("N",phoneticMap.get("N") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("t",phoneticMap.get("t") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("th",phoneticMap.get("th") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("d",phoneticMap.get("d") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("dh",phoneticMap.get("dh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("n",phoneticMap.get("n") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("p",phoneticMap.get("p") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("ph",phoneticMap.get("ph") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("b",phoneticMap.get("b") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("bh",phoneticMap.get("bh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("m",phoneticMap.get("m") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("y",phoneticMap.get("y") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("r",phoneticMap.get("r") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("l",phoneticMap.get("l") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("v",phoneticMap.get("v") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("sh",phoneticMap.get("sh") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("shy",phoneticMap.get("shy") );
								speechRecognitionResult = speechRecognitionResult.replaceAll("s",phoneticMap.get("s") );
						        speechRecognitionResult = speechRecognitionResult.replaceAll("h",phoneticMap.get("h") );
						        
						        
								
								//You said?
								System.out.println("You said: [" + speechRecognitionResult + "]\n");
								
								Platform.runLater(() -> speechRecognitionResultProperty.set(speechRecognitionResult));
								
								//Call the appropriate method 
								makeDecision(speechRecognitionResult, speechResult.getWords());
								
							}
						} else
							logger.log(Level.INFO, "Ingoring Speech Recognition Results...");
						
					}
				} catch (Exception ex) {
					logger.log(Level.WARNING, null, ex);
					Platform.runLater(() -> speechRecognizerThreadRunning.set(false));
				}
				
				logger.log(Level.INFO, "SpeechThread has exited...");
				
			});
	}
	
	/**
	 * Stops ignoring the results of SpeechRecognition
	 */
	public synchronized void stopIgnoreSpeechRecognitionResults() {
		
		//Stop ignoring speech recognition results
		Platform.runLater(() -> ignoreSpeechRecognitionResults.set(false));
	}
	
	/**
	 * Ignores the results of SpeechRecognition
	 */
	public synchronized void ignoreSpeechRecognitionResults() {
		
		//Instead of stopping the speech recognition we are ignoring it's results
		Platform.runLater(() -> ignoreSpeechRecognitionResults.set(true));
		
	}
	
	//-----------------------------------------------------------------------------------------------
	
	/**
	 * Starting a Thread that checks if the resources needed to the SpeechRecognition library are available
	 */
	public void startResourcesThread() {
		
		//Check lock
		if (resourcesThreadRunning)
			logger.log(Level.INFO, "Resources Thread already running...\n");
		else
			//Submit to ExecutorService
			eventsExecutorService.submit(() -> {
				try {
					
					//Lock
					resourcesThreadRunning = true;
					
					// Detect if the microphone is available
					while (true) {
						
						//Is the Microphone Available
						if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE))
							logger.log(Level.INFO, "Microphone is not available.\n");
						
						// Sleep some period
						Thread.sleep(350);
					}
					
				} catch (InterruptedException ex) {
					logger.log(Level.WARNING, null, ex);
					resourcesThreadRunning = false;
				}
			});
	}
	
	/**
	 * Takes a decision based on the given result
	 * 
	 * @param speechWords
	 */
	public void makeDecision(String speech , List<WordResult> speechWords) {
		
		System.out.println(speech);
		
	}
	
	public SimpleBooleanProperty ignoreSpeechRecognitionResultsProperty() {
		return ignoreSpeechRecognitionResults;
	}
	
	public SimpleBooleanProperty speechRecognizerThreadRunningProperty() {
		return speechRecognizerThreadRunning;
	}
	
	/**
	 * @return the speechRecognitionResultProperty
	 */
	public StringProperty getSpeechRecognitionResultProperty() {
		return speechRecognitionResultProperty;
	}
	
}
