package br.tcc.pega.agent;

import br.tcc.pega.agent.dto.ProgressInput;
import br.tcc.pega.agent.dto.ProgressOutput;
import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.entity.*;
import br.tcc.pega.repository.*;
import br.tcc.pega.service.StudentService;
import br.tcc.pega.util.SpringContextHolder;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.time.LocalDateTime;

/**
 * Persiste os resultados de jogo no banco de dados e atualiza o score do aluno.
 *
 * Recebe: ProgressInput {sessionId, studentId, activityId, acertos, erros, tempoMs, pontuacao, feedback, feedbackTipo}
 * Retorna: ProgressOutput {gameResultId, novoScore, novoNivel}
 *
 * Ações realizadas (em sequência):
 *  1. Cria e persiste GameResult
 *  2. Cria e persiste Feedback vinculado ao resultado
 *  3. Chama StudentService.updateScore() para atualizar score e nível
 */
public class ProgressAgent extends BaseAgent {

    @Override
    protected void setup() {
        log.info("ProgressAgent iniciado: {}", getAID().getName());
        addBehaviour(new SaveBehaviour());
    }

    private class SaveBehaviour extends CyclicBehaviour {

        private final MessageTemplate MT = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchOntology(ONTOLOGY)
        );

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MT);
            if (msg == null) { block(); return; }

            try {
                ProgressInput input = JsonUtil.fromJson(msg.getContent(), ProgressInput.class);
                ProgressOutput output = persistir(input);

                log.info("ProgressAgent: sessão={} salva — gameResultId={}, novoScore={}",
                        input.getSessionId(), output.getGameResultId(), output.getNovoScore());

                replyInform(msg, JsonUtil.toJson(output));

            } catch (Exception e) {
                log.error("ProgressAgent erro ao persistir: {}", e.getMessage(), e);
                replyFailure(msg, e.getMessage());
            }
        }

        private ProgressOutput persistir(ProgressInput input) {
            StudentRepository  studentRepo  = SpringContextHolder.getBean(StudentRepository.class);
            ActivityRepository activityRepo = SpringContextHolder.getBean(ActivityRepository.class);
            GameResultRepository resultRepo = SpringContextHolder.getBean(GameResultRepository.class);
            FeedbackRepository  feedbackRepo = SpringContextHolder.getBean(FeedbackRepository.class);
            StudentService      studentSvc   = SpringContextHolder.getBean(StudentService.class);

            Student student = studentRepo.findById(input.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + input.getStudentId()));
            Activity activity = activityRepo.findById(input.getActivityId())
                    .orElseThrow(() -> new RuntimeException("Atividade não encontrada: " + input.getActivityId()));

            // 1. Salvar resultado da sessão
            GameResult result = GameResult.builder()
                    .student(student)
                    .activity(activity)
                    .acertos(input.getAcertos())
                    .erros(input.getErros())
                    .tempoMs(input.getTempoMs())
                    .timestamp(LocalDateTime.now())
                    .build();
            result = resultRepo.save(result);

            // 2. Salvar feedback gerado pelo AssessmentAgent
            Feedback feedback = Feedback.builder()
                    .gameResult(result)
                    .mensagem(input.getFeedback())
                    .tipo(Feedback.FeedbackType.valueOf(input.getFeedbackTipo()))
                    .build();
            feedbackRepo.save(feedback);

            // 3. Atualizar score e nível do aluno
            StudentDto updated = studentSvc.updateScore(student.getId(), input.getPontuacao());

            return ProgressOutput.builder()
                    .gameResultId(result.getId())
                    .novoScore(updated.getScoreTotal())
                    .novoNivel(updated.getNivelAtual())
                    .build();
        }
    }
}
