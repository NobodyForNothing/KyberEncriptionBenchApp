package de.rausch.richard.kyberEncriptionBenchApp;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;

import javax.crypto.*;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;

public class RSACommunicationPartner implements CommunicationPartner{
    private final KeyPair keyPair;
    private byte[] encryptionKey;

    public RSACommunicationPartner() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(new RSAKeyGenParameterSpec(3072, RSAKeyGenParameterSpec.F4));
        keyPair = keyPairGen.generateKeyPair();
    }

    @Override
    public PublicKey getPublic() {
        return keyPair.getPublic();
    }

    @Override
    public void connectTo(CommunicationPartner partner) {
        // implementiert nach https://github.com/rodbate/bouncycastle-examples/blob/master/src/main/java/bcfipsin100/base/Rsa.java#L197
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES", "BCFIPS");
            keyGenerator.init(256);
            SecretKey aesKey = keyGenerator.generateKey();

            Cipher cipher = Cipher.getInstance("RSA-KTS-KEM-KWS", "BCFIPS");
            cipher.init(Cipher.WRAP_MODE, keyPair.getPublic(), new KTSParameterSpec.Builder(NISTObjectIdentifiers.id_aes256_wrap.getId(), 256).build());

            byte[] encapsulatedKey = cipher.wrap(aesKey);
            partner.unwrapSecretKey(encapsulatedKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException |
                 InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unwrapSecretKey(byte[] encapsulatedSecretKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA-KTS-KEM-KWS", "BCFIPS");
            cipher.init(Cipher.UNWRAP_MODE, keyPair.getPrivate(), new KTSParameterSpec.Builder(NISTObjectIdentifiers.id_aes256_wrap.getId(), 256).build());

            Key key = cipher.unwrap(encapsulatedSecretKey, "AES", Cipher.SECRET_KEY);
            encryptionKey = key.getEncoded();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException |
                 InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
