package br.tcc.pega.config;

import br.tcc.pega.agent.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Inicializa o container JADE e registra todos os agentes ao subir o Spring Boot.
 *
 * Porta:  JADE usa 1099 (RMI/IIOP)
 *         Spring Boot usa 8080 (HTTP)
 *         → sem conflito
 *
 * GUI do JADE desabilitada (false) para não depender de jade-tools.jar.
 * Em desenvolvimento, pode-se setar JADE_GUI=true e incluir jade-tools.jar.
 */
@Configuration
public class AgentConfig {

    private static final Logger log = LoggerFactory.getLogger(AgentConfig.class);

    @Value("${pega.jade.host:localhost}")
    private String jadeHost;

    @Value("${pega.jade.port:1099}")
    private String jadePort;

    private AgentContainer mainContainer;

    /** Mapa name → AgentController, usado pelo JadeAgentService para enviar mensagens O2A */
    @Getter
    private final Map<String, AgentController> agentControllers = new HashMap<>();

    @PostConstruct
    public void startJade() {
        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, jadeHost);
            profile.setParameter(Profile.MAIN_PORT, jadePort);
            profile.setParameter(Profile.GUI, "false");

            mainContainer = rt.createMainContainer(profile);
            log.info("Container JADE iniciado em {}:{}", jadeHost, jadePort);

            // Ordem de inicialização: agentes de suporte antes do orquestrador
            registerAgent("student",      StudentAgent.class);
            registerAgent("content",      ContentAgent.class);
            registerAgent("assessment",   AssessmentAgent.class);
            registerAgent("progress",     ProgressAgent.class);
            registerAgent("adaptation",   AdaptationAgent.class);
            registerAgent("orchestrator", OrchestratorAgent.class); // último — já pode chamar os outros

            log.info("Todos os agentes JADE iniciados com sucesso.");

        } catch (Exception e) {
            log.error("Falha ao iniciar container JADE: {}. " +
                    "O endpoint /api/agent/play-game ficará indisponível.", e.getMessage(), e);
        }
    }

    private void registerAgent(String name, Class<? extends jade.core.Agent> agentClass) throws Exception {
        AgentController ctrl = mainContainer.createNewAgent(name, agentClass.getName(), null);
        ctrl.start();
        agentControllers.put(name, ctrl);
        log.debug("Agente registrado: '{}'", name);
    }

    @PreDestroy
    public void stopJade() {
        if (mainContainer != null) {
            try {
                mainContainer.kill();
                log.info("Container JADE encerrado.");
            } catch (Exception e) {
                log.warn("Erro ao encerrar JADE (ignorado): {}", e.getMessage());
            }
        }
    }
}
