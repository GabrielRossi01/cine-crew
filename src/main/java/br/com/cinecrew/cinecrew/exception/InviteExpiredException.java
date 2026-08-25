package br.com.cinecrew.cinecrew.exception;

import org.springframework.http.HttpStatus;

public class InviteExpiredException extends ApiException {

    public InviteExpiredException() {
        super("Este link de convite expirou ou é inválido", HttpStatus.GONE);
    }
}
