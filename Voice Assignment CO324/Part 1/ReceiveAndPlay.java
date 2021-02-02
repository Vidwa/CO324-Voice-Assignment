/*
 * CO 324 - Network and Web Application Design
 * Assignment - Sample Code
 */

import java.net.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.*;

public class ReceiveAndPlay extends RecordPlayback implements Runnable {
	public void run(){
		try {
            // Construct the socket
            DatagramSocket socket = new DatagramSocket(55001);
            System.out.println("The server is ready");

            // Create a packet
            DatagramPacket packet = new DatagramPacket(new byte[100], 100);
            this.playAudio();
			//captureAudio();

            for (;;) {
			//while (true) {
                try {
                    
                    socket.receive(packet);	// Receive a packet
                  
                    // Print the packet
                    this.getSourceDataLine().write(packet.getData(), 0, 100); //playing the audio   

                } catch (Exception e) {
					System.out.println(e);
                }
			}

        } catch (Exception e) {
			System.out.println(e);
        }
	}
}