package br.tcc.pega.agent;

import br.tcc.pega.agent.dto.AdaptationOutput;
import br.tcc.pega.agent.dto.AssessmentOutput;
import br.tcc.pega.agent.dto.ProgressInput;
import br.tcc.pega.agent.dto.ProgressOutput;
import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.dto.PlayGameRequest;
import br.tcc.pega.dto.PlayGameResponse;
import br.tcc.pega.service.AgentSessionStore;
import br.tcc.pega.util.SpringContextHolder;
import jade.core.behaviours.CyclicBehaviour;

import java.util.Map;

/**
 * Agente orquestrador — maestro do pipeline de avaliação.
 *
 * Recebe requisições do Spring via O2A (Object-to-Agent) e coordena:
 *   1. AssessmentAgent  → avalia desempenho e gera feedback
 *   2. ProgressAgent    → persiste resultado e atualiza score/nível
 *   3. AdaptationAgent  → calcula dificuldade recomendada
 *   4. ContentAgent     → seleciona próxima atividade
 *
 * Ao final, completa o CompletableFuture no AgentSessionStore,
 * desbloqueando o controller Spring que estava aguardando.
 *
 * Comunicação com os agentes especializados: FIPA Request/Inform via ACL,
 * com correlação por replyWith/inReplyTo (blockingReceive, timeout 5s cada).
 */
public class OrchestratorAgent extends BaseAgent {

    @Override
    protected void setup() {
        log.info("OrchestratorAgent iniciado: {}", getAID().getName());
        setEnabledO2ACommunication(true, 0); // fila O2A sem limite de buffer
        addBehaviour(new OrchestrateBehaviour());
    }

    // =====================================================================
    // Behaviour principal
    // =====================================================================

    private class OrchestrateBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            PlayGameRequest req = (PlayGameRequest) myAgent.getO2AObject();
            if (req == null) { block(); return; }

            log.info("Iniciando pipeline: sessionId={}, studentId={}, activityId={}",
                    req.getSessionId(), req.getStudentId(), req.getActivityId());

            AgentSessionStore store = SpringContextHolder.getBean(AgentSessionStore.class);

            try {
                // 1. Avaliação de desempenho
                AssessmentOutput assessment = callAssessment(req);
                if (assessment == null) throw new RuntimeException("Timeout: AssessmentAgent sem resposta.");

                // 2. Persistência no BD
                ProgressOutput progress = callProgress(req, assessment);
                if (progress == null) throw new RuntimeException("Timeout: ProgressAgent sem resposta.");

                // 3. Adaptação de dificuldade (não-bloqueante se timeout — usa fallback)
                AdaptationOutput adaptation = callAdaptation(req.getStudentId());
                int dificuldade = adaptation != null ? adaptation.getDificuldadeRecomendada() : 1;

                // 4. Seleção da próxima atividade
                ActivityDto proxima = callContent(req.getStudentId(), dificuldade);

                // 5. Montar resposta final
                PlayGameResponse response = PlayGameResponse.builder()
                        .gameResultId(progress.getGameResultId())
                        .acertos(req.getAcertos())
                        .erros(req.getErros())
                        .tempoMs(req.getTempoMs())
                        .novoScore(progress.getNovoScore())
                        .novoNivel(progress.getNovoNivel())
                        .feedback(assessment.getFeedback())
                        .feedbackTipo(assessment.getFeedbackTipo())
                        .proximaAtividade(proxima)
                        .build();

                store.complete(req.getSessionId(), response);
                log.info("Pipeline concluído: sessionId={}, novoScore={}, novoNivel={}",
                        req.getSessionId(), progress.getNovoScore(), progress.getNovoNivel());

            } catch (Exception e) {
                log.error("Erro no pipeline sessionId={}: {}", req.getSessionId(), e.getMessage(), e);
                store.completeExceptionally(req.getSessionId(), e);
            }
        }

        // ---- Chamadas para agentes especializados ----

        private AssessmentOutput callAssessment(PlayGameRequest req) {
            String json = requestAndWait("assessment", JsonUtil.toJson(req));
            return json != null ? JsonUtil.fromJson(json, AssessmentOutput.class) : null;
        }

        private ProgressOutput callProgress(PlayGameRequest req, AssessmentOutput assessment) {
            // Serializa lista de palavras para JSON (pode ser null se frontend antigo)
            String detalhesPalavrasJson = null;
            if (req.getPalavras() != null && !req.getPalavras().isEmpty()) {
                detalhesPalavrasJson = JsonUtil.toJson(req.getPalavras());
            }

            ProgressInput input = ProgressInput.builder()
                    .sessionId(req.getSessionId())
                    .studentId(req.getStudentId())
                    .activityId(req.getActivityId())
                    .acertos(req.getAcertos())
                    .erros(req.getErros())
                    .tempoMs(req.getTempoMs())
                    .pontuacao(assessment.getPontuacao())
                    .feedback(assessment.getFeedback())
                    .feedbackTipo(assessment.getFeedbackTipo())
                    .detalhesPalavras(detalhesPalavrasJson)
                    .build();
            String json = requestAndWait("progress", JsonUtil.toJson(input));
            return json != null ? JsonUtil.fromJson(json, ProgressOutput.class) : null;
        }

        private AdaptationOutput callAdaptation(Long studentId) {
            String json = requestAndWait("adaptation",
                    JsonUtil.toJson(Map.of("studentId", studentId)));
            return json != null ? JsonUtil.fromJson(json, AdaptationOutput.class) : null;
        }

        private ActivityDto callContent(Long studentId, int dificuldade) {
            String json = requestAndWait("content",
                    JsonUtil.toJson(Map.of("studentId", studentId, "dificuldade", dificuldade)));
            return json != null ? JsonUtil.fromJson(json, ActivityDto.class) : null;
        }
    }
}
