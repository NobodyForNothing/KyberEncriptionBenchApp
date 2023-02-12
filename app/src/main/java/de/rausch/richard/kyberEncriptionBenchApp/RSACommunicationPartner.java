package de.rausch.richard.kyberEncriptionBenchApp;

import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;

public class RSACommunicationPartner implements CommunicationPartner{
    private final KeyPair keyPair;
    private byte[] encryptionKey;

    public RSACommunicationPartner() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPair = keyPairGen.generateKeyPair();
    }

    @Override
    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    @Override
    public void connectTo(CommunicationPartner partner) {

    }

    @Override
    public void decapsulateSecretKey(byte[] encapsulatedSecretKey) {

    }
}
