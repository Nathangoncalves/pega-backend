package br.tcc.pega.service;

import br.tcc.pega.config.AgentConfig;
import br.tcc.pega.dto.PlayGameRequest;
import br.tcc.pega.dto.PlayGameResponse;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * Ponte entre os controllers Spring e o OrchestratorAgent JADE.
 *
 * Fluxo:
 *  1. Gera sessionId único
 *  2. Registra CompletableFuture no AgentSessionStore
 *  3. Deposita PlayGameRequest na fila O2A do OrchestratorAgent (não-bloqueante)
 *  4. Aguarda o future por até TIMEOUT_SECONDS (enquanto o pipeline JADE processa)
 *  5. Retorna PlayGameResponse ou lança TimeoutException / ExecutionException
 */
@Service
@RequiredArgsConstructor
public class JadeAgentService {

    private static final Logger log = LoggerFactory.getLogger(JadeAgentService.class);
    private static final long TIMEOUT_SECONDS = 8;

    private final AgentConfig agentConfig;
    private final AgentSessionStore sessionStore;

    public PlayGameResponse executeGameSession(PlayGameRequest request) throws Exception {
        String sessionId = UUID.randomUUID().toString();
        request.setSessionId(sessionId);

        CompletableFuture<PlayGameResponse> future = sessionStore.createSession(sessionId);

        AgentController orchestrator = agentConfig.getAgentControllers().get("orchestrator");
        if (orchestrator == null) {
            sessionStore.completeExceptionally(sessionId,
                    new IllegalStateException("OrchestratorAgent não disponível — JADE iniciou corretamente?"));
            throw new IllegalStateException("OrchestratorAgent não disponível.");
        }

        try {
            // Entrega O2A não-bloqueante: o agente processa em sua própria thread
            orchestrator.putO2AObject(request, AgentController.ASYNC);
            log.debug("Requisição enviada ao OrchestratorAgent, sessionId={}", sessionId);
        } catch (StaleProxyException e) {
            sessionStore.completeExceptionally(sessionId, e);
            throw e;
        }

        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            sessionStore.completeExceptionally(sessionId, e);
            log.warn("Timeout ({}s) na sessão {}: pipeline JADE não concluiu a tempo.", TIMEOUT_SECONDS, sessionId);
            throw e;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw (cause instanceof Exception) ? (Exception) cause : new RuntimeException(cause);
        }
    }
}
