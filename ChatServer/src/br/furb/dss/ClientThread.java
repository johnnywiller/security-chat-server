package br.furb.dss;

import java.io.IOException;
import java.util.Arrays;

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

				System.out.println(
						"Read " + received + " from " + thisClient.getSocket().getInetAddress().getHostAddress() + ":"
								+ thisClient.getSocket().getPort() + " name = [" + thisClient.getName() + "]");

				String[] tokenized = received.split(" ");
				
				Arrays.toString(tokenized);
				
				if (tokenized[0].equals("/msg")) {

					String user = tokenized[1];
					String msg = received.substring(received.indexOf(tokenized[1]) + 1);
							
					SocketClient destUser = ConnectionsHandler.getHandler().getClient(user);
					System.out.println("dest user " + destUser.getName());
					destUser.getOut().writeUTF(msg);
					destUser.getOut().flush();

				}

			} catch (IOException e) {
				break;
			}

		}

		try {
			if (!thisClient.getSocket().isClosed())
				thisClient.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
