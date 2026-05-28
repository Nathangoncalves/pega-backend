package br.tcc.pega.agent;

import br.tcc.pega.agent.dto.AssessmentOutput;
import br.tcc.pega.dto.PlayGameRequest;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * Avalia o desempenho do aluno em uma sessão de jogo.
 *
 * Recebe: {acertos, erros, tempoMs}
 * Retorna: {pontuacao, feedback, feedbackTipo}
 *
 * Regras de feedback:
 *   acurácia ≥ 80% → POSITIVO
 *   acurácia ≥ 50% → NEUTRO
 *   acurácia ≥ 30% → ENCORAJAMENTO
 *   acurácia < 30% → NEGATIVO
 */
public class AssessmentAgent extends BaseAgent {

    @Override
    protected void setup() {
        log.info("AssessmentAgent iniciado: {}", getAID().getName());
        addBehaviour(new EvaluateBehaviour());
    }

    private class EvaluateBehaviour extends CyclicBehaviour {

        private final MessageTemplate MT = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchOntology(ONTOLOGY)
        );

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MT);
            if (msg == null) { block(); return; }

            try {
                PlayGameRequest req = JsonUtil.fromJson(msg.getContent(), PlayGameRequest.class);
                AssessmentOutput result = avaliar(req.getAcertos(), req.getErros(), req.getTempoMs());

                log.debug("Avaliação: acuracia={:.0f}%, pontuacao={}, tipo={}",
                        calcAcuracia(req.getAcertos(), req.getErros()) * 100,
                        result.getPontuacao(), result.getFeedbackTipo());

                replyInform(msg, JsonUtil.toJson(result));

            } catch (Exception e) {
                log.error("AssessmentAgent erro: {}", e.getMessage());
                replyFailure(msg, e.getMessage());
            }
        }

        private AssessmentOutput avaliar(int acertos, int erros, long tempoMs) {
            double acuracia = calcAcuracia(acertos, erros);
            int pontuacao = acertos * 10; // 10 pontos por acerto

            String feedbackTipo;
            String feedback;

            if (acuracia >= 0.8) {
                feedbackTipo = "POSITIVO";
                feedback = String.format(
                        "Excelente! Você acertou %d de %d. Continue assim!", acertos, acertos + erros);
            } else if (acuracia >= 0.5) {
                feedbackTipo = "NEUTRO";
                feedback = String.format(
                        "Bom trabalho! %d acertos. Continue praticando para melhorar ainda mais.", acertos);
            } else if (acuracia >= 0.3) {
                feedbackTipo = "ENCORAJAMENTO";
                feedback = "Não desanime! Você está aprendendo. Tente novamente com calma!";
            } else {
                feedbackTipo = "NEGATIVO";
                feedback = "Esta atividade está difícil por enquanto. Vamos praticar mais um pouco!";
            }

            return AssessmentOutput.builder()
                    .pontuacao(pontuacao)
                    .feedback(feedback)
                    .feedbackTipo(feedbackTipo)
                    .build();
        }

        private double calcAcuracia(int acertos, int erros) {
            int total = acertos + erros;
            return total == 0 ? 0.0 : (double) acertos / total;
        }
    }
}
