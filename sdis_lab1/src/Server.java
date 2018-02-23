import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class Server {
	private static int port;
	private static HashMap<String, String> registry;
	
	public static void main(String[] args) throws IOException{
		//Check number of arguments
		if(args.length != 1) {
			System.out.println("Error. Wrong number of arguments.");
			System.out.println("Please try as follows: java Server <port_number>");
			return;
		}
		else {
			port = Integer.parseInt(args[0]);
		}
		
		registry = new HashMap<String,String>();
		//Open socket
		DatagramSocket socket = new DatagramSocket(port);
		System.out.println("Socket was open. Port: " + port);
		
		//Listen for requests and send response
		boolean done = false;
		while(!done) {
			//Listening
			System.out.println("Now listening...");
			byte[] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String request = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Received request");
			String response = handleRequest(request);
			
			//Responding
			buffer = response.getBytes();
			InetAddress address = packet.getAddress();
			int clientPort = packet.getPort();
			packet = new DatagramPacket(buffer, buffer.length, address, clientPort);
			socket.send(packet);
			
		}
		
		socket.close();
	}
	
	private static String handleRequest(String request) {
		String[] parsed = request.split("&");
		String request_type = parsed[0];
		String plate_number = parsed[1];
		System.out.println("Request Type: " + request_type);
		System.out.println("Plate Number: " + plate_number);
		if(parsed[0].compareTo("register") == 0) {
			String owner_name = parsed[2];
			System.out.println("Owner Name: " + owner_name);
			
			//Check if plate is already registered
			if(registry.containsKey(plate_number)) {
				System.out.println("Plate number already existed. Did not register.");
				return "-1";
			}
			else {
				registry.put(plate_number,owner_name);
				System.out.println("Registered plate number and owner name.");
				return "" + registry.size();
			}
		}
		else {
			if(registry.containsKey(plate_number)) {
				System.out.println("Found plate number in registry.");
				return registry.get(plate_number);
			}
			else {
				System.out.println("Did not find plate number in registry.");
				return "NOT_FOUND";
			}
		}
	}
}