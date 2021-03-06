package com.example.gribovod.beanbag;

import android.media.*;

/**
 * Created by Shilin on 15.09.2017.
 */

public class SamplePlayer extends Thread {
    public static final int MODE_NOISE = 0;
    public static final int MODE_A = 1;

    int SoundMode = MODE_NOISE;

    volatile boolean finishFlag;
    public int bufferSize;
    private short[] raw;
    private short[] data;
    private int sampleRate = 16000;
    private double modulation = 0;
    public SamplePlayer() {
        finishFlag = false;
        bufferSize = AudioTrack.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        set_raw_data();
        data = new short[bufferSize];
    }

    public void setFinishFlag() {
        finishFlag = true;
    }

    public void setModulation(double value)
    {
        modulation = value;
    }

    @Override
    public void run() {
        try {
            AudioTrack aTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize, AudioTrack.MODE_STREAM);
            aTrack.play();

            while (!finishFlag) {
                for (int i = 0; i < data.length; i++) {
                    data[i] = (short) (raw[i] * modulation);
                }
                aTrack.write(data, 0, data.length);
            }

            aTrack.stop();
        } catch (Exception e) {
        }
    }

    private short[] generateNoise(int count) {
        short[] samples = new short[count];
        for (int i = 0; i < count; i++) {
            samples[i] = (short) ((Math.random() - 0.5) * 0x7FFF * (1 - i/(double)count));
        }
        return samples;
    }

    private short[] generateTone(int count, double freqHz) {
        short[] samples = new short[count];
        for (int i = 0; i < count; i++) {
            short sample = (short) (Math.sin(2 * Math.PI * i / (sampleRate / freqHz)) * 0x7FFF * (1 - i/(double)count));
            samples[i] = sample;
        }
        return samples;
    }

    public void setSoundMode(int mode) {
        if (mode!= SoundMode)
        {
            SoundMode = mode;
            set_raw_data();
        }
    }


    private void set_raw_data()
    {
        switch (SoundMode){
            case MODE_NOISE:
            {
                raw = generateNoise(bufferSize);
                break;
            }
            case MODE_A:
            {
                raw = generateTone(bufferSize, 400);
                break;
            }
        }
    }
}
