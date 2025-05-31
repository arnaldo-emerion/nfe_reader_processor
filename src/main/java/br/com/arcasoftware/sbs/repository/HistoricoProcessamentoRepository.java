package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.HistoricoProcessamento;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoProcessamentoRepository extends PagingAndSortingRepository<HistoricoProcessamento, Long> {

}
