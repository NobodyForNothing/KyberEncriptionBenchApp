package de.rausch.richard.kyberEncriptionBenchApp;

import de.rausch.richard.Util;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;

import javax.crypto.KeyGenerator;
import java.security.*;

public class Bob {
    private final KeyPair bobKeyPair;
    private byte[] encryptionKey;

    public Bob() throws NoSuchAlgorithmException {
        // generate key asymmetric Kyber keypair
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("Kyber");
        bobKeyPair = keyPairGen.generateKeyPair();
    }

    public void decapsulateSecretKey(byte[] encapsulatedKey) {
        // System.out.println("Bob entschlüsselt: " + Util.byteArrToHex(encapsulatedKey));
        try {
            // ein schlüsselgenerator wird mit Bobs privatem schlüssel und dem erhaltenden verschlüsseltem Schlüssel initialisiert
            KeyGenerator keyGen = KeyGenerator.getInstance("Kyber");
            keyGen.init(new KEMExtractSpec(bobKeyPair.getPrivate(), encapsulatedKey, "AES"), new SecureRandom());

            // der AES Schlüssel wird entschlüsselt und gespeichert
            SecretKeyWithEncapsulation secEnc = (SecretKeyWithEncapsulation) keyGen.generateKey();
            encryptionKey = secEnc.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public PublicKey getPublic() {
        return bobKeyPair.getPublic();
    }

    @Override
    public String toString() {
        return "Bob:" +
                /*
                "\n\tprivate key:\t" + Util.byteArrToHex(bobKeyPair.getPrivate().getEncoded()) +
                "\n\tpublic key:\t" + Util.byteArrToHex(bobKeyPair.getPublic().getEncoded()) +
                */
                "\n\tsecret key:\t" + Util.byteArrToHex(encryptionKey);
    }
}
