package com.example.harsha.gatorstally;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ai.api.AIConfiguration;
import ai.api.AIListener;
import ai.api.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import com.google.gson.JsonElement;
import java.util.Map;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import java.util.Locale;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements AIListener, TextToSpeech.OnInitListener
{
	private Button listenButton;
	private TextView resultTextView;
	private AIService aiService;
	private TextToSpeech tts;
	private boolean listening;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenButton = (Button) findViewById(R.id.listenButton);
		resultTextView = (TextView) findViewById(R.id.resultTextView);
		final AIConfiguration config = new AIConfiguration("4b325ccec5ea48d9b776084a6e5eab5a",
        AIConfiguration.SupportedLanguages.English,
        AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
		aiService.setListener(this);

		tts = new TextToSpeech(this, this);

	}
	public void listenButtonOnClick(final View view)
	{
		if(listening)
		{
			aiService.cancel();
		}
		else
		{
			if(tts.isSpeaking())
			{
				tts.stop();
			}
			resultTextView.setText("Listening.......");
			aiService.startListening();
		}
	}
	
	public void onResult(final AIResponse response) 
	{
   		Result result = response.getResult();

    	// Get parameters
    	String parameterString = "";
    	if (result.getParameters() != null && !result.getParameters().isEmpty()) 
    	{
        	for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) 
        	{
            	parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
        	}
    	}
		// Show results in TextView.
    	resultTextView.setText("\n" + response.getResult().getFulfillment().getSpeech().toString());
		speakOut(response.getResult().getFulfillment().getSpeech().toString());



	}


	@Override
	public void onInit(int status)
	{
		if(status == TextToSpeech.SUCCESS)
		{
			int result = tts.setLanguage(Locale.US);
			if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
			{
				Log.e("TTS", "This language is not supported");
			}
			else
			{

			}

		}
		else
		{
			Log.e("TTS", "Initialization failed");
		}
	}



	@Override
	public void onDestroy()
	{
		if(tts!= null)
		{
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	private void speakOut(String text)
	{
		tts.speak(text,TextToSpeech.QUEUE_FLUSH, null);
	}
	
	@Override
	public void onError(final AIError error) 
	{
    	resultTextView.setText(error.toString());
	}
	@Override
	public void onListeningStarted()
	{

		listening = true;
	}

	@Override
	public void onListeningCanceled()
	{
		resultTextView.setText("Click the button to speak");
		listening = false;
	}

	@Override
	public void onListeningFinished()
	{
		listening = false;
	}

	@Override
	public void onAudioLevel(final float level) {}
}
