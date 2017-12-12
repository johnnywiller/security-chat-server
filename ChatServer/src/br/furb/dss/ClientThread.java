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

				System.out.println(
						"Read " + received + " from " + thisClient.getSocket().getInetAddress().getHostAddress() + ":"
								+ thisClient.getSocket().getPort() + " name = [" + thisClient.getName() + "]");

				String[] tokenized = received.split(" ");

				if (tokenized[0].equals("/msg")) {

					String user = tokenized[1];

					SocketClient destUser = ConnectionsHandler.getHandler().getClient(user);

					destUser.getOut().writeUTF(tokenized[2]);

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
