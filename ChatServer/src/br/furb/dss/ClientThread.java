package br.furb.dss;

import java.io.IOException;

public class ClientThread extends Thread {

	private SocketClient thisClient;

	public ClientThread(SocketClient client) {
		this.thisClient = client;
	}

	@Override
	public void run() {

		while (true) {

			String received;
			
			try {
			
				received = thisClient.getIn().readUTF();

				System.out.println("Read " + received + " from " + thisClient.getName());

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
