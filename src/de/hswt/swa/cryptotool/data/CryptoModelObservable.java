package de.hswt.swa.cryptotool.data;

/**
 * @author AdrianWild
 * @version 1.0
 */
public interface CryptoModelObservable {

    void registerObserver(CryptoModelObserver observer);

    void unRegisterObserver(CryptoModelObserver observer);

    void fireUpdate();

}
