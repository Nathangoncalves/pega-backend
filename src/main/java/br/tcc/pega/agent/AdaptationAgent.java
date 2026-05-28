package br.tcc.pega.agent;

import br.tcc.pega.agent.dto.AdaptationOutput;
import br.tcc.pega.entity.GameResult;
import br.tcc.pega.entity.Student;
import br.tcc.pega.repository.GameResultRepository;
import br.tcc.pega.repository.StudentRepository;
import br.tcc.pega.util.SpringContextHolder;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;
import java.util.Map;

/**
 * Analisa o histórico do aluno e recomenda a dificuldade para a próxima atividade.
 *
 * Recebe: {studentId}
 * Retorna: {dificuldadeRecomendada}
 *
 * Algoritmo de adaptação (baseado nas últimas 5 sessões):
 *   acurácia média ≥ 80% → aumenta dificuldade (máx. 5)
 *   acurácia média < 35% → diminui dificuldade (mín. 1)
 *   caso contrário        → mantém dificuldade atual
 */
public class AdaptationAgent extends BaseAgent {

    @Override
    protected void setup() {
        log.info("AdaptationAgent iniciado: {}", getAID().getName());
        addBehaviour(new AdaptBehaviour());
    }

    private class AdaptBehaviour extends CyclicBehaviour {

        private final MessageTemplate MT = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchOntology(ONTOLOGY)
        );

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MT);
            if (msg == null) { block(); return; }

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> params = JsonUtil.fromJson(msg.getContent(), Map.class);
                Long studentId = ((Number) params.get("studentId")).longValue();

                int dificuldade = calcularDificuldade(studentId);
                AdaptationOutput output = new AdaptationOutput(dificuldade);

                log.debug("AdaptationAgent → studentId={}, dificuldadeRecomendada={}", studentId, dificuldade);
                replyInform(msg, JsonUtil.toJson(output));

            } catch (Exception e) {
                log.error("AdaptationAgent erro: {}", e.getMessage());
                replyFailure(msg, e.getMessage());
            }
        }

        private int calcularDificuldade(Long studentId) {
            StudentRepository studentRepo = SpringContextHolder.getBean(StudentRepository.class);
            GameResultRepository resultRepo = SpringContextHolder.getBean(GameResultRepository.class);

            Student student = studentRepo.findById(studentId).orElse(null);
            if (student == null) return 1;

            List<GameResult> recentes = resultRepo
                    .findByStudentIdOrderByTimestampDesc(studentId)
                    .stream().limit(5).toList();

            if (recentes.isEmpty()) return student.getNivelAtual();

            double acuraciaMedia = recentes.stream()
                    .mapToDouble(r -> {
                        int total = r.getAcertos() + r.getErros();
                        return total == 0 ? 0.0 : (double) r.getAcertos() / total;
                    })
                    .average()
                    .orElse(0.5);

            int nivel = student.getNivelAtual();
            if (acuraciaMedia >= 0.8 && nivel < 5) nivel++;
            else if (acuraciaMedia < 0.35 && nivel > 1) nivel--;

            log.debug("Aluno {}: acurácia média={:.0f}%, nível atual={}, nível recomendado={}",
                    studentId, acuraciaMedia * 100, student.getNivelAtual(), nivel);

            return nivel;
        }
    }
}
