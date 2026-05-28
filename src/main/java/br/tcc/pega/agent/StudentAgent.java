package br.tcc.pega.agent;

import br.tcc.pega.dto.StudentDto;
import br.tcc.pega.service.StudentService;
import br.tcc.pega.util.SpringContextHolder;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Map;

/**
 * Representa o aluno no sistema multiagentes.
 * Fornece o perfil atualizado do estudante (nível, score) para outros agentes.
 *
 * Recebe: {studentId}
 * Retorna: StudentDto
 */
public class StudentAgent extends BaseAgent {

    @Override
    protected void setup() {
        log.info("StudentAgent iniciado: {}", getAID().getName());
        addBehaviour(new ProfileBehaviour());
    }

    private class ProfileBehaviour extends CyclicBehaviour {

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

                StudentService studentService = SpringContextHolder.getBean(StudentService.class);
                StudentDto student = studentService.findById(studentId);

                log.debug("StudentAgent: perfil aluno id={}, nivel={}, score={}",
                        studentId, student.getNivelAtual(), student.getScoreTotal());

                replyInform(msg, JsonUtil.toJson(student));

            } catch (Exception e) {
                log.error("StudentAgent erro: {}", e.getMessage());
                replyFailure(msg, e.getMessage());
            }
        }
    }
}
