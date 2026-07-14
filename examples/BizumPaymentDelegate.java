package com.spain.bank.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import java.net.ConnectException;

/**
 * Обрати внимание: мы выбрасываем обычный RuntimeException, чтобы Flowable понял, что это технический сбой, и запустил наш кастомный цикл ретраев.
 *
 * Что происходит в Базе Данных во время ретраев?
 * Как физически в базе данных Flowable понимает, сколько попыток осталось?
 * 1. Во время паузы (этой самой 1 минуты) задача лежит в таблице ACT_RU_JOB.
 * 2. В колонке RETRIES_ изначально будет записано число 4.
 * 3. После первого падения движок уменьшит это число: в колонке RETRIES_ станет 3, а в колонке DUEDATE_ выставится время следующего запуска (текущее время + 1 минута).
 * 4. Если после 4 попыток сервис так и не заработал, строка перейдет в таблицу ACT_RU_DEADLETTER_JOB (это сигнал для админов, что процесс застрял окончательно и ретраи исчерпаны).
 *
 * Теперь пишем сам делегат. Мы симулируем ошибку подключения (например, ConnectException).
 * Мы выбрасываем обычный RuntimeException, чтобы Flowable понял, что это технический сбой, и запустил наш кастомный цикл ретраев.
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 14.07.2026
 */
@Component("bizumPaymentDelegate")
public class BizumPaymentDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String phone = (String) execution.getVariable("clientPhone");
        Double amount = (Double) execution.getVariable("paymentAmount");
        
        System.out.println("[Bizum] Intentando procesar pago de " + amount + "€ para el teléfono: " + phone);
        
        try {
            // Симуляция вызова внешнего API Bizum
            boolean gatewayIsDown = true; 
            if (gatewayIsDown) {
                throw new ConnectException("Bizum API timeout / Error de conexión");
            }
            
            // Если всё прошло успешно, фиксируем статус
            execution.setVariable("bizumStatus", "SUCCESS");
            
        } catch (ConnectException e) {
            System.err.println("[Bizum] Error de red detectado. Forzando reintento en Flowable...");
            
            // Выбрасываем RuntimeException. Flowable перехватит его, 
            // посмотрит на строку R4/PT1M и отложит задачу ровно на 1 минуту.
            throw new RuntimeException("Error técnico temporal con Bizum, reintentando...", e);
        }
    }
}

