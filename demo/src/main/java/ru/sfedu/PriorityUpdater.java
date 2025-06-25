package ru.sfedu;

/**
 * Представитель приоритета, функциональный интерфейс.
 * Для правильной работы, функция должна возвращать одно и то же число, если вызвана несколько раз подряд.
 * Единственная функция: {@link #priority()}.
 * Практически эквивалентно {@linkplain java.util.function.Supplier Supplier}<{@linkplain Integer}>.
 */
@FunctionalInterface
public interface PriorityUpdater {
    /**
     * Предоставление приоритета
     */
    int priority();
}
