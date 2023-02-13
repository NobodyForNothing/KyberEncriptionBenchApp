package de.rausch.richard.kyberEncriptionBenchApp;

import de.rausch.richard.Util;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.jetbrains.annotations.NotNull;

import javax.crypto.KeyGenerator;
import java.security.*;

public class KyberCommunicationPartner implements CommunicationPartner{
    private final KeyPair keyPair;
    private byte[] encryptionKey;

    public KyberCommunicationPartner() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // generate key asymmetric Kyber keypair
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("Kyber");
        keyPairGen.initialize(KyberParameterSpec.kyber1024_aes);
        keyPair = keyPairGen.generateKeyPair();
    }

    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    public void connectTo(CommunicationPartner partner) {
        try {
            // symmetrischen key generator erstellen und AES-Schlüssel erzeugen
            // für das KEM wird der öffentliche Schlüssel des kommunikationspartners übergeben
            KeyGenerator keyGen = KeyGenerator.getInstance("Kyber");
            keyGen.init(new KEMGenerateSpec(partner.getPublic(), "AES"), new SecureRandom());
            SecretKeyWithEncapsulation secretAESKey = (SecretKeyWithEncapsulation) keyGen.generateKey();

            // der generierte AES schlüssel wird gespeichert
            encryptionKey = secretAESKey.getEncoded();

            // der mit Bobs öffentlichem Schlüssel verschlüsselte AES-Schlüssel wird an Bob gesendet
            byte[] encapsulatedKey = secretAESKey.getEncapsulation();
            partner.unwrapSecretKey(encapsulatedKey);

        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void unwrapSecretKey(byte[] encapsulatedKey) {
        try {
            // ein schlüsselgenerator wird mit privatem schlüssel und dem erhaltenden Schlüssel initialisiert
            KeyGenerator keyGen = KeyGenerator.getInstance("Kyber");
            keyGen.init(new KEMExtractSpec(keyPair.getPrivate(), encapsulatedKey, "AES"), new SecureRandom());

            // der AES Schlüssel wird entschlüsselt und gespeichert
            SecretKeyWithEncapsulation secEnc = (SecretKeyWithEncapsulation) keyGen.generateKey();
            encryptionKey = secEnc.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "KyberCommunicationPartner:" +
                "\n\tprivate key:\t" + Util.byteArrToHex(keyPair.getPrivate().getEncoded()) +
                "\n\tpublic key:\t" + Util.byteArrToHex(keyPair.getPublic().getEncoded()) +
                "\n\tsecret key:\t" + Util.byteArrToHex(encryptionKey);
    }

}
