package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscalModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CFeRepository extends PagingAndSortingRepository<CupomFiscalModel, Long> {

    List<CupomFiscalModel> findByUserCreateAndChaveCFe(String userCreate, String chaveNFe);

}
