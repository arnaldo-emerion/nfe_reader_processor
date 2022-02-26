package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.Transportadora;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransportadoraRepository extends PagingAndSortingRepository<Transportadora, Long> {

    Optional<Transportadora> getByUserCreateAndCnpj(String userCreate, String cnpj);
}
