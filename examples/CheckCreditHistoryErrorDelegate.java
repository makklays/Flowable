
/**
 * Example of Java Delegate on step Service Task (for example, check Credit History with BpmnError)
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 14.07.2026
 */ 
@Component("checkCreditHistoryError")
public class CheckCreditHistoryErrorDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        boolean clientIsUnreliable = true; // Допустим, проверка провалена

        // Транзакция НЕ откатывается. Движок ловит эту ошибку и направляет процесс 
        // на специальный элемент схемы — Error Boundary Event (Граничное событие ошибки), которое нарисовано на схеме.
      
        if (clientIsUnreliable) {
            // Выбрасываем BpmnError и передаем код ошибки, который указан на BPMN-схеме
            throw new BpmnError("CREDIT_REJECTED_CODE");
        }
    }
}
