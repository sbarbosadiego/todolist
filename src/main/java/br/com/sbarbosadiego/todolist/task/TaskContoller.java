package br.com.sbarbosadiego.todolist.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tarefa")
public class TaskContoller {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/criar")
    public TaskModel criarTarefa(@RequestBody TaskModel taskModel) {
        var tarefa = this.taskRepository.save(taskModel);
        return tarefa;
    }
    
}
