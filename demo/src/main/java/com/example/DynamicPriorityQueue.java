package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamicPriorityQueue<T> {
    protected class Node
    {
        protected T value;
        protected int key;
        protected ArrayList<Node> children;
        protected PriorityUpdater updater;
        protected Node parent;
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
    protected HashMap<T, Node> elements = new HashMap<>();
    protected Node root = null;

    public DynamicPriorityQueue()
    {

    }
    public DynamicPriorityQueue(Map<T, ?> priorities)
    {
        addAll(priorities);
    }

    // Вспомогательные методы

    // Слияние деревьев
    protected Node meld(Node elem1, Node elem2)
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

    // Слияние деревьев парами
    protected Node mergePairs(ArrayList<Node> list)
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

    // Уменьшает ключ вершины
    protected void decreaseKey(Node node, int newVal)
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

    // Вставить вершину и ее вернуть 
    protected Node insertNode(T value, PriorityUpdater updater)
    {
        var newNode = new Node(value, updater, new ArrayList<>());
        elements.put(value, newNode);
        root = meld(newNode, root);
        return newNode;
    }
    
    // Использование как очередь

    // Вставление нового элемента как слияние с корнем
    public boolean offer(T value, PriorityUpdater updater) // PriorityQueue
    {
        if (elements.containsKey(value))
            return false;
        var newNode = new Node(value, updater, new ArrayList<>());
        elements.put(value, newNode);
        root = meld(newNode, root);
        return true;
    }
    public boolean add(T value, PriorityUpdater updater) // AbstractQueue
    {
        return offer(value, updater);
    }
    public boolean offer(T value, int priority) // PriorityQueue
    {
        return offer(value, () -> priority);
    }
    public boolean add(T value, int priority) // PriorityQueue
    {
        return offer(value, () -> priority);
    }

    // Вставление множества
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

    // Нахождение минимума
    public T peek() // PriorityQueue
    {
        // Возвращает минимум если пусто, как и PriorityQueue.
        if (root == null)
            return null;
        return root.value;
    }
    public T element() // AbstractQueue
    {
        return peek();
    }

    // Вытаскивание минимума
    public T poll() // PriorityQueue
    {
        // Возвращает минимум если пусто, как и PriorityQueue.
        if (root == null)
            return null;
        var res = root.value;
        elements.remove(res);
        root = mergePairs(root.children);
        return res;
    }
    public T remove() // AbstractQueue
    {
        return poll();
    }

    // Удаляет какой-то объект
    public boolean remove(Object o)
    {
        var node = elements.get(o);
        if (node == null)
            return false;
        decreaseKey(node, Integer.MIN_VALUE);
        poll();
        return true;
    }

    // Очистка очереди
    public void clear()
    {
        root = null;
        elements.clear();
    }

    // Имеет ли элемент
    public boolean contains(Object o)
    {
        return elements.keySet().contains(o);
    }

    // Итератор
    public Iterator<T> iterator() {
        return elements.keySet().iterator();
    }

    // Размер
    public int size() {
        return elements.size();
    }

    // Пустой ли
    public boolean isEmpty()
    {
        return root == null;
    }

    // В массив
    public Object[] toArray()
    {
        return elements.keySet().toArray();
    }

    // Уникальное для динамической очереди

    // Обновить элемент. false если его нет, иначе true.
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

    // Обновить все значения
    public void updateAll()
    {
        var elementMap = elements.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().updater));
        clear();
        addAll(elementMap);
    }

    public String toString()
    {
        return String.format("head: %s\nelements: %s", root, elements.values().toString());
    }
}
