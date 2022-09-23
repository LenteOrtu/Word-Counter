public class Node<T> {
    protected T data;
    protected Node<T> next;

    Node(T data) {
        this.next = null;
        this.data = data;
    }

    public T getData() {
        // return data stored in this node
        return data;
    }

    public Node<T> getNext() {
        // get next node
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
