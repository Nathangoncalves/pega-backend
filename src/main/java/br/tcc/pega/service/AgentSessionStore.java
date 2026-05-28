package br.tcc.pega.service;

import br.tcc.pega.dto.PlayGameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ponte thread-safe entre os controllers Spring e os agentes JADE.
 *
 * Fluxo:
 *  1. AgentController cria uma sessão (CompletableFuture) e aguarda.
 *  2. OrchestratorAgent processa o pipeline e chama complete() ao final.
 *  3. O future é removido do mapa após ser completado.
 */
@Component
public class AgentSessionStore {

    private static final Logger log = LoggerFactory.getLogger(AgentSessionStore.class);

    private final ConcurrentHashMap<String, CompletableFuture<PlayGameResponse>> sessions =
            new ConcurrentHashMap<>();

    public CompletableFuture<PlayGameResponse> createSession(String sessionId) {
        CompletableFuture<PlayGameResponse> future = new CompletableFuture<>();
        sessions.put(sessionId, future);
        return future;
    }

    public void complete(String sessionId, PlayGameResponse result) {
        CompletableFuture<PlayGameResponse> future = sessions.remove(sessionId);
        if (future != null) {
            future.complete(result);
        } else {
            log.warn("complete() chamado para sessão inexistente/expirada: {}", sessionId);
        }
    }

    public void completeExceptionally(String sessionId, Throwable ex) {
        CompletableFuture<PlayGameResponse> future = sessions.remove(sessionId);
        if (future != null) {
            future.completeExceptionally(ex);
        }
    }
}
