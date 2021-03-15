package no.digdir.minidnotificationserver.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@ControllerAdvice
@Slf4j
public class SecurityExceptionHandler implements SecurityAdviceTrait {

}