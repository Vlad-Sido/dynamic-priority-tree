package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Имплементация динамической {@linkplain java.util.PriorityQueue очереди приоритета}. 
 * Для приоритета используется {@linkplain PriorityUpdater функция, возвращающая целое число}.
 * Корнем очереди считается элемент с наименьшим приоритетом.
 * Элемент не может быть добавлен в очередь, если он уже находится в ней.
 */
public class DynamicPriorityQueue<T> {
    /**
     * Создает пустую очередь
     */
    public DynamicPriorityQueue()
    {
    }
    /**
     * Создает очередь с данными элементами
     * Ключом словаря являются элементы, а значением: либо {@linkplain PriorityUpdater функция, возвращающая целое число}, либо целое число.
     */
    public DynamicPriorityQueue(Map<T, ?> priorities)
    {
        addAll(priorities);
    }


    /**
     * Добавляет элемент в очередь
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean offer(T value, PriorityUpdater updater) // PriorityQueue
    {
        if (elements.containsKey(value))
            return false;
        var newNode = new Node(value, updater, new ArrayList<>());
        elements.put(value, newNode);
        root = meld(newNode, root);
        return true;
    }
    /**
     * Добавляет элемент в очередь
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean add(T value, PriorityUpdater updater) // AbstractQueue
    {
        return offer(value, updater);
    }
    /**
     * Добавляет элемент в очередь, со статическим приоритетом.
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean offer(T value, int priority) // PriorityQueue
    {
        return offer(value, () -> priority);
    }
    /**
     * Добавляет элемент в очередь, со статическим приоритетом.
     * @return {@code true} если его еще нет, иначе {@code false}
     */
    public boolean add(T value, int priority) // PriorityQueue
    {
        return offer(value, () -> priority);
    }

    /**
     * Добавляет все элементы из словаря в очередь
     * Ключом словаря являются элементы, а значением: либо {@linkplain PriorityUpdater функция, возвращающая целое число}, либо целое число.
     * @return {@code true} если хотя бы один был добавлен, иначе {@code false}
     */
    public boolean addAll(Map<T, ?> priorities)
    {
        if (priorities.isEmpty())
            return false;
        var res = false;
        for (var entry : priorities.entrySet())
        {
            if (entry.getValue() instanceof PriorityUpdater pu)
            {
                offer(entry.getKey(), pu);
                res = true;
            }
            else if (entry.getValue() instanceof Integer num)
            {
                offer(entry.getKey(), num);
                res = true;
            }
        }
        return res;
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
        elements.remove(res);
        root = mergePairs(root.children);
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
        var newKey = node.updater.priority();
        if (newKey < node.key)
            decreaseKey(node, newKey);
        else if (newKey > node.key)
        {
            var updater = node.updater;
            var val = node.value;
            decreaseKey(node, Integer.MIN_VALUE);
            poll();
            offer(val, updater);
        }
        return true;
    }

    /**
     * Обновляет приоритет всех значений.
     */
    public void updateAll()
    {
        var elementMap = elements.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().updater));
        clear();
        addAll(elementMap);
    }

    private class Node
    {
        private T value;
        private int key;
        private ArrayList<Node> children;
        private PriorityUpdater updater;
        private Node parent;
        public Node(T value, PriorityUpdater updater, ArrayList<Node> children)
        {
            this.value = value;
            this.updater = updater;
            this.children = children;
            key = updater.priority();
        }
        public String toString()
        {
            return String.format("v: %s, k: %d", value, key);
        }
    }
    private HashMap<T, Node> elements = new HashMap<>();
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
        if (elem1.key < elem2.key)
        {
            elem1.children.add(elem2);
            elem2.parent = elem1;
            return elem1;
        }
        elem2.children.add(elem1);
        elem1.parent = elem2;
        return elem2;
    }

    /**
     *  Слияние деревьев парами
     */
    private Node mergePairs(ArrayList<Node> list)
    {
        if (list.isEmpty())
            return null;
        if (list.size() == 1)
        {
            list.get(0).parent = null;
            return list.get(0);
        }
        var node1 = list.removeFirst();
        var node2 = list.removeFirst();
        node1.parent = null;
        node2.parent = null;
        return meld(meld(node1, node2), mergePairs(list));
    }

    /**
     *  Уменьшает ключ вершины
     */
    private void decreaseKey(Node node, int newVal)
    {
        if (node.key == newVal)
            return;
        node.key = newVal;
        if (node.parent == null)
            return;
        node.parent.children.remove(node);
        node.parent = null;
        root = meld(root, node);
    }
}
