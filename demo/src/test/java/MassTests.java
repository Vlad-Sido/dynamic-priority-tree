

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.example.DynamicPriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class MassTests {
    public Integer[] randomPriorityArray(int capacity, Random rnd)
    {
        var priorityArr = new Integer[capacity];
        for (int i = 0; i < capacity; i++)
        {
            var usedNumbers = new HashSet<Integer>();
            var newNum = rnd.nextInt();
            while (usedNumbers.contains(newNum))
                newNum = rnd.nextInt();
            usedNumbers.add(newNum);
            priorityArr[i] = newNum;
        }
        return priorityArr;
    }
    @Test
    public void ArraySorterTest() 
    {
        var rnd = new Random(500);
        var priorityArr = randomPriorityArray(10000, rnd);
        var safeQueue = new PriorityQueue<Integer>((i1, i2) -> priorityArr[i1].compareTo(priorityArr[i2]));
        var testQueue = new DynamicPriorityQueue<Integer, Integer>();
        for (int i = 0; i < 10000; i++)
        {
            safeQueue.offer(i);
            testQueue.offer(i, i, priorityArr[i]);
        }
        for (int i = 0; i < 10001; i++) // 10001 обеспечивает, что последняя операция вернет null
            assertEquals(safeQueue.poll(), testQueue.poll());
    }
    @Test
    public void StaticStressRandomTest()
    {
        var rnd = new Random(1234);
        var priorityArr = randomPriorityArray(1000, rnd);
        var safeQueue = new PriorityQueue<Integer>((i1, i2) -> priorityArr[i1].compareTo(priorityArr[i2]));
        var testQueue = new DynamicPriorityQueue<Integer, Integer>();
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
                    assertEquals(exp, testQueue.offer(newVal, newVal, indArr));
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
        var safeQueue = new PriorityQueue<Integer>(1000, (i1, i2) -> priorityArr[i1].compareTo(priorityArr[i2]));
        var testQueue = new DynamicPriorityQueue<Integer, Integer>();
        var numsInQueue = new HashSet<Integer>();
        Integer newVal = 0;
        boolean exp;
        for (int i = 0; i < 10000; i++)
        {
            var nextAction = rnd.nextInt(5);
            if (nextAction >= 2)
                newVal = rnd.nextInt(1000);
            switch (nextAction)
            {
                case 0:
                    assertEquals(safeQueue.peek(), testQueue.peek());
                    break;
                case 1:
                    numsInQueue.remove(safeQueue.peek());
                    assertEquals(safeQueue.poll(), testQueue.poll());
                    break;
                case 2:
                    int indArr = newVal;
                    if (safeQueue.contains(indArr))
                        exp = false;
                    else
                        exp = safeQueue.offer(indArr);
                    numsInQueue.add(indArr);
                    assertEquals(exp, testQueue.offer(newVal, newVal, () -> priorityArr[indArr]));
                    break;
                case 3:
                    usedNumbers.remove(priorityArr[newVal]);
                    var newPrio = rnd.nextInt();
                    while (usedNumbers.contains(newPrio))
                        newPrio = rnd.nextInt();
                    priorityArr[newVal] = newPrio;
                    exp = safeQueue.remove(newVal);
                    assertEquals(exp, testQueue.update(newVal));
                    if (exp)
                    {
                        safeQueue.offer(newVal);
                    }
                case 4:
                    safeQueue.clear();
                    for (var j : numsInQueue)
                    {
                        usedNumbers.remove(priorityArr[j]);
                        var massPrio = rnd.nextInt();
                        while (usedNumbers.contains(massPrio))
                            newPrio = rnd.nextInt();
                        priorityArr[j] = massPrio;
                        safeQueue.offer(j);
                    }
                    testQueue.updateAll();
            }
        }
    }
}
