package br.furb.dss;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

public class ClientThread extends Thread {

	private SocketClient thisClient;

	private final int MAX_BUF_SIZE = 255;

	public ClientThread(SocketClient client) {
		this.thisClient = client;
	}

	@Override
	public void run() {

		while (true) {

			byte[] received = new byte[255];

			try {
				
				thisClient.getIn().read(received);

				parsePacket(received);

			} catch (Exception e) {
				break;
			}
		}

		try {
			System.out.println("closed");
			if (!thisClient.getSocket().isClosed())
				thisClient.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void parsePacket(byte[] packet) throws IOException, ClassNotFoundException, InterruptedException {

		String msg = new String(packet);

		String[] tokenized = msg.split(" ");

		System.out.println("token 0 = " + tokenized[0]);

		switch (tokenized[0].trim()) {

		case "/startsession":
			System.out.println("Received start session, I'm " + thisClient.getName());
			startSession(tokenized[1].trim());
			break;

		case "/acksession":
			System.out.println("Received ack session, I'm " + thisClient.getName());
			ackSession(tokenized[1]);
			break;
		default:
			// default is to send message to another user
			routeToUser(packet);

		}
	}

	private void routeToUser(byte[] packet) throws IOException {

		byte[] bytesFromUser = Arrays.copyOf(packet, 10);

		String fromUser = new String(bytesFromUser).trim();

		// get user keys
		SocketClient toSend = ConnectionsHandler.getHandler().getClient(fromUser);

		toSend.getOut().write(packet);
		toSend.getOut().flush();

	}

	private void ackSession(String client) throws ClassNotFoundException, IOException {

		SocketClient sclient = ConnectionsHandler.getHandler().getClient(client.trim());

		BigInteger p, g, y;

		p = (BigInteger) thisClient.getIn().readObject();
		g = (BigInteger) thisClient.getIn().readObject();
		y = (BigInteger) thisClient.getIn().readObject();

		sclient.getOut().writeObject(p);
		sclient.getOut().writeObject(g);
		sclient.getOut().writeObject(y);

		sclient.getOut().flush();

	}

	private void startSession(String client) throws IOException, ClassNotFoundException {

		System.out.println("starting session client = " + client);

		SocketClient sclient = ConnectionsHandler.getHandler().getClient(client);

		String msg = "/startsession " + thisClient.getName();

		byte[] packet = new byte[msg.length() + 1];

		packet[0] = (byte) msg.length();

		System.arraycopy(msg.getBytes(), 0, packet, 1, msg.getBytes().length);

		sclient.getOut().write(packet);
		sclient.getOut().flush();

		BigInteger p, g, y;

		p = (BigInteger) thisClient.getIn().readObject();
		g = (BigInteger) thisClient.getIn().readObject();
		y = (BigInteger) thisClient.getIn().readObject();

		sclient.getOut().writeObject(p);
		sclient.getOut().writeObject(g);
		sclient.getOut().writeObject(y);

		sclient.getOut().flush();

	}

}
