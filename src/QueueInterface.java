public interface QueueInterface<T> {

     void put(T data);
     T get();
     boolean isEmpty();

}
