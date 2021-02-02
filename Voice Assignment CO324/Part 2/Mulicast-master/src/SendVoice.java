import java.net.*;
import java.io.IOException;


public class SendVoice extends Voice {

    private final int packetSize = 1000;
    private int port;
    private InetAddress host;
    private MulticastSocket socket = null;
    private byte buffer[] = new byte[this.packetSize];

    public SendVoice(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    private void send() {
        try {
            int count;
            for (; ; ) {
                System.out.print("");
                count = this.getTargetDataLine().read(this.buffer, 0, this.buffer.length);  //capture sound into buffer
                if (Integer.signum(count) > 0) {
//                    System.out.println("sending audio");
                    // Construct the packet
                    DatagramPacket packet = new DatagramPacket(this.buffer, this.buffer.length, this.host, this.port);
                    // Send the packet
                    this.socket.send(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        try {
            initSocket();
            //setup the audio input and output,function is in the parent class
            this.captureAudio();

            //start sending the packets
            this.send();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }

    private void initSocket() throws IOException {
        this.socket = new MulticastSocket(this.port);

        //avoid hearing my voice again
        this.socket.setLoopbackMode(true);//avoid loop back

        //used for windows environments ,it enables the multicast
        this.socket.setBroadcast(true);
        //join the multicast group
        this.socket.joinGroup(this.host);
    }


}