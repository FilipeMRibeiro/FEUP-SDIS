import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Client {
	private static String host_name;
	private static int port;
	private static String request_type, plate_number, owner_name;
	
	public static void main(String[] args) throws IOException {
		//Check args
		if(args.length != 4 && args.length != 5) {
			System.out.println("Error. Wrong number of arguments.");
			System.out.println("Please try as follows: java Client <host_name> <port_number> [register <plate_number> <owner_name> | lookup <plate_number>]");;
			return;
		}
		else if(args[2].compareTo("register") == 0 && args.length != 5) {
			System.out.println("Error. Wrong number of arguments for register command.");
			System.out.println("Please try as follows: java Client <host_name> <port_number> register <plate_number> <owner_name>");;
			return;
		}
		else if(args[2].compareTo("lookup") == 0 && args.length != 4) {
			System.out.println("Error. Wrong number of arguments for lookup command.");
			System.out.println("Please try as follows: java Client <host_name> <port_number> lookup <plate_number>");;
			return;
		}
		else {
			host_name = args[0];
			port = Integer.parseInt(args[1]);
			request_type = args[2];
			plate_number = args[3];
			String request;
			if(args.length == 5) {
				owner_name = args[4];
				request = new String(request_type + "&" + plate_number + "&" + owner_name);
			}
			else request = new String(request_type + "&" + plate_number);
			
			//Open Socket
			DatagramSocket socket = new DatagramSocket();
			System.out.println("Socket was open.");
			
			//Send request
			byte[] buffer = request.getBytes();
			InetAddress address = InetAddress.getLocalHost();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
			socket.send(packet);
			System.out.println("Sent: " + request);
			
			//Receive response
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String response = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Received: " + response);
			
			//Close socket
			socket.close();
		}
	}
}