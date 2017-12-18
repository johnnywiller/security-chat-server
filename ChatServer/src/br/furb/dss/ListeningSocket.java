package br.furb.dss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ListeningSocket extends Thread {

	private final int SERVER_PORT = 6678;
	private final int SO_TIMEOUT = 10 * 1000 * 3600;

	private ServerSocket serverSocket;
	
	private int counter;
	
	public ListeningSocket() throws IOException {

		serverSocket = new ServerSocket(SERVER_PORT);
		serverSocket.setSoTimeout(SO_TIMEOUT);

	}

	@Override
	public void run() {
		while (true) {
			try {
				acceptSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void acceptSocket() throws IOException {
		
		Socket sock = serverSocket.accept();
		
		System.out.println("Received connection from " + sock.getInetAddress().getHostAddress());
		
		SocketClient client = new SocketClient();
		
		ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
		ObjectOutputStream out  = new ObjectOutputStream(sock.getOutputStream());
		
		client.setOut(out);
		client.setIn(in);
		
		//client.setName(in.readUTF());
		client.setSocket(sock);
		client.setName(String.valueOf(counter++));
		ConnectionsHandler.getHandler().addClient(client);
		
		System.out.println("Client name is " + client.getName());
		
		ClientThread clientThread = new ClientThread(client);
		clientThread.start();
		

		
	}

}
