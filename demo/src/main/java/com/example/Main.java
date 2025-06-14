package com.example;

public class Main {
    public static void main(String[] args) {
        var queue = new DynamicPriorityQueue<Integer>();
        queue.add(7, () -> 4);
        System.out.println(queue.peek());
        queue.add(1, () -> 5);
        System.out.println(queue.peek());
        queue.add(2, () -> 6);
        System.out.println(queue.peek());
        queue.add(3, () -> 3);
        System.out.println(queue.peek());
        queue.add(5, () -> 7);
        System.out.println(queue.peek());
        queue.add(10, () -> 1);
        System.out.println(queue.peek());
        queue.add(8, () -> 2);
        System.out.println(queue.peek());
        System.out.println(queue);
    }
}