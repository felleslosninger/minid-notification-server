package no.digdir.minidnotificationserver.api;

import com.google.common.base.Strings;
import com.vdurmont.semver4j.Semver;
import lombok.RequiredArgsConstructor;
import no.digdir.minidnotificationserver.config.ConfigProvider;
import no.digdir.minidnotificationserver.exceptions.AppVersionProblem;
import no.digdir.minidnotificationserver.exceptions.ErrorConstants;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidateVersionHeadersAspect {


    private final HttpServletRequest request;
    private final ConfigProvider configProvider;

    public static final String MINID_APP_OS_HEADER = "X-MinID-App-OS";
    public static final String MINID_APP_VERSION_HEADER = "X-MinID-App-Version";

    //Aspect can be placed on class or method
    @Before("within(@no.digdir.minidnotificationserver.api.ValidateVersionHeaders *) || @annotation(no.digdir.minidnotificationserver.api.ValidateVersionHeaders)")
    public void validateAspect(JoinPoint joinPoint) throws Throwable {


        String os = request.getHeader(MINID_APP_OS_HEADER);
        String version = request.getHeader(MINID_APP_VERSION_HEADER);

        if(Strings.isNullOrEmpty(os) || Strings.isNullOrEmpty(version)) {
            String mesg = String.format("Headers '%s' and '%s' must be supplied.", MINID_APP_OS_HEADER, MINID_APP_VERSION_HEADER);
            throw new AppVersionProblem(mesg, ErrorConstants.APP_VERSION_TYPE);
        }

        // Semantic Version using https://github.com/vdurmont/semver4j

        Semver appVersion = new Semver(version, Semver.SemverType.LOOSE);
        Semver requiredVersion;
        if("android".equalsIgnoreCase(os)) {
            requiredVersion = new Semver(configProvider.getAppVersions().getAndroid().getRequired(), Semver.SemverType.LOOSE);
        } else if ("ios".equalsIgnoreCase(os)) {
            requiredVersion = new Semver(configProvider.getAppVersions().getIos().getRequired(), Semver.SemverType.LOOSE);
        } else {
            String mesg = String.format("Header '%s' must be 'Android' or 'iOS'.", MINID_APP_OS_HEADER);
            throw new AppVersionProblem(mesg, ErrorConstants.APP_VERSION_TYPE);
        }

        boolean hasRequiredVersionOrBetter = requiredVersion.compareTo(appVersion) <= 0;
        if(!hasRequiredVersionOrBetter) {
            String mesg = String.format("Minimum required version of application is '%s'.", requiredVersion.toString());
            throw new AppVersionProblem(mesg, ErrorConstants.APP_REQUIRED_VERSION_TYPE);
        }

    }

}