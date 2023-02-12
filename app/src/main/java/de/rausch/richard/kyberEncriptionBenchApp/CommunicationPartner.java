package de.rausch.richard.kyberEncriptionBenchApp;

import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;

public interface CommunicationPartner {
    PublicKey getPublic();
    void connectTo(CommunicationPartner partner);
    void unwrapSecretKey(byte[] encapsulatedSecretKey);
    @NotNull
    String toString();

}
