package com.spain.bank.flowable;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of all engines and start a new BPMN process 
 *
 * @author Alexander Kuziv <makklays@gmail.com>
 * @company TechMatrix18
 * @version 0.0.1
 * @since 15.07.2026
 */
public class FlowableBpmExample {
    public static void main(String[] args) {
        // 1. Инициализация движка Flowable с использованием in-memory базы данных H2
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1")
                .setJdbcUsername("sa")
                .setJdbcPassword("")
                .setJdbcDriver("org.h2.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        ProcessEngine processEngine = cfg.buildProcessEngine();

        // 2. Деплой (загрузка) BPMN-схемы процесса из папки ресурсов
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource("holiday-request.bpmn20.xml") // Файл разметки процесса
                .deploy();

        System.out.println("Процесс успешно деплоирован с ID: " + deployment.getId());

        // 3. Запуск экземпляра бизнес-процесса с входными переменными
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", "Иван Иванов");
        variables.put("holidays", 5);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);
        System.out.println("Запущен процесс. ID инстанса: " + processInstance.getId());

        // 4. Получение списка активных задач для менеджеров
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("Количество активных задач для менеджеров: " + tasks.size());

        for (Task task : tasks) {
            System.out.println("Выполняется задача: " + task.getName());
            // Завершение задачи с утверждением отпуска
            Map<String, Object> approvalVariables = new HashMap<>();
            approvalVariables.put("approved", true);
            taskService.complete(task.getId(), approvalVariables);
        }
        
        System.out.println("Процесс успешно завершен!");
    }
}

