package Comuns;

import java.util.LinkedList;

public class BlockingQueue<T> {

	private LinkedList<T> queue = new LinkedList<T>();
	private static int QUEUE_MAX_SIZE;

	public BlockingQueue(int i) {
		QUEUE_MAX_SIZE = i;
	}

	public BlockingQueue() {

	}

	// Adicionar elemento à queue
	public synchronized void add(T t) {

		while (isFull()) {
			try {
				System.out.println("Queue está cheia!");
				wait();

			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}

		queue.add(t);
		notifyAll();

	}

	// Remover elemento da queue
	public synchronized T remove() {

		while (isEmpty()) {

			try {
				System.out.println("Queue está vazia!");
				wait();

			} catch (InterruptedException e) {
				e.printStackTrace();

			}
		}

		T aux = queue.removeFirst();
		notifyAll();

		return aux;
	}

	// Verificar se a queue está vazia
	public boolean isEmpty() {

		if (queue.size() == 0)
			return true;

		return false;
	}

	// Verificar se a queue está cheia
	private boolean isFull() {

		if (QUEUE_MAX_SIZE == queue.size())
			return true;

		return false;
	}

	// Obter tamanho da queue
	public int size() {
		return queue.size();

	}

	// Limpar a queue
	public void clear() {
		queue.clear();

	}

	// Obter um certo elemento
	public T get(int i) {
		return queue.get(i);

	}

	// Obter um certo elemento mas não o remover da queue
	public T peek() {
		return queue.peek();

	}

	// Remover um elemento da queue
	public T removeByIndex(int i) {
		return queue.remove(i);

	}

}