import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class Server {
	private static int srvc_port;
	private static String mcast_addr;
	private static int mcast_port;
	
	private static String service_address = "127.0.0.1";
	
	private static HashMap<String, String> registry;
	
	public static void main(String[] args) throws IOException {
		//Check number of arguments
		if(args.length != 3) {
			System.out.println("Error. Wrong number of arguments.");
			System.out.println("Please, try as follows: java Server <srvc_port> <mcast_addr> <mcast_port>");
			return;
		}
		
		srvc_port = Integer.parseInt(args[0]);
		mcast_addr = args[1];
		mcast_port = Integer.parseInt(args[2]);
		
		registry = new HashMap<String,String>();
				
		//Open MulticastSocket
		MulticastSocket multicast_socket = new MulticastSocket();
		multicast_socket.setTimeToLive(1);
		InetAddress multicast_address = InetAddress.getByName(mcast_addr);
		
		//Open Socket
		DatagramSocket server_socket = new DatagramSocket(srvc_port);
		server_socket.setSoTimeout(2000);
		
		long elapsed_time = 2000;
		long time_started = System.currentTimeMillis();
		
		//Listen for requests and send response
		boolean done = false;
		while(!done) {
			//Listening
			byte[] buffer = new byte[256];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			
			try {
				server_socket.receive(packet);
				String request = new String(packet.getData(), 0, packet.getLength());
				String response = handleRequest(request);
				
				//Responding
				buffer = response.getBytes();
				InetAddress client_address = packet.getAddress();
				int client_port = packet.getPort();
				packet = new DatagramPacket(buffer, buffer.length, client_address, client_port);
				server_socket.send(packet);
				
			} catch(SocketTimeoutException e) {
				//System.out.println(e);
			}
			
			long current_time = System.currentTimeMillis();
			elapsed_time += current_time - time_started;
			time_started = current_time;
			
			//Advertise
			if(elapsed_time >= 2000) {
				elapsed_time -= 2000;
				String advertisement = service_address + "&" + Integer.toString(srvc_port);
				packet = new DatagramPacket(advertisement.getBytes(), advertisement.getBytes().length, multicast_address, mcast_port);
				multicast_socket.send(packet);
			}
		}
		server_socket.close();
		multicast_socket.close();
	}
	
	private static String handleRequest(String request) {
		String[] parsed = request.split("&");
		String request_type = parsed[0];
		String plate_number = parsed[1];
		if(parsed[0].compareTo("register") == 0) {
			String owner_name = parsed[2];
			
			//Check if plate is already registered
			if(registry.containsKey(plate_number)) {
				System.out.println(request_type + " " + plate_number + " " + owner_name + " :: -1");
				return "-1";
			}
			else {
				registry.put(plate_number,owner_name);
				System.out.println(request_type + " " + plate_number + " " + owner_name + " :: " + registry.size());
				return "" + registry.size();
			}
		}
		else {
			if(registry.containsKey(plate_number)) {
				System.out.println(request_type + " " + plate_number + " :: " + registry.get(plate_number));
				return registry.get(plate_number);
			}
			else {
				System.out.println(request_type + " " + plate_number + " :: NOT_FOUND");
				return "NOT_FOUND";
			}
		}
	}
}
