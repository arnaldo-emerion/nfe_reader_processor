package br.com.arcasoftware.sbs.business;

import br.com.arcasoftware.sbs.enums.NFeTypeEnum;

import java.util.Arrays;
import java.util.List;

public class ValidNFeTypes {

    public List<NFeTypeEnum> getAllValidNFeTypes() {
        return Arrays.asList(NFeTypeEnum.VENDA);
    }
}
