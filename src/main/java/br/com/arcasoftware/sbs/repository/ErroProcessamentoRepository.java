package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErroProcessamentoRepository extends PagingAndSortingRepository<ErroProcessamento, Long> {

}
