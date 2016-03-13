package com.example.jzhou.serendlpity;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Bel on 24.02.2016.
 */
public class Record extends Fragment {

    private MediaPlayer mediaPlayer;
    private MediaRecorder recorder;
    private String OUTPUT_FILE;
    public RelativeLayout sfLayout;
    public RecordButton sfRecordButton;
    public PlayRecordingButton sfPlayButton;
    public StopButton sfStopButton;

    File newRecordedFile;
    String newRecordedFileName;
    Calendar calendar;


    public TextView tvSeconds;
    public TextView tvMinutes;
    public TextView tvHours;

    int curTime;
    int curPosition;
    private static MessageHandler messageHandler;

    // PARAMETERS THAT SHOULD BE TAKEN FROM SETTINGS
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    //OUTPUT FORMAT SHOULD ALSO CHANGE THE FORMAT OF THE FILE
    private static final int OUTPUT_FORMAT = MediaRecorder.OutputFormat.THREE_GPP;
    private static final int AUDIO_ENCODER = MediaRecorder.AudioEncoder.AMR_NB;

    //Create buttons and layout
    public void createLayout() {

        //create button with changeable images
        sfRecordButton = new RecordButton(getContext());
        //set ID defined in ids.xml file
        //sfRecordButton.setId(R.id.RecordButton);
        //set picture background to transparent
        sfRecordButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ;
        // set layout parameters for button
        RelativeLayout.LayoutParams paramsRecord = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //add button BELOW + centralizing last button with name Stop Play audio
        paramsRecord.addRule(RelativeLayout.CENTER_IN_PARENT);
        paramsRecord.addRule(RelativeLayout.BELOW, R.id.Timer);
        //add button for Recording to the layout + parameters
        sfLayout.addView(sfRecordButton, paramsRecord);

        //create button with changeable images
        sfStopButton = new StopButton(getContext());
        //set ID defined in ids.xml file
        //sfStopButton.setId(R.id.StopButton);
        //set picture background to transparent
        sfStopButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ;
        // set layout parameters for button
        RelativeLayout.LayoutParams paramsStop = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //add button BELOW + centralizing last button with name Stop Play audio
        paramsStop.addRule(RelativeLayout.LEFT_OF, sfRecordButton.getId());
        paramsStop.addRule(RelativeLayout.BELOW, R.id.Timer);
        paramsStop.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //add button for Recording to the layout + parameters
        sfLayout.addView(sfStopButton, paramsStop);

        //create button with changeable images
        sfPlayButton = new PlayRecordingButton(getContext());
        //set picture background to transparent
        sfPlayButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        ;
        // set layout parameters for button
        RelativeLayout.LayoutParams paramsPlay = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //add button BELOW + centralizing last button with name Stop Play audio
        paramsPlay.addRule(RelativeLayout.RIGHT_OF, sfRecordButton.getId());
        paramsPlay.addRule(RelativeLayout.BELOW, R.id.Timer);
        paramsPlay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //add button for Recording to the layout + parameters
        sfLayout.addView(sfPlayButton, paramsPlay);
    }

    class RecordButton extends ImageButton {
        boolean sfStartRecording;

        public RecordButton(Context ctx) {
            super(ctx);
            setImageResource(R.mipmap.ic_rec);
            setOnClickListener(clicker);
            setSfStartRecording(true);
        }

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                //function to check recording
                if (getSfStartRecording()) {
                    setImageResource(R.mipmap.ic_rec_stop);
                    //function to Start recording
                    StartRecord();
                    sfPlayButton.disable();
                } else {
                    setImageResource(R.mipmap.ic_rec);
                    //function to Stop recording
                    StopRecord();
                    sfPlayButton.enable();
                    Toast toast = Toast.makeText(getContext(), "Recording successful. Uploaded to the server.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                }
                setSfStartRecording(!getSfStartRecording());
            }

        };

        public void setSfStartRecording(boolean sfStartRecording) {
            this.sfStartRecording = sfStartRecording;
        }

        public boolean getSfStartRecording() {
            return sfStartRecording;
        }

        public void enable() {
            setEnabled(true);
            setAlpha(1f);
        }

        public void disable() {
            setEnabled(false);
            setAlpha(0.5f);
        }


    }

    class PlayRecordingButton extends ImageButton {
        boolean sfPlayRecording = true;

        public PlayRecordingButton(Context ctx) {
            super(ctx);
            setImageResource(R.mipmap.ic_rec_play);
            setOnClickListener(clicker);
        }

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                //function to check recording
                if (sfPlayRecording) {
                    setImageResource(R.mipmap.ic_rec_pause);
                    try {
                        PlayRecord();
                        sfRecordButton.disable();
                        sfStopButton.enable();
                        Toast toast = Toast.makeText(getContext(), "Playing the audio...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    setImageResource(R.mipmap.ic_rec_play);
                    PausePlay();
                    sfRecordButton.enable();
                    sfStopButton.disable();
                }
                sfPlayRecording = !sfPlayRecording;
            }
        };

        public void enable() {
            setEnabled(true);
            setAlpha(1f);
        }

        public void disable() {
            setEnabled(false);
            setAlpha(0.5f);
        }

    }

    class StopButton extends ImageButton {
        //boolean sfStartRecording = true;
        int whatToStop;

        public StopButton(Context ctx) {
            super(ctx);
            setImageResource(R.mipmap.ic_rec_stop);
            setOnClickListener(clicker);
            disable();
        }

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                if (getWhatToStop() == 1)
                    StopRecord();
                else if (getWhatToStop() == 2)
                    StopPlay();
            }
        };

        public void enable() {
            setEnabled(true);
            setAlpha(1f);
        }

        public void disable() {
            setEnabled(false);
            setAlpha(0.5f);
        }

        public void setWhatToStop(int whatToStop) {
            this.whatToStop = whatToStop;
            enable();
        }

        public int getWhatToStop() {
            return whatToStop;
        }
    }

    // functions for MEDIA RECORDING

    public void StartRecord() {
        ditchMediarecorder();
        File outputFile = new File(OUTPUT_FILE);

        if (outputFile.exists()) {
            outputFile.delete();
        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(AUDIO_SOURCE);
        recorder.setOutputFormat(OUTPUT_FORMAT);
        recorder.setAudioEncoder(AUDIO_ENCODER);
        //recorder.setAudioSamplingRate(Med);
        recorder.setOutputFile(OUTPUT_FILE);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        startTimerCounting();
    }

    public void ditchMediarecorder() {
        if (recorder != null) recorder.release();
    }

    public void StopRecord() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    //finish of MEDIA RECORDING functions

    // functions for MEDIA PLAYER

    public void PlayRecord() throws IOException {
        ditchMediaPlayer();

        //mediaPlayer = new MediaPlayer();

        //check from internal project
        //mediaPlayer = MediaPlayer.create(getContext(), R.raw.bird);
        mediaPlayer.start();
//        try {
//            //mediaPlayer.setDataSource(OUTPUT_FILE);
//            //mediaPlayer.setOnCompletionListener(getContext());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            fillTimer("0", "0", "0");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void ditchMediaPlayer() {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    public void PausePlay() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void StopPlay() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    //Start timer functions

    public void startTimerCounting() {
        curTime = 0;
        Thread thread = new Thread(new TimerRecord());
        thread.start();
    }

    private class TimerRecord implements Runnable {
        @Override
        public void run() {
            while (!sfRecordButton.getSfStartRecording()) {
                ++curTime;
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putInt("count", curTime);

                //bundle.putLong("duration", getDuration(OUTPUT_FILE));
                //bundle.putLong("durationMilliseconds", durationMilliseconds );

                Message message = new Message();
                message.setData(bundle);
                messageHandler.sendMessage(message);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("count", --curTime);
            Message message = new Message();
            message.setData(bundle);
            messageHandler.sendMessage(message);

        }
    }

    // Handler for Runnable class + Timer filling
    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            convertSecondsToTime(msg.getData().getInt("count"));
            //convertMillisecondsToTime(msg.getData().getLong("duration"));
            //convertMillisecondsToTime(msg.getData().getLong("durationMilliseconds"));
        }
    }


    //get duration of file
    //code taken from http://stackoverflow.com/questions/15394640/get-duration-of-audio-file
    public long getDuration(String dataSource) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(dataSource);
        String durationString =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long durationLong = Long.parseLong(durationString);
        convertMillisecondsToTime(durationLong);
        //Toast.makeText(getContext(), durationString, Toast.LENGTH_LONG).show();
        return durationLong;
    }

    public void convertMillisecondsToTime(long duration) {
        String hours = String.valueOf(((duration / (1000 * 60 * 60)) % 24));
        String minutes = String.valueOf((duration / (1000 * 60)) % 60);
        String seconds = String.valueOf((duration / 1000) % 60);
        fillTimer(hours, minutes, seconds);
    }

    public void convertSecondsToTime(int duration) {
        String hours = String.valueOf(duration / 3600);
        String minutes = String.valueOf((duration % 3600) / 60);
        String seconds = String.valueOf((duration % 3600) % 60);
        fillTimer(hours, minutes, seconds);
    }

    public void fillTimer(String h, String m, String s) {
        tvSeconds.setText(s.length() == 1 ? "0" + s : s);
        tvMinutes.setText(m.length() == 1 ? "0" + m : m);
        tvHours.setText(h.length() == 1 ? "0" + h : h);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        sfLayout = (RelativeLayout) view.findViewById(R.id.sfLayout);

        tvSeconds = (TextView) view.findViewById(R.id.tvTimerSeconds);
        tvMinutes = (TextView) view.findViewById(R.id.tvTimerMinutes);
        tvHours = (TextView) view.findViewById(R.id.tvTimerHours);

        curPosition = 0;
        messageHandler = new MessageHandler();

        createLayout();

        Calendar calendar = Calendar.getInstance();
        Toast.makeText(getContext(), calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND), Toast.LENGTH_LONG).show();


        Button buttonUploadRecordingToServer = (Button) view.findViewById(R.id.buttonUploadRecordingToServer);
        buttonUploadRecordingToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "upload", Toast.LENGTH_LONG).show();
                //working

                // do something
                uploadRecordingToServer();
            }
        });


        //file location
        newRecordedFileName = "audio.3gp";
        OUTPUT_FILE = Environment.getExternalStorageDirectory() + File.separator + newRecordedFileName;

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void uploadRecordingToServer() {
        File outputFile = new File(OUTPUT_FILE);
        UserLocalStore userLocalStore = new UserLocalStore(getContext());

        if (outputFile.exists()) {
            String username = userLocalStore.getUsername();
            ServerRequests serverRequests = new ServerRequests(getContext());
            serverRequests.uploadRecordingInBackground(OUTPUT_FILE, newRecordedFileName, username);

            //Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.bird);
            //serverRequests.uploadRecordingInBackground(uri.toString(), "bird", username);

            //Toast.makeText(getContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
