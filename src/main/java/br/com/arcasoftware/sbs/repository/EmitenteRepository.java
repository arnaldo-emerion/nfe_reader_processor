package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.Emitente;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmitenteRepository extends PagingAndSortingRepository<Emitente, Long> {

    Optional<Emitente> getByUserCreateAndCnpj(String userCreate, String cnpj);
}
