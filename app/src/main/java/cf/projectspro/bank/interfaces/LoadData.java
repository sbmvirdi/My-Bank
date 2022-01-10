package cf.projectspro.bank.interfaces;

/**
 * Interface to send generic data
 * @param <T> data to be loaded
 */
public interface LoadData<T> {
    void onDataLoaded(T data);
}
