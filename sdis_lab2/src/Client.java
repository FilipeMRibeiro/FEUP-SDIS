import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class Client {
	private static String mcast_addr;
	private static int mcast_port;
	
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
		
		mcast_addr = args[0];
		mcast_port = Integer.parseInt(args[1]);
		
		//Opens Multicast Socket & Joins Multicast Group
		InetAddress multicast_group = InetAddress.getByName(mcast_addr);
		MulticastSocket multicast_socket = new MulticastSocket(mcast_port);
		multicast_socket.joinGroup(multicast_group);
		
		//Receives Ads
		byte[] buffer = new byte[256];
		DatagramPacket multicast_packet = new DatagramPacket(buffer, buffer.length);
		multicast_socket.receive(multicast_packet);		
		
		String advertisement = new String(multicast_packet.getData());
		String[] parsed = advertisement.split("&");
		String srvc_addr = parsed[0];
		int srvc_port = Integer.parseInt(parsed[1].replaceAll("[^\\d.]", ""));
		System.out.println("multicast: " + mcast_addr + " " + mcast_port + ": " + srvc_addr + " " + srvc_port);
		
		//Generates request string
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

		//Send request
		buffer = request.getBytes();
		InetAddress address = InetAddress.getByName(srvc_addr);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, srvc_port);
		socket.send(packet);

		//Receive response
		packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		String response = new String(packet.getData(), 0, packet.getLength());
		System.out.println("Result: " + response);

		//Close socket
		socket.close();
		
		multicast_socket.leaveGroup(multicast_group);
		multicast_socket.close();
	}
}
