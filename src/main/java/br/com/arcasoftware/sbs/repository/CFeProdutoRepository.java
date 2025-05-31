package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProdutoModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CFeProdutoRepository extends PagingAndSortingRepository<CFeProdutoModel, Long> {
    Optional<CFeProdutoModel> getByUserCreateAndCodigo(String userCreate, String codigo);
}
