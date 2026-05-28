package br.tcc.pega.agent;

import br.tcc.pega.dto.ActivityDto;
import br.tcc.pega.entity.Activity;
import br.tcc.pega.repository.ActivityRepository;
import br.tcc.pega.util.SpringContextHolder;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Seleciona atividades de alfabetização adequadas ao perfil do aluno.
 *
 * Recebe: {studentId, dificuldade}
 * Retorna: ActivityDto da atividade selecionada
 *
 * Estratégia de seleção:
 *  1. Busca atividades ativas com a dificuldade exata
 *  2. Se não encontrar, retorna qualquer atividade ativa
 *  3. Seleção aleatória entre os candidatos (evita repetição imediata)
 */
public class ContentAgent extends BaseAgent {

    private static final Random RANDOM = new Random();

    @Override
    protected void setup() {
        log.info("ContentAgent iniciado: {}", getAID().getName());
        addBehaviour(new SelectActivityBehaviour());
    }

    private class SelectActivityBehaviour extends CyclicBehaviour {

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
                int dificuldade = params.containsKey("dificuldade")
                        ? ((Number) params.get("dificuldade")).intValue()
                        : 1;

                ActivityRepository repo = SpringContextHolder.getBean(ActivityRepository.class);

                List<Activity> candidatos = repo.findByDificuldade(dificuldade).stream()
                        .filter(a -> Boolean.TRUE.equals(a.getAtivo()))
                        .toList();

                if (candidatos.isEmpty()) {
                    candidatos = repo.findByAtivoTrue(); // fallback: qualquer atividade ativa
                }

                if (candidatos.isEmpty()) {
                    replyFailure(msg, "Nenhuma atividade disponível no banco de dados.");
                    return;
                }

                Activity escolhida = candidatos.get(RANDOM.nextInt(candidatos.size()));
                ActivityDto dto = toDto(escolhida);

                log.debug("ContentAgent selecionou: '{}' (dif={})", escolhida.getNome(), dificuldade);
                replyInform(msg, JsonUtil.toJson(dto));

            } catch (Exception e) {
                log.error("ContentAgent erro: {}", e.getMessage());
                replyFailure(msg, e.getMessage());
            }
        }

        private ActivityDto toDto(Activity a) {
            return ActivityDto.builder()
                    .id(a.getId())
                    .nome(a.getNome())
                    .tipo(a.getTipo().name())
                    .descricao(a.getDescricao())
                    .dificuldade(a.getDificuldade())
                    .ativo(a.getAtivo())
                    .build();
        }
    }
}
