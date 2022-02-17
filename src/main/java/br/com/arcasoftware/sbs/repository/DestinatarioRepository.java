package br.com.arcasoftware.sbs.repository;

import br.com.arcasoftware.sbs.model.nfe.Destinatario;
import br.com.arcasoftware.sbs.model.nfe.Emitente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinatarioRepository extends JpaRepository<Destinatario, Long> {

    Optional<Destinatario> getByCnpj(String cnpj);

    Optional<Destinatario> getByEmitenteAndCnpj(Emitente emitente, String cnpj);

}
