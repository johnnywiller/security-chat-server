package br.furb.dss;

public class Main {

	public static void main(String[] args) {
		System.out.println("Started Server");
		MessageEncryptor encryptor = new MessageEncryptor();
		try {
			encryptor.encryptMessage("ola mundo server");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
