package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatarioModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CFeDestinatarioRepository extends PagingAndSortingRepository<CFeDestinatarioModel, Long> {

    Optional<CFeDestinatarioModel> getByUserCreateAndCpf(String userCreate, String cpf);
}
