/*
 * CO 324 - Network and Web Application Design
 * Assignment - Sample Code
 */

import java.net.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;


public class RecordPlayback implements Runnable {
	
	//final int packetsize = 100;
	//final int port = 55000;
	private static InetAddress host = null;
	private DatagramSocket socket = null;
	

	boolean stopCapture = false;
	ByteArrayOutputStream byteArrayOutputStream;
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	byte tempBuffer[] = new byte[500];

	/*public RecordPlayback(InetAddress host){ //Constructor of the class
		this.host = host;
	}*/




	public AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

	public synchronized SourceDataLine getSourceDataLine() {
		return sourceDataLine;
	}
	
	public synchronized TargetDataLine getTargetDataLine() {
		return targetDataLine;
	}

	/*private void captureAudio() {
		
		try {
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
			System.out.println("Available mixers:");
			Mixer mixer = null;
			for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
				System.out.println(cnt + " " + mixerInfo[cnt].getName());
				mixer = AudioSystem.getMixer(mixerInfo[cnt]);

				Line.Info[] lineInfos = mixer.getTargetLineInfo();
				if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
					System.out.println(cnt + " Mic is supported!");
					break;
				}
			}

			audioFormat = getAudioFormat();     //get the audio format
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

			targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();

			DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			
			//Setting the maximum volume
			FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(control.getMaximum());

			//captureAndSend(); //playing the audio

		} catch (LineUnavailableException e) {
			System.out.println(e);
			System.exit(0);
		}
	  
	}*/

	public synchronized void captureAudio() {
		
		try {
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
			System.out.println("Available mixers:");
			Mixer mixer = null;
			for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
				System.out.println(cnt + " " + mixerInfo[cnt].getName());
				mixer = AudioSystem.getMixer(mixerInfo[cnt]);

				Line.Info[] lineInfos = mixer.getTargetLineInfo();
				if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
					System.out.println(cnt + " Mic is supported!");
					break;
				}
			}

			audioFormat = getAudioFormat();     //get the audio format
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

			targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			
		} catch (LineUnavailableException e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	public void playAudio() {
		try{
			audioFormat = getAudioFormat();     //get the audio format
			
			DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			
			//Setting the maximum volume
			FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(control.getMaximum());

			//captureAndSend(); //playing the audio

		} catch (LineUnavailableException e) {
			System.out.println(e);
			System.exit(0);
		}
		
	}

	//private void captureAndPlay() {
	private void captureAndSend() {
		byteArrayOutputStream = new ByteArrayOutputStream();
		stopCapture = false;
		try {
			int readCount;
			while (!stopCapture) {
				readCount = targetDataLine.read(tempBuffer, 0, tempBuffer.length);  //capture sound into tempBuffer
				if (readCount > 0) {
					byteArrayOutputStream.write(tempBuffer, 0, readCount);
					//sourceDataLine.write(tempBuffer, 0, 500);   //playing audio available in tempBuffer
					
					// Construct the datagram packet
					DatagramPacket packet = new DatagramPacket(this.tempBuffer, this.tempBuffer.length, this.host,55001);
						
					socket.send(packet);	// Send the packet
				}
			}
			byteArrayOutputStream.close();
		} catch (IOException e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	/*private void receiveAndPlay() {
		try {
				// Construct the socket
				//DatagramSocket socket = new DatagramSocket(55001);
				System.out.println("The server is ready");

				// Create a packet
				DatagramPacket packet = new DatagramPacket(new byte[100], 100);
				this.playAudio();
				//captureAudio();

				for (;;) {
				//while (1) {
					try {

						// Receive a packet (blocking)
						socket_receive.receive(packet);
					  
						// Print the packet
						this.getSourceDataLine().write(packet.getData(), 0, 100); //playing the audio   

					} catch (Exception e) {
						System.out.println(e);
					}
				}

			} catch (Exception e) {
				System.out.println(e);
			}
		
		
	}*/


	public void run() {	//threads
		try {
			RecordPlayback playback = new RecordPlayback();
			this.socket = new DatagramSocket(55000); //construct the socket
			//socket_receive = new DatagramSocket(55001); //construct the socket
			
			playback.captureAudio();
			//captureAndPlay();
			playback.captureAndSend();
			//playback.receiveAndPlay();

		} catch (Exception e) {
			System.out.println(e);

		} finally {
			socket.close();
			//socket_receive.close();
		}
	}

	public static void main(String[] args) {
		
		//RecordPlayback playback = new RecordPlayback();
		//playback.captureAudio();
		
		if (args.length != 1) {	// Check the whether the arguments are given
			System.out.println("DatagramClient host ");
			return;
		}
		
		try {
				host = InetAddress.getByName(args[0]);
				Thread s = new Thread(new RecordPlayback());
				s.start();

				Thread r = new Thread(new ReceiveAndPlay());
				r.start();

			} catch (Exception e) {
				System.out.println(e);
			}

	}




}