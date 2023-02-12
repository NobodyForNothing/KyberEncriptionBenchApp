package de.rausch.richard.kyberEncriptionBenchApp;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.generators.BaseKDFBytesGenerator;
import org.bouncycastle.crypto.kems.RSAKEMGenerator;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;

public class RSACommunicationPartner implements CommunicationPartner{
    private final KeyPair keyPair;
    private byte[] encryptionKey;

    public RSACommunicationPartner() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4)); //TODO: match key sizes
        keyPair = keyPairGen.generateKeyPair();
    }

    @Override
    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    @Override
    public void connectTo(CommunicationPartner partner) {
        try {
            byte[] aeskeyBytes = new byte[16];
            new SecureRandom().nextBytes(aeskeyBytes);
            Key aesKey = new SecretKeySpec(aeskeyBytes,"AES");
            encryptionKey = aesKey.getEncoded();

            // Encrypt the AES key using RSA
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
            rsaCipher.init(Cipher.WRAP_MODE, partner.getPublic());

            byte[] encapsulatedKey = rsaCipher.wrap(aesKey);
            partner.unwrapSecretKey(encapsulatedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unwrapSecretKey(byte[] encapsulatedSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
            cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate(), new SecureRandom());

            Key key = cipher.unwrap(encapsulatedSecretKey, "AES", Cipher.SECRET_KEY);
            encryptionKey = key.getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }
}
