# minid-notification-server 
Tjeneste til bruk for MinID app PoC.

## Tjenesten har en egen db 
- tilby registrering av kobling mellom fødselsnr og device-id
- oppslag for å finne kobling mellom fødselsnr og device-id
- deaktivering av kobling mellom fødselsnr og device-id

## Tjenesten har en Firebase SDK (TODO: link)
- Tilbyr tjeneste som slår opp rett device-id og sender en Firebase Cloud Message til devicen

## Tjenesten kalles bare internt, fra minid-pluss

## Tjenesten integrerer mot idporten? maskinporten?
Formål: Hente ut et eget token for å sende til app'en, så responsen kan autentiseres. 

## Tilgangskontroll:
- Hva slags tilgangskontroll skal vi ha?

## TODO:
- Skal vi tilby tjeneste som generer QR-kode for registrering på app? Eller skal det gjøres noe annet sted?
 
## API doc
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
