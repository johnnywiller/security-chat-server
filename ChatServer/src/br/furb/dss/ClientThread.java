package br.furb.dss;

import java.io.IOException;
import java.util.Arrays;

public class ClientThread extends Thread {

	private SocketClient thisClient;

	private final int MAX_BUF_SIZE = 255;

	private byte[] ourNameInBytes;

	public ClientThread(SocketClient client) {
		this.thisClient = client;
		this.ourNameInBytes = String.format("%1$10s", client.getName()).getBytes();
	}

	@Override
	public void run() {

		while (true) {

			byte[] received = new byte[MAX_BUF_SIZE];

			try {

				thisClient.getIn().read(received);

				parsePacket(received);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Client = " + thisClient.getName() + " \n " + e.getMessage());
				break;
			}
		}

		try {
			System.out.println(thisClient.getName() + " has leaved the room");
			if (!thisClient.getSocket().isClosed()) {
				thisClient.getSocket().close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ConnectionsHandler.getHandler().removeClient(thisClient.getName());
		}

	}

	private void parsePacket(byte[] packet) throws IOException, ClassNotFoundException, InterruptedException {

		byte[] resizedPacket = getResizedPacket(packet);

		String msg = new String(resizedPacket);

		String[] tokenized = msg.split(" ");

		switch (tokenized[0].trim()) {

		case "/startsession":
			System.out.println("Received start session, I'm " + thisClient.getName());
			startSession(tokenized[1].trim());
			break;

		case "/acksession":
			System.out.println("Received ack session, I'm " + thisClient.getName());
			ackSession(tokenized[1]);
			break;
		case "/changeuser":
			break;
		case "/getpublic":
			requestPublicKey(tokenized[1]);
			break;

		case "/online":
			requestOnlineUsers();
			break;
		default:
			// default is to send message to another user
			byte[] bytesFromUser = Arrays.copyOf(resizedPacket, 10);

			String fromUser = new String(bytesFromUser).trim();

			routeToUser(packet, fromUser);

		}
	}

	private void requestOnlineUsers() throws IOException {

		thisClient.getOut().write("/online".getBytes());

		for (String user : ConnectionsHandler.getHandler().getUsers()) {
			thisClient.getOut().write(user.getBytes());
		}

		thisClient.getOut().write("/endonline".getBytes());

		thisClient.getOut().flush();
	}

	private void requestPublicKey(String who) throws IOException {

		byte[] pubKey = new byte[385];

		// get user keys
		SocketClient sWho = ConnectionsHandler.getHandler().getClient(who);

//		pubKey[0] = (byte) ((sWho.getPublicKey().getEncoded().length - 200));
//
//		System.arraycopy(sWho.getPublicKey().getEncoded(), 0, pubKey, 1, sWho.getPublicKey().getEncoded().length);
//
//		thisClient.getOut().write(pubKey);
//		thisClient.getOut().flush();
		
		thisClient.getOut().writeObject(sWho.getPublicKey());
		thisClient.getOut().flush();
		System.out.println("MANDOU CHAVE PRIVADA PARA CLIENTE");

	}

	private void routeToUser(byte[] packet, String user) throws IOException {

		// get user keys
		SocketClient toSend = ConnectionsHandler.getHandler().getClient(user);

		// change user header
		System.arraycopy(ourNameInBytes, 0, packet, 1, ourNameInBytes.length);

		toSend.getOut().write(packet);
		toSend.getOut().flush();

	}

	private void changeUsername(String name) {

		ConnectionsHandler.getHandler().removeClient(thisClient.getName());
		thisClient.setName(name);
		ConnectionsHandler.getHandler().addClient(thisClient);

	}

	private void ackSession(String client) throws ClassNotFoundException, IOException {

		SocketClient sclient = ConnectionsHandler.getHandler().getClient(client.trim());

		byte[] dhParam = new byte[385];

		// read P
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

		sclient.getOut().flush();

		dhParam = new byte[385];

		// read G
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

		sclient.getOut().flush();

		dhParam = new byte[385];

		// read Y
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

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

		byte[] dhParam = new byte[385];

		// read P
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

		sclient.getOut().flush();

		dhParam = new byte[385];

		// read G
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

		sclient.getOut().flush();

		dhParam = new byte[385];

		// read Y
		thisClient.getIn().read(dhParam);
		sclient.getOut().write(dhParam);

		sclient.getOut().flush();

	}

	private byte[] getResizedPacket(byte[] packet) {
		byte[] resized;
		byte size = packet[0];

		resized = Arrays.copyOfRange(packet, 1, size + 1);

		return resized;
	}

}
