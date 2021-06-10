
public class HeapQueue<E extends Comparable<E>> implements Queue<E> {
    private Heap<E> heap;
    
    public HeapQueue() {
        heap = new Heap<E>();
    }
    public int size() {
        return heap.size();
    }
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    public void enqueue(E e) {
        heap.add(e);
    }
    public E dequeue() {
        return heap.remove();
    }
    public E first() {
        return heap.min();
    }
}
