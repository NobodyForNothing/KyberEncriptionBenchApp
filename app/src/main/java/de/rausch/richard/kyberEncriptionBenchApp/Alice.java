package de.rausch.richard.kyberEncriptionBenchApp;

import de.rausch.richard.Util;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;

import javax.crypto.KeyGenerator;
import java.security.*;

public class Alice {
    private final KeyPair aliceKeyPair;
    private byte[] encryptionKey;

    public Alice() throws NoSuchAlgorithmException {
        // generate key asymmetric Kyber keypair
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("Kyber");
        aliceKeyPair = keyPairGen.generateKeyPair();
    }

    public void connectTo(Bob bob) {
        try {
            // symmetrischen key generator erstellen und AES-Schlüssel erzeugen
            // für das KEM wird bobs öffentlicher Schlüssel übergeben
            KeyGenerator keyGen = KeyGenerator.getInstance("Kyber");
            keyGen.init(new KEMGenerateSpec(bob.getPublic(), "AES"), new SecureRandom());
            SecretKeyWithEncapsulation secretAESKey = (SecretKeyWithEncapsulation) keyGen.generateKey();

            // der generierte AES schlüssel wird gespeichert
            encryptionKey = secretAESKey.getEncoded();

            // der mit Bobs öffentlichem Schlüssel verschlüsselte AES-Schlüssel wird an Bob gesendet
            byte[] encapsulatedKey = secretAESKey.getEncapsulation();
            bob.decapsulateSecretKey(encapsulatedKey);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public String toString() {
        return "Alice:" +
                /*
                "\n\tprivate key:\t" + Util.byteArrToHex(aliceKeyPair.getPrivate().getEncoded()) +
                "\n\tpublic key:\t" + Util.byteArrToHex(aliceKeyPair.getPublic().getEncoded()) +
                 */
                "\n\tsecret key:\t" + Util.byteArrToHex(encryptionKey);
    }
}
