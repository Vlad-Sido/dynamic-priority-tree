package ru.sfedu.DynamicPriorityQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Имплементация динамической {@linkplain java.util.PriorityQueue очереди приоритета}. 
 * Для приоритета используется {@linkplain PriorityUpdater функция, возвращающая целое число}.
 * Корнем очереди считается элемент с наименьшим приоритетом.
 * Элемент не может быть добавлен в очередь, если он уже находится в ней.
 */
public class DynamicPriorityQueue<T, I> {
    /**
     * Создает пустую очередь
     */
    public DynamicPriorityQueue()
    {
    }


    /**
     * Добавляет элемент в очередь
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean offer(T value, I identifier, PriorityUpdater updater) // PriorityQueue
    {
        if (elements.containsKey(identifier))
            return false;
        insertNode(value, identifier, updater, updater.priority());
        return true;
    }
    /**
     * Добавляет элемент в очередь
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean add(T value, I identifier, PriorityUpdater updater) // AbstractQueue
    {
        return offer(value, identifier, updater);
    }
    /**
     * Добавляет элемент в очередь, со статическим приоритетом.
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean offer(T value, I identifier, int priority) // PriorityQueue
    {
        if (elements.containsKey(identifier))
            return false;
        insertNode(value, identifier, null, priority);
        return true;
    }
    /**
     * Добавляет элемент в очередь, со статическим приоритетом.
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean add(T value, I identifier, int priority) // PriorityQueue
    {
        return offer(value, identifier, priority);
    }

    /**
     * Возвращает корень очереди, не удаляя его.
     * Если очередь пуста, возвращает {@code null}
     */
    public T peek() // PriorityQueue
    {
        // Возвращает null если пусто, как и PriorityQueue.
        if (root == null)
            return null;
        return root.value;
    }
    /**
     * Возвращает корень очереди, не удаляя его.
     * Если очередь пуста, возвращает {@code null}
     */
    public T element() // AbstractQueue
    {
        return peek();
    }

    /**
     * Возвращает корень очереди и удаляет его.
     * Если очередь пуста, возвращает {@code null}
     */
    public T poll() // PriorityQueue
    {
        // Возвращает null если пусто, как и PriorityQueue.
        if (root == null)
            return null;
        var res = root.value;
        elements.remove(root.identifier);
        root = mergePairs(root.leftChild);
        return res;
    }
    /**
     * Возвращает корень очереди и удаляет его.
     * Если очередь пуста, возвращает {@code null}
     */
    public T remove() // AbstractQueue
    {
        return poll();
    }

    /**
     * Удаляет элемент из очереди.
     * @return {@code true} если он был, иначе {@code false}
     */
    public boolean remove(Object o)
    {
        var node = elements.get(o);
        if (node == null)
            return false;
        decreaseKey(node, Integer.MIN_VALUE);
        poll();
        return true;
    }

    /**
     * Упостушает очередь полностью
     */
    public void clear()
    {
        root = null;
        for (var node : elements.values())
        {
            node.leftChild = null;
            node.rightChild = null;
            node.nextNode = null;
            node.prevNode = null; 
        }
        elements.clear();
    }

    /**
     * Проверяет, ести ли в очереди объект.
     * @return {@code true} если он был, иначе {@code false}
     */
    public boolean contains(Object o)
    {
        return elements.keySet().contains(o);
    }

    /**
     * Возвращает количество элементов в очереди.
     */
    public int size() {
        return elements.size();
    }

    /**
     * Проверяет, пуста ли очередь.
     * @return {@code true} если в очереди нет элементов, иначе {@code false}
     */
    public boolean isEmpty()
    {
        return root == null;
    }

    /**
     * Обновляет приоритет определенного значения.
     * @return {@code true} если в очереди он есть, иначе {@code false}
     */
    public boolean update(Object o)
    {
        var node = elements.get(o);
        if (node == null)
            return false;
        if (node.updater == null)
            return true;
        var newKey = node.updater.priority();
        if (newKey < node.key)
            decreaseKey(node, newKey);
        else if (newKey > node.key)
            increaseKey(node, newKey);
        return true;
    }

    /**
     * Обновляет приоритет всех значений.
     */
    public void updateAll()
    {
        var nodeList = elements.values().stream().map(node -> new SimpleNode(node.value, node.identifier, node.updater, node.key)).collect(Collectors.toList());
        clear();
        for (var node : nodeList)
            insertNode(node.value, node.identifier, node.updater, node.updater == null ? node.key : node.updater.priority());
    }
    private class SimpleNode
    {
        private T value;
        private int key;
        private PriorityUpdater updater;
        private I identifier;
        public SimpleNode(T value, I identifier, PriorityUpdater updater, int key)
        {
            this.value = value;
            this.updater = updater;
            this.key = key;
            this.identifier = identifier;
        }
    }

    private class Node
    {
        private T value;
        private int key;
        private PriorityUpdater updater;
        private Node leftChild;
        private Node rightChild;
        private Node nextNode;
        private Node prevNode;
        private boolean leftmost = true;
        private boolean rightmost = true;
        private I identifier;
        public Node(T value, I identifier, PriorityUpdater updater, int key)
        {
            this.value = value;
            this.updater = updater;
            this.key = key;
            this.identifier = identifier;
        }
        public String toString()
        {
            return String.format("v: %s, k: %d", value, key);
        }
    }
    private HashMap<I, Node> elements = new HashMap<>();
    private Node root = null;

    // Вспомогательные методы

    /**
     *  Слияние деревьев
     */
    private Node meld(Node elem1, Node elem2)
    {
        // Если одно пустое, возвращаем другое
        if (elem1 == null)
            return elem2;
        if (elem2 == null)
            return elem1;
        // Иначе, добавляем более крупное как элемент меньшего
        if (elem2.key < elem1.key)
        {
            var temp = elem1;
            elem1 = elem2;
            elem2 = temp;
        }
        if (elem1.leftChild != null)
        {
            elem2.nextNode = elem1.leftChild;
            elem2.nextNode.prevNode = elem2;
            elem2.nextNode.leftmost = false;
            elem2.rightmost = false;
        }
        else
        {
            elem1.rightChild = elem2;
            elem2.nextNode = elem1;
            elem2.rightmost = true;
        }
        elem2.prevNode = elem1;
        elem1.leftChild = elem2;
        elem2.leftmost = true;
        return elem1;
    }

    /**
     *  Слияние деревьев парами
     */
    private Node mergePairs(Node node)
    {
        if (node == null)
            return null;
        node.prevNode = null;
        Node nextNode = node.rightmost ? null : node.nextNode;
        node.nextNode = null;
        node.leftmost = true;
        node.rightmost = true;
        return mergePairs(node, nextNode);
    }
    private Node mergePairs(Node node1, Node node2)
    {
        if (node2 == null)
            return node1;
        node2.prevNode = null;
        Node nextNode = node2.rightmost ? null : node2.nextNode;
        node2.nextNode = null;
        node2.leftmost = true;
        node2.rightmost = true;
        return meld(meld(node1, node2), mergePairs(nextNode));
    }

    /**
     *  Уменьшает ключ вершины
     */
    private void decreaseKey(Node node, int newVal)
    {
        node.key = newVal;
        if (node == root)
            return;
        if (!node.leftmost && !node.rightmost)
        {
            node.prevNode.nextNode = node.nextNode;
            node.nextNode.prevNode = node.prevNode;
        }
        else if (!node.leftmost)
        {
            node.prevNode.nextNode = node.nextNode;
            node.prevNode.rightmost = true;
            node.nextNode.rightChild = node.prevNode;
        }
        else if (!node.rightmost)
        {
            node.nextNode.prevNode = node.prevNode;
            node.nextNode.leftmost = true;
            node.prevNode.leftChild = node.nextNode;
        }
        else
        {
            node.prevNode.leftChild = null;
            node.nextNode.rightChild = null;
        }
        node.prevNode = null;
        node.nextNode = null;
        node.leftmost = true;
        node.rightmost = true;
        root = meld(root, node);
    }

    private void increaseKey(Node node, int newKey)
    {
        if (node == root)
        {
            var prio = node.updater;
            var val = node.value;
            var id = node.identifier;
            poll();
            insertNode(val, id, prio, newKey);
            return;
        }
        node.key = newKey;
        if (node.leftChild == null)
            return;
        node.leftChild.prevNode = node.prevNode;
        node.rightChild.nextNode = node.nextNode;
        if (node.leftmost)
            node.prevNode.leftChild = node.leftChild;
        else
        {
            node.leftChild.leftmost = false;
            node.prevNode.nextNode = node.leftChild;
            node.leftmost = true;
        }
        if (node.rightmost)
            node.nextNode.rightChild = node.rightChild;
        else
        {
            node.rightChild.rightmost = false;
            node.nextNode.prevNode = node.rightChild;
            node.rightmost = true;
        }
        node.prevNode = null;
        node.nextNode = null;
        node.leftChild = null;
        node.rightChild = null;
        root = meld(node, root);
    }

    private Node insertNode(T value, I identifier, PriorityUpdater updater, int key)
    {
        var newNode = new Node(value, identifier, updater, key);
        root = meld(newNode, root);
        elements.put(identifier, newNode);
        return newNode;
    }

    public String toString()
    {
        return String.format("head: %s, elements: %s", root, elements);
    }
}
