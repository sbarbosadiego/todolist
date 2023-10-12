package br.com.sbarbosadiego.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.sbarbosadiego.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        if (servletPath.equals("/tarefa/criar") || servletPath.equals("/tarefa/listar") || servletPath.startsWith("/tarefa/")) {
            // Pega a informação para autenticação
            var authorization = request.getHeader("Authorization");
            var authorizationEncoded = authorization.substring("Basic".length()).trim();
            byte[] authorizationDecode = Base64.getDecoder().decode(authorizationEncoded);
            var authorizationString = new String(authorizationDecode);
            String[] credencial = authorizationString.split(":");
            String username = credencial[0];
            String password = credencial[1];

            // Validar usuário
            var usuario = this.userRepository.findByUserName(username);
            if (usuario == null) {
                response.sendError(401);
            } else {
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), usuario.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", usuario.getIdUser());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }

        } else {
            filterChain.doFilter(request, response);
        }

    }

}
