package com.example.collection;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomLinkedListCollector<E> implements Collector<E, CustomLinkedList<E>, CustomLinkedList<E>> {

    @Override
    public Supplier<CustomLinkedList<E>> supplier() {
        return CustomLinkedList::new;
    }

    @Override
    public BiConsumer<CustomLinkedList<E>, E> accumulator() {
        return CustomLinkedList::add;
    }

    @Override
    public BinaryOperator<CustomLinkedList<E>> combiner() {
        return (list1, list2) -> {
            list2.iterator().forEachRemaining(list1::add);
            return list1;
        };
    }

    @Override
    public Function<CustomLinkedList<E>, CustomLinkedList<E>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    public static <E> Collector<E, ?, CustomLinkedList<E>> toCustomLinkedList() {
        return new CustomLinkedListCollector<>();
    }
}
