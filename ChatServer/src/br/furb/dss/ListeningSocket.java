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

		SocketClient client = new SocketClient();
		
		ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
		
		client.setName(in.readUTF());
		client.setSocket(sock);

		ConnectionsHandler.getHandler().addClient(client);
		
		
		
	}

}
