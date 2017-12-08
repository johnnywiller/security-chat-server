package br.furb.dss;

import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MessageEncryptor {

	private ListeningSocket socket;

	byte[] symmetricKey;
	byte[] macKey;

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

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		secret = digest.digest(secret);

		symmetricKey = Arrays.copyOf(secret, 16);
		macKey = Arrays.copyOfRange(secret, 16, 32);

		SecretKeySpec secretKeySpec = new SecretKeySpec(symmetricKey, "AES");

		// read the packet
		byte packetLength =	socket.getIn().readByte();
		
		byte[] packet = new byte[packetLength];

		// read the packet
		socket.getIn().readFully(packet);
		
		byte[] iv = Arrays.copyOf(packet, 16);
		byte[] cipherText = Arrays.copyOfRange(packet, 16, packet.length - iv.length);

		IvParameterSpec ivSpec = new IvParameterSpec(iv);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
		
		System.out.println("PACKET--------------------");
		System.out.print(packetLength);
		System.out.println(Arrays.toString(packet));
		System.out.println("--------------------");
	
		byte[] plainText = cipher.doFinal(cipherText);

		System.out.println("Plain text received: " + new String(plainText));

		// System.out.println("SERVER secret");
		// System.out.println(Arrays.toString(secret));
	}

}
