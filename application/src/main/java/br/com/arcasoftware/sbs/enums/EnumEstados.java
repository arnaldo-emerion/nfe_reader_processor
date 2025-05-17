package br.com.arcasoftware.sbs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum EnumEstados {
    RONDONIA(11, "R0"),
    ACRE(12, "AC"),
    AMAZONAS(13, "AM"),
    RORAIMA(14, "RR"),
    PARA(15, "PA"),
    AMAPA(16, "AP"),
    TOCANTINS(17, "TO"),
    MARANHAO(21, "MA"),
    PIAUI(22, "PI"),
    CEARA(23, "CE"),
    RIO_GRANDE_DO_NORTE(24, "RN"),
    PARAIBA(25, "PB"),
    PERNAMBUCO(26, "PE"),
    ALAGOAS(27, "AL"),
    SERGIPE(28, "SE"),
    BAHIA(29, "BA"),
    MINAS_GERAIS(31, "MG"),
    ESPIRITO_SANTO(32, "ES"),
    RIO_DE_JANEIRO(33, "RJ"),
    SAO_PAULO(35, "SP"),
    PARANA(41, "PR"),
    SANTA_CATARINA(42, "SC"),
    RIO_GRANDE_DO_SUL(43, "RS"),
    MATO_GROSSO_DO_SUL(50, "MS"),
    MATO_GROSSO(51, "MT"),
    GOIAS(52, "GO"),
    DISTRITO_FEDERAL(53, "DF");

    private final int codigo;
    private final String sigla;

    public static String getSiglaFromCodigo(int codigo) {
        Optional<EnumEstados> estados = Arrays.stream(values()).filter(it -> it.codigo == codigo).findFirst();
        return estados.map(value -> value.sigla).orElse(null);
    }
}
