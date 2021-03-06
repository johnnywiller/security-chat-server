package br.furb.dss;

import java.util.HashMap;
import java.util.Set;

public class ConnectionsHandler {
	
	private static ConnectionsHandler handler;
	private HashMap<String, SocketClient> clients = new HashMap<>();
	
	private ConnectionsHandler() {
		
	}
	
	public static ConnectionsHandler getHandler() {
		if (handler == null)
			handler = new ConnectionsHandler();
		
		return handler;
	}
	
	public Set<String> getUsers() {
		return clients.keySet();
	}
	
	public void removeClient(String key) {
		clients.remove(key);
	}
	
	public void addClient(SocketClient client) {		
		clients.put(client.getName(), client);
	}
	
	public SocketClient getClient(String name) {
		return clients.get(name);
	}
	
}
