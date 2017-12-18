package br.furb.dss;

import java.io.IOException;
import java.math.BigInteger;

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
				System.out.println("aqui");
				thisClient.getIn().read(received);
				
				System.out.println("read");
				
				parsePacket(received);

			} catch (IOException e) {
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			if (!thisClient.getSocket().isClosed())
				thisClient.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void parsePacket(byte[] packet) throws IOException, ClassNotFoundException {

		String msg = new String(packet);
		
		System.out.println("received " + msg);
		
		String[] tokenized = msg.split(" ");

		switch (tokenized[0]) {

		case "/msg":
			break;

		case "/startsession":
			System.out.println("Received start session, I'm " + thisClient.getName());
			startSession(tokenized[1]);
			break;
		
		
		case "/acksession":
			System.out.println("Received ack session, I'm " + thisClient.getName());
			ackSession(tokenized[1]);
			break;
			
		}
	}

	private void ackSession(String client) throws ClassNotFoundException, IOException {
		
		SocketClient sclient = ConnectionsHandler.getHandler().getClient(client);
		
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
