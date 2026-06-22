package br.tcc.pega.service;

import br.tcc.pega.dto.GameResultDto;
import br.tcc.pega.entity.GameResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GameResultService extends CrudService<GameResult, GameResultDto> {

    Page<GameResultDto> findByStudentId(UUID studentId, Pageable pageable);
}
