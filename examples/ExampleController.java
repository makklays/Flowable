import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * Example of end-point and start a new BPMN process with variables 
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 14.07.2026
 */
@RestController
public class ExampleController {

    @Autowired
    private RuntimeService runtimeService; // Внедряем сервис

    @PostMapping("/start-process")
    public String startProcess() {
        // 1. Готовим переменные процесса (Process Variables)
        Map<String, Object> variables = new HashMap<>();
        variables.put("amount", 5000);

        // 2. Запускаем процесс по его ключу и передаем переменные
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "creditApprovalProcess", 
            variables
        );

        // Возвращаем ID запущенного экземпляра процесса
        return "Process started with ID: " + processInstance.getId();
    }
}

