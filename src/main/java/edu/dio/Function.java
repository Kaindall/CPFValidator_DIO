package edu.dio;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("validator")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Iniciando processo de validação de CPF.");
        
        System.out.println(request.getQueryParameters());
        final String cpf = request.getQueryParameters().get("cpf");

        if (cpf == null) {return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Favor passar o CPF como um query-parameter.").build();}
        if (isValidCPF(cpf)) {
            return request.createResponseBuilder(HttpStatus.OK).body("O CPF " + cpf + " é válido!").build();
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("O CPF " + cpf + " é inválido").build();
        }
        
    }
    private boolean isValidCPF(String cpf) {
        if (cpf == null) return false;

        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) return false;

        // Cálculo do primeiro dígito verificador
        int sum = 0, weight = 10;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * weight--;
        }
        int firstDigit = (sum * 10) % 11;
        if (firstDigit == 10) firstDigit = 0;

        // Cálculo do segundo dígito verificador
        sum = 0;
        weight = 11;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * weight--;
        }
        int secondDigit = (sum * 10) % 11;
        if (secondDigit == 10) secondDigit = 0;

        // Verifica se os dígitos verificadores estão corretos
        return firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0');
    }
}
