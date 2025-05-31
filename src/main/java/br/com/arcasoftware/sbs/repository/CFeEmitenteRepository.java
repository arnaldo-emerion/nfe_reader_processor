package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitenteModel;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CFeEmitenteRepository extends PagingAndSortingRepository<CFeEmitenteModel, Long> {

    Optional<CFeEmitenteModel> getByUserCreateAndCnpj(String userCreate, String cnpj);
}
