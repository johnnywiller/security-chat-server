package br.furb.dss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ListeningSocket extends Thread {

	private final int SERVER_PORT = 6678;
	private final int SO_TIMEOUT = 10 * 1000 * 3600;

	private ServerSocket serverSocket;

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
		
		//client.setName(in.readUTF());
		client.setSocket(sock);
		client.setName("teste222");
		ConnectionsHandler.getHandler().addClient(client);
		
		ClientThread clientThread = new ClientThread(client);
		clientThread.start();
		

		
	}

}
