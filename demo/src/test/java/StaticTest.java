

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.example.DynamicPriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class StaticTest {
    @Test
    public void StaticQueueTest()
    {
        var queue = new DynamicPriorityQueue<Integer>();
        queue.add(7, () -> 4);
        queue.add(1, () -> 5);
        queue.add(2, () -> 6);
        queue.add(3, () -> 3);
        queue.add(5, () -> 7);
        queue.add(10, () -> 1);
        queue.add(8, () -> 2);
        assertEquals(7, queue.size());
        assertEquals(10, queue.poll());
        assertEquals(8, queue.poll());
        assertEquals(3, queue.poll());
        assertEquals(7, queue.poll());
        assertEquals(1, queue.poll());
        assertEquals(2, queue.poll());
        assertEquals(5, queue.poll());
    }
    @Test
    public void StaticStressRandomTest()
    {
        var rnd = new Random(1234);
        var priorityArr = new Integer[1000];
        for (int i = 0; i < 1000; i++)
        {
            var usedNumbers = new HashSet<Integer>();
            var newNum = rnd.nextInt();
            while (usedNumbers.contains(newNum))
                newNum = rnd.nextInt();
            usedNumbers.add(newNum);
            priorityArr[i] = newNum;
        }
        var safeQueue = new PriorityQueue<Integer>((i1, i2) -> priorityArr[i1].compareTo(priorityArr[i2]));
        var testQueue = new DynamicPriorityQueue<Integer>();
        Integer newVal = 0;
        for (int i = 0; i < 10000; i++)
        {
            var nextAction = rnd.nextInt(3);
            if (nextAction == 2)
                newVal = rnd.nextInt(1000);
            switch (nextAction)
            {
                case 0:
                    assertEquals(safeQueue.peek(), testQueue.peek());
                    break;
                case 1:
                    assertEquals(safeQueue.poll(), testQueue.poll());
                    break;
                case 2:
                    int indArr = priorityArr[newVal].intValue();
                    boolean exp;
                    if (safeQueue.contains(newVal))
                        exp = false;
                    else
                        exp = safeQueue.offer(newVal);
                    assertEquals(exp, testQueue.offer(newVal, () -> indArr));
                    break;
            }
        }
    }
    @Test
    public void DynamicStressRandomTest()
    {
        var rnd = new Random(1234);
        var priorityArr = new Integer[1000];
        var usedNumbers = new HashSet<Integer>();
        for (int i = 0; i < 1000; i++)
        {
            var newNum = rnd.nextInt();
            while (usedNumbers.contains(newNum))
                newNum = rnd.nextInt();
            usedNumbers.add(newNum);
            priorityArr[i] = newNum;
        }
        var safeQueue = new PriorityQueue<Integer>((i1, i2) -> priorityArr[i1].compareTo(priorityArr[i2]));
        var testQueue = new DynamicPriorityQueue<Integer>();
        Integer newVal = 0;
        boolean exp;
        for (int i = 0; i < 10000; i++)
        {
            var nextAction = rnd.nextInt(4);
            if (nextAction >= 2)
                newVal = rnd.nextInt(1000);
            switch (nextAction)
            {
                case 0:
                    assertEquals(safeQueue.peek(), testQueue.peek());
                    break;
                case 1:
                    assertEquals(safeQueue.poll(), testQueue.poll());
                    break;
                case 2:
                    int indArr = newVal;
                    if (safeQueue.contains(newVal))
                        exp = false;
                    else
                        exp = safeQueue.offer(newVal);
                    assertEquals(exp, testQueue.offer(newVal, () -> priorityArr[indArr]));
                    break;
                case 3:
                    usedNumbers.remove(priorityArr[newVal]);
                    var newPrio = rnd.nextInt();
                    while (usedNumbers.contains(newPrio))
                        newPrio = rnd.nextInt();
                    priorityArr[newVal] = newPrio;
                    System.out.println(newPrio);
                    exp = safeQueue.remove(newVal);
                    assertEquals(exp, testQueue.update(newVal));
                    if (exp)
                        safeQueue.offer(newVal);
            }
        }
    }
}
