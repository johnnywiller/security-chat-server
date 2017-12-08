package br.furb.dss;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ListeningSocket {

	private ObjectOutputStream out;
	private ObjectInputStream in;

	private HashMap<String, Socket> clients = new HashMap<>();

	public ListeningSocket() throws Exception {

		ServerSocket socket = new ServerSocket(6678);
		socket.setSoTimeout(10 * 1000 * 3600);

		Socket client = socket.accept();

		//clients.put(client.getInetAddress().getHostAddress(), client);

		this.in = new ObjectInputStream(client.getInputStream());
		this.out = new ObjectOutputStream(client.getOutputStream());

	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public ObjectInputStream getIn() {
		return in;
	}

}
