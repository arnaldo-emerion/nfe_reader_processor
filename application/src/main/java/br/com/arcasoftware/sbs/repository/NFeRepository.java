package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.NFe;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NFeRepository extends PagingAndSortingRepository<NFe, Long> {

    List<NFe> findByUserCreateAndChaveNFe(String userCreate, String chaveNFe);

}
