package br.tcc.pega.controller;

import br.tcc.pega.dto.PlayGameRequest;
import br.tcc.pega.dto.PlayGameResponse;
import br.tcc.pega.service.JadeAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeoutException;

/**
 * Endpoint que aciona o pipeline completo de agentes JADE.
 *
 * POST /api/agent/play-game
 *   → OrchestratorAgent coordena: assessment → persistence → adaptation → content
 *   ← retorna score atualizado, feedback e próxima atividade recomendada
 */
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@Tag(name = "Agentes JADE", description = "Pipeline multiagente de avaliação de sessões de jogo")
@SecurityRequirement(name = "bearerAuth")
public class AgentController {

    private final JadeAgentService jadeAgentService;

    @PostMapping("/play-game")
    @Operation(
        summary = "Processar sessão de jogo via pipeline JADE",
        description = """
            Aciona o pipeline de 4 agentes:
            1. **AssessmentAgent** — avalia desempenho e gera feedback
            2. **ProgressAgent**   — persiste resultado no banco e atualiza score/nível
            3. **AdaptationAgent** — calcula dificuldade recomendada com base no histórico
            4. **ContentAgent**    — seleciona a próxima atividade adequada
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pipeline executado com sucesso",
                content = @Content(schema = @Schema(implementation = PlayGameResponse.class))),
        @ApiResponse(responseCode = "504", description = "Timeout — agentes não responderam em 8 segundos"),
        @ApiResponse(responseCode = "500", description = "Erro interno no pipeline multiagente")
    })
    public ResponseEntity<?> playGame(@Valid @RequestBody PlayGameRequest request) {
        try {
            PlayGameResponse response = jadeAgentService.executeGameSession(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (TimeoutException e) {
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                    .body("Timeout: o pipeline multiagente não respondeu em 8 segundos.");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Serviço JADE indisponível: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro no processamento multiagente: " + e.getMessage());
        }
    }
}
