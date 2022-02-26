package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.Emitente;
import br.com.arcasoftware.sbs.model.nfe.Produto;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends PagingAndSortingRepository<Produto, Long> {

    Optional<Produto> getByUserCreateAndCodigo(String userCreate, String codigo);

    List<Produto> getByUserCreateAndEmitente(String userEmitente, Emitente emitente);

}
