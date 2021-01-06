#params.pp
class minid_notification_server::params {
  $java_home                        = hiera('platform::java_home')
  $log_root                         = '/var/log/'
  $log_level                        = 'WARN'
  $install_dir                      = '/opt/'
  $config_dir                       = '/etc/opt/'
  $group_id                         = 'no.idporten'
  $artifact_id                      = 'minid-notification-server'
  $service_name                     = 'minid-notification-server'
  $server_port                      = ''
  $application                      = 'minid-notification-server'
  $server_tomcat_max_threads        = 200
  $server_tomcat_min_spare_threads  = 10
  $health_show_details              = 'always'
  $auditlog_dir                     = '/var/log/minid-notification-server/audit/'
  $auditlog_file                    = 'audit.log'
  $tomcat_tmp_dir                   = '/opt/minid-notification-server/tmp'
  $token_lifetime_seconds           = 600
  $code_lifetime_seconds            = 6
  $eventlog_jms_queuename           = hiera('idporten_logwriter::jms_queueName')
  $eventlog_jms_url                 = hiera('platform::jms_url')
  $session_ttl_seconds              = 1800
  $idporten_oidc_issuer_uri         = hiera('minid_notification_server::idporten_oidc_issuer_uri')
  $database_url                     = hiera('minid_notification_server::minid_notification_db_url')
  $database_username                = hiera('minid_notification_server::minid_notification_db_username')
  $database_password                = hiera('minid_notification_server::minid_notification_db_password')
  $swagger_client_id                = hiera('minid_notification_server::minid_notification_swagger_client_id')
  $swagger_client_secret            = hiera('minid_notification_server::minid_notification_swagger_client_secret')

}
