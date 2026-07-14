import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/*
 * Example of Java Delegate on step Service Task (for example, check Credit History)
 *
 * Если мне нужно передать тяжелый JSON-документ из Service Task 1 в Service Task 2, которые идут друг за другом в одной транзакции, 
 * я использую setTransientVariable(). Это экономит ресурсы БД и ускоряет процесс. 
 * Но если мне нужно сохранить данные для истории или вывести их пользователю на следующем шаге в User Task, я использую обычный setVariable().
 * 
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 14.07.2026
 */
@Component("checkCreditHistory") // Имя бина для BPMN схемы
public class CheckCreditHistoryDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Здесь пишется бизнес-логика шага
        
        // 1. Как достать переменную из процесса?
        Integer loanAmount = (Integer) execution.getVariable("amount");
        System.out.println("Проверяем лимит для суммы: " + loanAmount);

        // 2. Как записать новый результат обратно в процесс?
        execution.setVariable("isApproved", true);

        // Делает переменную локальной для конкретной задачи или подпроцесса. 
        // Она сохраняется в БД, но исчезает, как только завершается этот конкретный элемент схемы.
        // execution.setVariableLocal("isApproved", true); 

        // Cоздает переменную, которая живет исключительно в оперативной памяти и никогда не пишется в базу данных. 
        // Она уничтожается автоматически, как только текущая транзакция делает COMMIT (достигает ближайшего Wait State).
        // execution.setTransientVariable("isApproved", true); 
    }
}

