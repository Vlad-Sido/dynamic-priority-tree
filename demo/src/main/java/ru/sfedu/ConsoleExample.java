package ru.sfedu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ConsoleExample {
    public static void help(boolean manualID)
    {
        System.out.println("Комманды:");
        if (manualID)
            System.out.println("insert <value> <id> <priority>: вставляет значение с указанным идентификатором и приоритетом.");
        else
            System.out.println("insert <value> <priority>: вставляет значение с указанным приоритетом.");
        System.out.println("peek: печатает значение корня очереди.");
        System.out.println("poll: вытаскивает корень очереди и печатает его значение.");
        System.out.println("update <id> <priority>: меняет приоритет вершины.");
        System.out.println("manual (on|off): включает или выключает ввод идентификатора вручную.");
        System.out.println("help: показывание этого сообщения.");
        System.out.println("exit: закончить исполнение.");
    } 
    public static void errorMessage()
    {
        System.out.println("Неправильный формат комманды. Используйте help для списка комманд.");
    }
    public static void main(String[] args) throws IOException
    {
        boolean manualID = true;
        System.out.print("Должен ли идентификкатор автоматически быть равен значению, вместо указывания вручную? (y/n) ");
        var input = new BufferedReader(new InputStreamReader(System.in));
        if (input.readLine().equals("y"))
            manualID = false;
        var queue = new DynamicPriorityQueue<String, String>();
        var priorities = new HashMap<String, Integer>();
        help(manualID);
        while (true)
        {
            var line = input.readLine().split(" ");
            switch (line[0])
            {
                case "help":
                    help(manualID);
                    break;
                case "manual":
                    if (line.length < 2)
                    {
                        errorMessage();
                        break;
                    }
                    if (line[1].equals("on"))
                    {
                        manualID = true;
                        System.out.println("Ввод идентификатора вручную включен! См. help для нового формата insert.");
                    }
                    else if (line[1].equals("off"))
                    {
                        manualID = true;
                        System.out.println("Идентификатор автоматически равен значению! См. help для нового формата insert.");
                    }
                    else
                        errorMessage();
                    break;
                case "peek":
                    System.out.println(queue.peek());
                    break;
                case "poll":
                    var removed = queue.poll();
                    priorities.remove(removed);
                    System.out.println(removed);
                    break;
                case "insert":
                    if (line.length < (manualID ? 4 : 3))
                    {
                        errorMessage();
                        break;
                    }
                    Integer prio;
                    try {
                        prio = Integer.parseInt(line[manualID ? 3 : 2]);
                    }
                    catch (NumberFormatException ex)
                    {
                        errorMessage();
                        break;
                    }
                    var id = manualID ? line[2] : line[1];
                    if (priorities.containsKey(id))
                    {
                        System.out.println("В очереди это значение уже есть.");
                        break;
                    }
                    priorities.put(id, prio);
                    queue.offer(line[1], id, () -> priorities.get(id));
                    System.out.println("Значение добавлено.");
                    break;
                case "update":
                    if (line.length < 3 || !priorities.containsKey(line[1]))
                    {
                        errorMessage();
                        break;
                    }
                    Integer newPrio;
                    try {
                        newPrio = Integer.parseInt(line[2]);
                    }
                    catch (NumberFormatException ex)
                    {
                        errorMessage();
                        break;
                    }
                    priorities.replace(line[1], newPrio);
                    queue.update(line[1]);
                    System.out.println("Значение обновлено.");
                    break;
                case "exit":
                    return;
                default:
                    errorMessage();
                    break;
            }
        }
    }
}
