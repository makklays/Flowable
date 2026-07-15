package com.example.rxjava;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Example of Reactive Java Flowable 
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 15.07.2026
 */ 
public class RxJavaFlowableExample {
    public static void main(String[] args) throws InterruptedException {
        
        // 1. Создаем Flowable-источник, генерирующий числа от 1 до 1 000 000
        Flowable<Integer> numberStream = Flowable.create(emitter -> {
            for (int i = 1; i <= 1_000_000 && !emitter.isCancelled(); i++) {
                emitter.onNext(i);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER); // Если подписчик не успевает, данные временно буферизируются

        // 2. Настраиваем асинхронную обработку данных
        numberStream
                .subscribeOn(Schedulers.io()) // Генерация происходит в IO-потоке
                .observeOn(Schedulers.computation(), false, 16) // Обработка в вычислительном потоке порциями по 16 элементов
                .map(number -> {
                    // Имитируем тяжелые вычисления
                    Thread.sleep(10); 
                    return number * 2;
                })
                .subscribe(
                        result -> System.out.println("Обработано число: " + result),
                        throwable -> System.err.println("Ошибка: " + throwable.getMessage()),
                        () -> System.out.println("Поток данных полностью обработан!")
                );

        // Держим главный поток активным, чтобы асинхронные потоки успели поработать
        Thread.sleep(5000);
    }
}

