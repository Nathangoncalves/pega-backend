package br.tcc.pega.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe base para todos os agentes JADE do projeto PEGA.
 *
 * Fornece:
 * - Logger SLF4J com nome da subclasse
 * - Constantes compartilhadas (ontologia, timeout)
 * - requestAndWait(): envia REQUEST e aguarda INFORM de resposta (síncrono, FIPA-like)
 * - replyInform() / replyFailure(): helpers para responder mensagens recebidas
 */
public abstract class BaseAgent extends Agent {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected static final long   TIMEOUT_MS = 5_000L;
    protected static final String ONTOLOGY   = "pega-game";

    /**
     * Envia um REQUEST ACL para o agente nomeado e aguarda o INFORM de resposta.
     * Usa replyWith/inReplyTo para correlacionar a resposta correta.
     *
     * @return conteúdo JSON da resposta, ou null se timeout
     */
    protected String requestAndWait(String receiverName, String contentJson) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(receiverName, AID.ISLOCALNAME));
        msg.setOntology(ONTOLOGY);
        msg.setContent(contentJson);

        String replyWith = receiverName + "-" + System.nanoTime();
        msg.setReplyWith(replyWith);
        send(msg);

        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchInReplyTo(replyWith),
                MessageTemplate.MatchPerformative(ACLMessage.INFORM)
        );
        ACLMessage reply = blockingReceive(mt, TIMEOUT_MS);

        if (reply == null) {
            log.warn("Timeout ({}ms) aguardando resposta de '{}'", TIMEOUT_MS, receiverName);
        }
        return reply != null ? reply.getContent() : null;
    }

    /** Responde a uma mensagem REQUEST com INFORM (sucesso) */
    protected void replyInform(ACLMessage request, String contentJson) {
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent(contentJson);
        send(reply);
    }

    /** Responde a uma mensagem REQUEST com FAILURE (erro) */
    protected void replyFailure(ACLMessage request, String reason) {
        ACLMessage reply = request.createReply();
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent(reason);
        send(reply);
    }
}
