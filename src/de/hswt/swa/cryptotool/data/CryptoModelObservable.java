package de.hswt.swa.cryptotool.data;

public interface CryptoModelObservable {

    public void registerObserver(CryptoModelObserver observer);

    public void unRegisterObserver(CryptoModelObserver observer);

    public void fireUpdate();

}
