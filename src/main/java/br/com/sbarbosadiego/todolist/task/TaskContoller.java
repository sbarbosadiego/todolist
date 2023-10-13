package br.com.sbarbosadiego.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.sbarbosadiego.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tarefa")
public class TaskContoller {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/criar")
    public ResponseEntity criarTarefa(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var  idUser = request.getAttribute("idUser");
        taskModel.setIdUser((UUID) idUser);

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de ínicio e término deve ser superior a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de ínicio deve ser menor que data de término");
        }

        var tarefa = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(taskModel);
    }

    @GetMapping("/listar")
    public List<TaskModel> listar(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tarefas = this.taskRepository.findByIdUser((UUID) idUser);
        return tarefas;
    }

    @PutMapping("/{id}")
    public TaskModel atualizarTarefa(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tarefa = this.taskRepository.findById(id).orElse(null);
        
        Utils.copyNonNullProperties(taskModel, tarefa);

        return this.taskRepository.save(tarefa);
    }
    
}
