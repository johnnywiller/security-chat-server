package br.furb.dss;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {

	private Socket socket;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ObjectOutputStream getOut() throws IOException {
		return (ObjectOutputStream) socket.getOutputStream();
	}
	
	public ObjectInputStream getIn() throws IOException {
		return (ObjectInputStream) socket.getInputStream();
	}

}
