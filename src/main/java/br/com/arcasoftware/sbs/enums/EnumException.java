package br.com.arcasoftware.sbs.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum EnumException {
    INTERNAL_SERVER_ERROR("Um erro ocorreu ao tentar processar a requisição. Por favor tente novamente mais tarde", HttpStatus.INTERNAL_SERVER_ERROR),
    NFE_NOT_SUPPORTED("Nota fiscal não suportada para processamento", HttpStatus.BAD_REQUEST),
    INVALID_XML_PASSED_IN("Arquivo XML mal formatado ou incompleto", HttpStatus.BAD_REQUEST),
    NFE_ALREADY_PROCESSED("Arquivo NFe já processado anteriormente", HttpStatus.BAD_REQUEST),
    FORMATO_DE_DATA_NAO_RECONHECIDO("Formato de data não reconhecido", HttpStatus.BAD_REQUEST),
    NFE_NAO_ENCONTRADA("Arquivo NFe não encontrado. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    TRANSPORTADORA_NOT_FOUND("Transportadora não encontrada. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    EMITENTE_NOT_FOUND("Emitente não encontrado. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    CLIENTE_NAO_ENCONTRADO("Cliente não encontrado. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    PRODUTO_NAO_ENCONTRADO("Produto não encontrado. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    DESTINATARIO_NAO_ENCONTRADO("Destinatário não encontrado. Por favor verifique o identificador fornecido", HttpStatus.NOT_FOUND),
    NFE_WITHOUT_PROTOCOL("Nota Fiscal não possui um protocolo válido. Apenas notas fiscais válidas são processadas", HttpStatus.BAD_REQUEST);

    private final String description;
    private final HttpStatus httpStatus;

    EnumException(String description, HttpStatus status) {
        this.description = description;
        this.httpStatus = status;
    }
}
