package br.furb.dss;

import java.security.KeyPair;
import java.util.Arrays;

import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;

public class MessageEncryptor {

	private ListeningSocket socket;

	public MessageEncryptor() {
		try {
			this.socket = new ListeningSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void encryptMessage(String msg) throws Exception {

		DiffieHellmanUitls dh = new DiffieHellmanUitls();

		KeyPair keyPair = dh.generateKeyPair();

		dh.passPublicToClient((DHPublicKey) keyPair.getPublic(), socket.getOut());

		DHPublicKey publicKey = dh.getClientPublic(socket.getIn());

		byte[] secret = dh.computeDHSecretKey((DHPrivateKey) keyPair.getPrivate(), publicKey);
		
		System.out.println("SERVER secret");
		System.out.println(Arrays.toString(secret));
	}

}
