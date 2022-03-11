package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.ProcessamentoNFe;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoNFeRepository extends PagingAndSortingRepository<ProcessamentoNFe, Long> {

    @Modifying
    @Query(nativeQuery = true,
            value = "update processamentonfe set status = 'FINALIZADO', data_finalizacao = current_timestamp where user_create = :identityId and file_name = :fileName")
    void finalizeProcessamento(String identityId, String fileName);

    @Query(nativeQuery = true,
            value = "select count(1) from processamentonfe where user_create = :identityId and status = 'RECEBIDO'")
    int getEmProcessamento(@Param("identityId") String identityId);
}
